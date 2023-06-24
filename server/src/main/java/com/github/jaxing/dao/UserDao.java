package com.github.jaxing.dao;

import com.github.jaxing.common.domain.UserInfo;
import com.github.jaxing.common.domain.UserRole;
import com.github.jaxing.common.enums.CollectionEnum;
import com.github.jaxing.common.enums.GenderEnum;
import com.github.jaxing.common.utils.SecurityUtils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author cjxin
 * @date 2023/06/13
 */
@Slf4j
@Repository
public class UserDao {

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
        mongoClient.insert(CollectionEnum.role.name(), JsonObject.mapFrom(root)).onSuccess( id1 -> {
            log.info(id1);
            mongoClient.insert(CollectionEnum.role.name(), JsonObject.mapFrom(normal)).onSuccess(id2 -> {
                log.info(id2);
                mongoClient.insert(CollectionEnum.user.name(), JsonObject.mapFrom(userInfo)).onSuccess(log::info);
            });
        });

    }
}
