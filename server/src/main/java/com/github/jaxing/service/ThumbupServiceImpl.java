package com.github.jaxing.service;

import com.github.jaxing.common.domain.ThumbupRecord;
import com.github.jaxing.common.enums.CollectionEnum;
import com.github.jaxing.config.RedisConfig;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.jaxing.common.Constant.DEFAULT_ZSET_CACHE_SIZE;

@Slf4j
@Service
public class ThumbupServiceImpl implements ThumbupService {

    @Resource
    private MongoClient mongoClient;

    @Resource
    private RedisAPI redis;

    @Override
    public Future<Void> like(String uid, String targetId, String business, boolean cancel) {
        Promise<Void> promise = Promise.promise();
        ThumbupRecord likeRecord = new ThumbupRecord();
        likeRecord.setUid(uid);
        likeRecord.setTargetId(targetId);
        likeRecord.setTargetType(business);
        likeRecord.setCreateTime(new Date().getTime());
        String key = String.format(RedisConfig.Key.ZSet.LIKE_ZSET, uid, business);
        if (cancel) {
            //减少db
            JsonObject query = JsonObject.of("$and", JsonArray.of(
                    JsonObject.of("uid", uid),
                    JsonObject.of("targetId", targetId),
                    JsonObject.of("targetType", business)
            ));
            mongoClient.removeDocument(CollectionEnum.thumbup_record.name(), query).onFailure(promise::fail).onSuccess(res1 -> {
                // 减少次数，删除列表数据
                redis.zrem(Arrays.asList(key, targetId)).onSuccess(res2 -> promise.complete()).onFailure(t -> log.error(t.getMessage()));
            });
        } else {
            // 是否已经点赞过
            liked(Arrays.asList(targetId), business, uid).onFailure(promise::fail).onSuccess(ids -> {
                if (ids.contains(targetId)) {
                    promise.fail("已经点赞过了!");
                } else {
                    ThumbupRecord thumbupRecord = new ThumbupRecord();
                    thumbupRecord.setUid(uid);
                    thumbupRecord.setTargetType(business);
                    thumbupRecord.setTargetId(targetId);
                    thumbupRecord.setCreateTime(new Date().getTime());
                    JsonObject document = JsonObject.mapFrom(thumbupRecord);
                    document.remove("_id");
                    mongoClient.save(CollectionEnum.thumbup_record.name(), document).onFailure(promise::fail).onSuccess(res1 -> {
                        // 查询数据
                        cacheToRedis(uid, business, Collections.singletonList(thumbupRecord)).onSuccess(res2 -> promise.complete())
                                .onFailure(t -> log.error(t.getMessage()));
                    });

                }
            });
        }
        return promise.future();
    }

    @Override
    public Future<Set<String>> liked(Collection<String> targetId, String business, String uid) {
        Promise<Set<String>> promise = Promise.promise();
        String key = String.format(RedisConfig.Key.ZSet.LIKE_ZSET, uid, business);
        // 获取用户所有点赞集合
        redis.zrange(Arrays.asList(key, "0", "-1")).onSuccess(redisResp -> {
            // 已经点赞过的列表
            Set<String> set = redisResp.stream().map(Response::toString).collect(Collectors.toSet());
            // 没点赞过的查db
            Set<String> resultSet = targetId.stream().filter(set::contains).collect(Collectors.toSet());
            Set<String> otherTargetId = targetId.stream().filter(t -> !set.contains(t)).collect(Collectors.toSet());
            if (ObjectUtils.isEmpty(otherTargetId)) {
                promise.complete(resultSet);
                return;
            }
            JsonArray objects = new JsonArray();
            otherTargetId.forEach(objects::add);
            JsonObject query = JsonObject.of("$and", JsonArray.of(
                    JsonObject.of("uid", uid),
                    JsonObject.of("targetId", JsonObject.of("$in", objects)),
                    JsonObject.of("targetType", business)
            ));
            mongoClient.find(CollectionEnum.thumbup_record.name(), query).onSuccess(res -> {
                if (res.isEmpty()) {
                    promise.complete(resultSet);
                } else {
                    List<ThumbupRecord> records = new ArrayList<>();
                    for (JsonObject j : res) {
                        resultSet.add(j.getString("targetId"));
                        records.add(j.mapTo(ThumbupRecord.class));
                    }
                    cacheToRedis(uid, business, records).onFailure(promise::fail).onSuccess(t -> promise.complete(resultSet));
                }
            }).onFailure(promise::fail);
        }).onFailure(promise::fail);
        return promise.future();
    }

    /**
     * 缓存数据
     *
     * @param uid      用户id
     * @param business 业务对象
     * @param records  业务id和操作时间
     */
    private Future<Void> cacheToRedis(String uid, String business, List<ThumbupRecord> records) {
        Promise<Void> promise = Promise.promise();
        String key = String.format(RedisConfig.Key.ZSet.LIKE_ZSET, uid, business);
        List<String> strings = new ArrayList<>();
        strings.add(key);
        for (ThumbupRecord record : records) {
            strings.add((record.getCreateTime() * -1) + "");
            strings.add(record.getTargetId());
        }
        redis.zadd(strings).onFailure(promise::fail).onSuccess(res -> {
            redis.zremrangebyrank(key, DEFAULT_ZSET_CACHE_SIZE + "", "-1").onSuccess(res2 -> promise.complete()).onFailure(promise::fail);
        });
        return promise.future();
    }

    @Override
    public Future<Map<String, Integer>> info(Set<String> targetIds, String business) {
        Promise<Map<String, Integer>> promise = Promise.promise();
        Map<String, Integer> map = new HashMap<>();
        JsonArray targetIdArray = new JsonArray();
        targetIds.forEach(targetIdArray::add);
        JsonObject query = JsonObject.of("$and", JsonArray.of(
                JsonObject.of("targetId", JsonObject.of("$in", targetIdArray)),
                JsonObject.of("targetType", business)
        ));
        JsonArray pipeline = JsonArray.of(
                JsonObject.of("$match", query),
                JsonObject.of("$group", JsonObject.of("_id", "$targetId", "count", JsonObject.of("$sum", 1)))
        );
        mongoClient.aggregate(CollectionEnum.thumbup_record.name(), pipeline).exceptionHandler(promise::fail)
                .handler(j -> map.put(j.getString("_id"), j.getInteger("count")))
                .endHandler(res -> {
                    for (String targetId : targetIds) {
                        if (!map.containsKey(targetId)) {
                            map.put(targetId, 0);
                        }
                    }
                    promise.complete(map);
                });
        return promise.future();
    }
}
