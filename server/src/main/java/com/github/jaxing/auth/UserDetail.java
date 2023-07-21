package com.github.jaxing.auth;

import com.github.jaxing.common.domain.UserInfo;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.impl.UserImpl;
import lombok.Data;

import java.io.Serializable;

/**
 * @author cjxin
 * @date 2023/06/13
 */
@Data
public class UserDetail extends UserImpl implements Serializable, User {

    private UserInfo userInfo;

    public UserDetail(UserInfo userInfo) {
        super(JsonObject.mapFrom(userInfo), JsonObject.of(
                "uid", userInfo.getId(),
                "username", userInfo.getUsername(),
                "roles", userInfo.getRoles()));
        this.userInfo = userInfo;
    }
}
