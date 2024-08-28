package com.github.jaxing.service;

import com.github.jaxing.common.Constant;
import com.github.jaxing.common.domain.Comment;
import com.github.jaxing.common.dto.CommentVO;
import com.github.jaxing.common.enums.BusinessObjectEnum;
import com.github.jaxing.common.enums.CollectionEnum;
import com.github.jaxing.common.enums.YesOrNoEnum;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.jaxing.common.Constant.BASE_ID;
import static com.github.jaxing.common.Constant.DEFAULT_PAGE_SIZE;

@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private MongoClient mongoClient;

    @Override
    public Future<Void> save(String uid, String replyId, String content, String targetId, String type) {
        Promise<Void> promise = Promise.promise();
        BusinessObjectEnum businessObjectEnum = BusinessObjectEnum.get(type);
        if (businessObjectEnum == null) {
            promise.fail("参数错误");
        }
        Comment comment = new Comment();
        comment.setUid(uid);
        comment.setReplyId(ObjectUtils.isEmpty(replyId) ? BASE_ID : replyId);
        comment.setContent(content);
        comment.setTargetId(targetId);
        comment.setTargetType(type);
        comment.setCreateTime(new Date().getTime());
        comment.setDeleted(false);
        JsonObject document = JsonObject.mapFrom(comment);
        document.remove("_id");
        mongoClient.save(CollectionEnum.comment.name(), document)
                .onFailure(promise::fail)
                .onSuccess(s -> promise.complete());
        return promise.future();
    }

    @Override
    public Future<List<CommentVO>> query(Integer pageNum, String uid, String replyId, String targetId, String type) {
        Promise<List<CommentVO>> promise = Promise.promise();
        JsonObject projectField = JsonObject.of(
                "_id", 1,
                "replyId", 1,
                "content", 1,
                "uid", 1,
                "deleted", 1,
                "createTime", 1
        );
        if (ObjectUtils.isEmpty(replyId)) {
            replyId = BASE_ID;
        }
        JsonArray match = JsonArray.of(
                JsonObject.of("replyId", replyId),
                JsonObject.of("targetId", targetId),
                JsonObject.of("targetType", type)
        );

        JsonArray pipeline = JsonArray.of(
                JsonObject.of("$match", JsonObject.of("$and", match)),
                JsonObject.of("$project", projectField.copy().put("uid", JsonObject.of("$toObjectId", "$uid"))),
                JsonObject.of("$lookup", JsonObject.of(
                        "from", "user",
                        "localField", "uid",
                        "foreignField", "_id",
                        "as", "user")
                ),
                JsonObject.of("$project", projectField.copy()
                        .put("id", JsonObject.of("$toString", "$_id"))
                        .put("uid", JsonObject.of("$toString", "$uid"))
                        .put("user", JsonObject.of("$arrayElemAt", JsonArray.of("$user", 0)))),
                JsonObject.of("$sort", JsonObject.of("createTime", 1)),
                JsonObject.of("$limit", DEFAULT_PAGE_SIZE),
                JsonObject.of("$skip", (pageNum - 1) * DEFAULT_PAGE_SIZE)
        );
        List<CommentVO> result = new ArrayList<>();
        mongoClient.aggregate(CollectionEnum.comment.name(), pipeline)
                .handler(o -> result.add(new CommentVO(o))).exceptionHandler(promise::fail)
                .endHandler(o -> queryChildSize(result.stream().map(CommentVO::getId).collect(Collectors.toSet()))
                        .onFailure(promise::fail).onSuccess(res -> {
                    for (CommentVO commentVO : result) {
                        commentVO.setChildSize(res.get(commentVO.getId()));
                    }
                    promise.complete(result);
                }));
        return promise.future();
    }

    /**
     * 查询评论id对应子评论数量
     */
    @Override
    public Future<Map<String, Integer>> queryChildSize(Collection<String> commentIds) {
        Promise<Map<String, Integer>> promise = Promise.promise();
        ;
        Map<String, Integer> map = new HashMap<>();
        JsonArray pipeline = JsonArray.of(
                JsonObject.of("$match", JsonObject.of("replyId", JsonObject.of("$in", commentIds))),
                JsonObject.of("$group", JsonObject.of("_id", "$replyId", "count", JsonObject.of("$sum", 1)))
        );
        mongoClient.aggregate(CollectionEnum.comment.name(), pipeline).exceptionHandler(promise::fail)
                .handler(j -> map.put(j.getString("_id"), j.getInteger("count")))
                .endHandler(res -> {
                    for (String commentId : commentIds) {
                        if (!map.containsKey(commentId)) {
                            map.put(commentId, 0);
                        }
                    }
                    promise.complete(map);
                });
        return promise.future();
    }
}
