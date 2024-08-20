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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

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
        String countKey = String.format(RedisConfig.Key.Str.LIKE_COUNT, targetId, business);
        if (cancel) {
            //减少db
            JsonObject query = JsonObject.of("and", JsonArray.of(
                    JsonObject.of("uid", uid),
                    JsonObject.of("targetId", targetId),
                    JsonObject.of("business", business)
            ));
            mongoClient.removeDocument(CollectionEnum.thumbup_record.name(), query).onFailure(promise::fail).onSuccess(res -> {
                // 减少次数，删除列表数据
                redis.decr(countKey).onFailure(t -> log.error(t.getMessage()));
                redis.zrem(Arrays.asList(key, targetId)).onFailure(t -> log.error(t.getMessage()));
                promise.complete();
            });
        } else {
            CompositeFuture.all(redis.incr(countKey), redis.zadd(Arrays.asList(key, likeRecord.getCreateTime().toString(), targetId)))
                    .onSuccess(res -> {
                        promise.complete();
                    }).onFailure(t -> log.error(t.getMessage()));

        }
        return promise.future();
    }

    @Override
    public Future<Set<String>> liked(Set<String> targetId, String businessId) {
        return null;
    }

    @Override
    public Future<Map<String, Integer>> info(Set<String> targetId, String businessId) {
        return null;
    }
}
