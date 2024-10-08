package com.github.jaxing.service;


import com.github.jaxing.common.domain.Post;
import com.github.jaxing.common.domain.UserInfo;
import com.github.jaxing.common.dto.post.PostSaveDTO;
import com.github.jaxing.common.dto.post.PostUpdateDTO;
import com.github.jaxing.common.dto.post.PostVO;
import com.github.jaxing.common.enums.BusinessObjectEnum;
import com.github.jaxing.common.enums.CollectionEnum;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.jaxing.common.Constant.DEFAULT_PAGE_SIZE;

/**
 * @author cjxin
 * @date 2023/09/04
 */
@Component
public class PostServiceImpl implements PostService {


    @Resource
    private MongoClient mongoClient;

    @Resource
    private UserService userService;

    @Resource
    private ThumbupService thumbupService;

    @Resource
    private CommentService commentService;

    /**
     * 查询动态详情
     *
     * @param id
     * @param uid
     * @return 动态
     */
    @Override
    public Future<PostVO> findById(String id, String uid) {
        Promise<PostVO> promise = Promise.promise();
        mongoClient.findOne(CollectionEnum.post.name(), JsonObject.of("_id", id), JsonObject.of()).onFailure(promise::fail).onSuccess(json -> {
            PostVO postVO = new PostVO(json);
            postVO.setId(id);
            Set<String> postIds = new HashSet<>(Collections.singletonList(id));
            String code = BusinessObjectEnum.POST.getCode();
            CompositeFuture.all(
                    thumbupService.info(postIds, code),
                    thumbupService.liked(postIds, code, uid),
                    userService.findById(postVO.getUid()),
                    commentService.queryCommentSize(postIds, BusinessObjectEnum.POST.getCode())
            ).onFailure(promise::fail).onSuccess(cf -> {
                Map<String, Integer> map = cf.resultAt(0);
                Set<String> set = cf.resultAt(1);
                UserInfo userInfo = cf.resultAt(2);
                Map<String, Integer> commentSizeMap = cf.resultAt(3);
                postVO.setThumbupCount(map.get(postVO.getId()));
                postVO.setThumbuped(set.contains(postVO.getId()));
                postVO.setUser(userInfo);
                postVO.setCommentCount(commentSizeMap.getOrDefault(postVO.getId(), 0));
                promise.complete(postVO);
            });
        });
        return promise.future();
    }

    @Override
    public Future<List<PostVO>> search(String uid, String value, Boolean follow, Integer page) {
        JsonObject match = JsonObject.of("state", 1);
        if (!ObjectUtils.isEmpty(value)) {
            match.put("content", JsonObject.of("$regex", value));
        }
        if (Boolean.TRUE.equals(follow)) {
            Promise<List<PostVO>> promise = Promise.promise();
            userService.followList(uid).onFailure(promise::fail).onSuccess(res -> {
                if (!ObjectUtils.isEmpty(res)) {
                    JsonArray array = new JsonArray();
                    res.forEach(array::add);
                    match.put("uid", JsonObject.of("$in", array));
                }
                search(uid, match, page).onFailure(promise::fail).onSuccess(promise::complete);
            });
            return promise.future();
        } else {
            return search(uid, match, page);
        }
    }

    private Future<List<PostVO>> search(String uid, JsonObject match, Integer page) {
        if (page == null) {
            page = 1;
        }
        Promise<List<PostVO>> promise = Promise.promise();
        JsonObject projectField = JsonObject.of(
                "uid", 1,
                "title", 1,
                "images", 1,
                "content", 1,
                "cover", 1,
                "state", 1,
                "createTime", 1,
                "updateTime", 1
        );
        JsonArray pipeline = JsonArray.of(
                JsonObject.of("$match", match),
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
                JsonObject.of("$sort", JsonObject.of("createTime", -1)),
                JsonObject.of("$limit", DEFAULT_PAGE_SIZE),
                JsonObject.of("$skip", (page - 1) * DEFAULT_PAGE_SIZE)
        );
        List<PostVO> list = new ArrayList<>();
        mongoClient.aggregate(CollectionEnum.post.name(), pipeline).exceptionHandler(promise::fail).endHandler(v -> {
            Set<String> postIds = list.stream().map(PostVO::getId).collect(Collectors.toSet());
            String business = BusinessObjectEnum.POST.getCode();
            CompositeFuture.all(
                    thumbupService.info(postIds, business),
                    thumbupService.liked(postIds, business, uid),
                    commentService.queryCommentSize(postIds, business)
            ).onFailure(t -> promise.complete(list)).onSuccess(cf -> {
                Map<String, Integer> map = cf.resultAt(0);
                Set<String> set = cf.resultAt(1);
                Map<String, Integer> commentSizeMap = cf.resultAt(2);
                for (PostVO postVO : list) {
                    String id = postVO.getId();
                    postVO.setThumbupCount(map.getOrDefault(id, null));
                    postVO.setThumbuped(set.contains(postVO.getId()));
                    postVO.setCommentCount(commentSizeMap.getOrDefault(id, null));
                }
                promise.complete(list);
            });

        }).handler(jsonObject -> list.add(new PostVO(jsonObject)));
        return promise.future();
    }

    @Override
    public Future<String> save(PostSaveDTO saveDTO, String uid) {
        Promise<String> promise = Promise.promise();
        Post post = new Post();
        BeanUtils.copyProperties(saveDTO, post);
        post.setUid(uid);
        post.setCreateTime(new Date());
        if (!post.getMarkdown()) {
            post.setState(Post.STATE_PUBLISH);
            if (StringUtils.isEmpty(post.getTitle())) {
                promise.fail("参数错误");
            }
        } else {
            post.setState(Post.STATE_PREPARE);
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
