package com.github.jaxing.service;

import com.github.jaxing.common.domain.Client;
import com.github.jaxing.common.domain.UserInfo;
import com.github.jaxing.common.domain.UserRole;
import com.github.jaxing.common.dto.RegisterRequestDTO;
import com.github.jaxing.common.enums.CollectionEnum;
import com.github.jaxing.common.enums.RoleEnum;
import com.github.jaxing.common.utils.SecurityUtils;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author cjxin
 * @date 2023/06/13
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private MongoClient mongoClient;

    @Resource
    private FileService fileService;

    public void init() {
        UserRole root = new UserRole(ObjectId.get().toHexString(), RoleEnum.ROOT.getCode(), "管理员", "管理员");
        UserRole normal = new UserRole(ObjectId.get().toHexString(), RoleEnum.NORMAL.getCode(), "普通用户", "普通用户");
        UserInfo userInfo = new UserInfo();
        userInfo.setId(ObjectId.get().toHexString());
        userInfo.setName("张三");
        userInfo.setUsername("admin");
        userInfo.setPassword(SecurityUtils.encode("mimashi123"));
        userInfo.setRoles(Arrays.asList(root.getRoleKey(), normal.getRoleKey()));
        mongoClient.insert(CollectionEnum.role.name(), JsonObject.mapFrom(root)).onSuccess(id1 -> {
            log.info(id1);
            mongoClient.insert(CollectionEnum.role.name(), JsonObject.mapFrom(normal)).onSuccess(id2 -> {
                log.info(id2);
                mongoClient.insert(CollectionEnum.user.name(), JsonObject.mapFrom(userInfo)).onSuccess(log::info);
            });
        });
    }

    @Override
    public Future<Void> register(RegisterRequestDTO requestDTO) {
        Promise<Void> promise = Promise.promise();
        JsonObject query = JsonObject.of("$or", JsonArray.of(
                JsonObject.of("username", requestDTO.getUsername()),
                JsonObject.of("name", requestDTO.getName())
        ));
        mongoClient.count(CollectionEnum.user.name(), query).onSuccess(num -> {
            if (num == 0) {
                UserInfo userInfo = new UserInfo();
                userInfo.setId(ObjectId.get().toHexString());
                userInfo.setUsername(requestDTO.getUsername());
                userInfo.setPassword(SecurityUtils.encode(requestDTO.getPassword()));
                userInfo.setName(requestDTO.getName());
                userInfo.setGender(requestDTO.getGender());
                userInfo.setRoles(Collections.singletonList(RoleEnum.NORMAL.getCode()));
                mongoClient.insert(CollectionEnum.user.name(), JsonObject.mapFrom(userInfo))
                        .onSuccess(id -> promise.complete())
                        .onFailure(promise::fail);
            } else {
                promise.fail("username或昵称重复");
            }
        }).onFailure(promise::fail);
        return promise.future();
    }

    /**
     * 查询用户信息
     *
     * @param uid 用户id
     * @return 用户信息
     */
    @Override
    public Future<UserInfo> findById(String uid) {
        return mongoClient.findOne(CollectionEnum.user.name(), JsonObject.of("_id", uid), JsonObject.of("password", 0))
                .map(json -> {
                    if (ObjectUtils.isEmpty(json)) {
                        return null;
                    }
                    UserInfo userInfo = json.mapTo(UserInfo.class);
                    userInfo.setOnline(Client.CLIENT_POOL.containsKey(userInfo.getId()));
                    return userInfo;
                });
    }

    /**
     * 搜索用户
     *
     * @param key 用户名/名字
     * @return 用户信息
     */
    @Override
    public Future<List<UserInfo>> searchUser(String key) {
        Promise<List<UserInfo>> promise = Promise.promise();
        JsonObject $regex = JsonObject.of("$regex", key);
        JsonObject query = JsonObject.of("$or", JsonArray.of(JsonObject.of("username", $regex), JsonObject.of("name", $regex)));
        mongoClient.find(CollectionEnum.user.name(), query, async -> {
                    if (async.succeeded()) {
                        promise.complete(async.result().stream().map(json -> {
                            UserInfo userInfo = json.mapTo(UserInfo.class);
                            userInfo.setPassword(null);
                            userInfo.setRoles(null);
                            userInfo.setOnline(Client.CLIENT_POOL.containsKey(userInfo.getId()));
                            return userInfo;
                        }).collect(Collectors.toList()));
                    } else {
                        promise.fail(async.cause());
                    }
                }
        );
        return promise.future();
    }

    /**
     * 关注/取关
     *
     * @param uid       用户id
     * @param targetUid 用户id
     * @param action    关注/取关/拉黑/取消拉黑
     * @return 结果
     */
    @Override
    public Future<Void> follow(String uid, String targetUid, String action) {
        //TODO
        return null;
    }

    /**
     * 更新用户信息
     *
     * @param userInfo 用户信息
     */
    @Override
    public Future<Void> update(UserInfo userInfo) {
        Promise<Void> promise = Promise.promise();
        JsonObject update = JsonObject.of();
        if (!ObjectUtils.isEmpty(userInfo.getAvatar())){
            update.put("avatar",userInfo.getAvatar());
        }
        if (!ObjectUtils.isEmpty(userInfo.getName())){
            update.put("name",userInfo.getName());
        }
        if (!ObjectUtils.isEmpty(userInfo.getEmail())){
            update.put("email",userInfo.getEmail());
        }
        if (!ObjectUtils.isEmpty(userInfo.getGender())){
            update.put("gender",userInfo.getGender());
        }
        mongoClient.findOneAndUpdate(CollectionEnum.user.name(), JsonObject.of("_id", userInfo.getId()), JsonObject.of("$set", update))
                .onFailure(promise::fail).onSuccess(event -> {
            String oldAvatar = event.getString("avatar");
            String newAvatar = userInfo.getAvatar();
            if (!ObjectUtils.isEmpty(oldAvatar) && !ObjectUtils.isEmpty(newAvatar) && !oldAvatar.equals(newAvatar)) {
                // 删除旧头像
                fileService.delete(oldAvatar);
            }
            promise.complete();
        });
        return promise.future();
    }
}
