package com.github.jaxing.auth;

import com.github.jaxing.common.domain.UserInfo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.auth.authorization.Authorizations;
import lombok.Data;

import java.io.Serializable;
import java.util.stream.Collectors;

/**
 * @author cjxin
 * @date 2023/06/13
 */
@Data
public class UserDetail implements Serializable, User {

    private UserInfo userInfo;

    public void setUserInfo(UserInfo userInfo) {
        userInfo.setPassword(null);
        this.userInfo = userInfo;
    }

    @Override
    public JsonObject attributes() {
        return new JsonObject()
                .put("uid", userInfo.getId())
                .put("username", userInfo.getUsername())
                .put("role", userInfo.getRoles());
    }

    @Override
    public User isAuthorized(Authorization authority, Handler<AsyncResult<Boolean>> resultHandler) {
        return this;
    }

    @Override
    public JsonObject principal() {
        return JsonObject.mapFrom(userInfo);
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {
    }

    @Override
    public User merge(User other) {
        return this;
    }
}
