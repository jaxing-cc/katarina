package com.github.jaxing.service;

import com.github.jaxing.common.domain.UserInfo;
import com.github.jaxing.common.domain.UserRole;
import com.github.jaxing.common.dto.RegisterRequestDTO;
import com.github.jaxing.common.enums.CollectionEnum;
import com.github.jaxing.common.utils.SecurityUtils;
import io.vertx.core.CompositeFuture;
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

/**
 * @author cjxin
 * @date 2023/06/13
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private MongoClient mongoClient;

    public void init() {
        UserRole root = new UserRole(ObjectId.get().toHexString(), "root", "管理员", "管理员");
        UserRole normal = new UserRole(ObjectId.get().toHexString(), "normal", "普通用户", "普通用户");
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
                userInfo.setRoles(Collections.singletonList("normal"));
                mongoClient.insert(CollectionEnum.user.name(), JsonObject.mapFrom(userInfo))
                        .onSuccess(id -> promise.complete())
                        .onFailure(promise::fail);
            } else {
                promise.fail("username或昵称重复");
            }
        }).onFailure(promise::fail);
        return promise.future();
    }
}
