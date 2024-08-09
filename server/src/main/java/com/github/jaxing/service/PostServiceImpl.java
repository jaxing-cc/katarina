package com.github.jaxing.service;


import com.github.jaxing.common.domain.Post;
import com.github.jaxing.common.dto.post.PostSaveDTO;
import com.github.jaxing.common.dto.post.PostUpdateDTO;
import com.github.jaxing.common.enums.CollectionEnum;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author cjxin
 * @date 2023/09/04
 */
@Component
public class PostServiceImpl implements PostService {

    @Resource
    private MongoClient mongoClient;

    @Override
    public Future<String> save(PostSaveDTO saveDTO, String uid) {
        Promise<String> promise = Promise.promise();
        Post post = new Post();
        BeanUtils.copyProperties(saveDTO, post);
        post.setUid(uid);
        post.setCreateTime(new Date());
        post.setState(Post.STATE_PREPARE);
        if (post.getMarkdown() && StringUtils.isEmpty(post.getTitle())) {
            promise.fail("参数错误");
        }
        JsonObject document = JsonObject.mapFrom(post);
        document.remove("_id");
        mongoClient.insert(CollectionEnum.post.name(), document)
                .onFailure(promise::fail)
                .onSuccess(id -> promise.complete(post.getId()));
        return promise.future();
    }

    @Override
    public Future<Void> update(PostUpdateDTO updateDTO) {
        Promise<Void> promise = Promise.promise();
        Post post = new Post();
        BeanUtils.copyProperties(updateDTO, post);
        JsonObject query = JsonObject.of("$and", JsonArray.of(
                JsonObject.of("_id", new JsonObject().put("$oid", updateDTO.getId())),
                JsonObject.of("state", Post.STATE_PREPARE))
        );
        JsonObject updateJson = new JsonObject();
        if (!ObjectUtils.isEmpty(updateDTO.getTitle())) {
            updateJson.put("title", updateDTO.getTitle());
        }
        if (!ObjectUtils.isEmpty(updateDTO.getCover())) {
            updateJson.put("cover", updateDTO.getCover());
        }
        if (!ObjectUtils.isEmpty(updateDTO.getImages())) {
            updateJson.put("images", updateDTO.getImages());
        }
        if (!ObjectUtils.isEmpty(updateDTO.getContent())) {
            updateJson.put("content", updateDTO.getContent());
        }
        if (Boolean.TRUE.equals(updateDTO.getPublish())) {
            updateJson.put("state", Post.STATE_PUBLISH);
        }
        updateJson.put("updateTime", new Date().getTime());
        mongoClient.findOneAndUpdate(CollectionEnum.post.name(), query, JsonObject.of("$set", updateJson))
                .onFailure(promise::fail)
                .onSuccess(js -> promise.complete());
        return promise.future();
    }
}
