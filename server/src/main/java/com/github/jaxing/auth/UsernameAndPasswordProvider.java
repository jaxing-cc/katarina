package com.github.jaxing.auth;

import com.github.jaxing.common.domain.UserInfo;
import com.github.jaxing.common.enums.CollectionEnum;
import com.github.jaxing.common.utils.SecurityUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.mongo.MongoClient;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * @author cjxin
 * @date 2023/06/13
 */
@Component
public class UsernameAndPasswordProvider implements AuthenticationProvider {

    @Resource
    public MongoClient mongoClient;

    @Override
    public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> resultHandler) {
        String username = credentials.getString("username");
        String password = credentials.getString("password");
        if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password)) {
            resultHandler.handle(Future.failedFuture("用户名或者密码错误"));
            return;
        }
        mongoClient.findOne(CollectionEnum.user.name(), new JsonObject().put("username", username), null)
                .onSuccess(u -> {
                    UserInfo user = null;
                    if (ObjectUtils.isEmpty(u)
                            || ObjectUtils.isEmpty(user = u.mapTo(UserInfo.class))
                            || !SecurityUtils.match(password, user.getPassword())) {
                        resultHandler.handle(Future.failedFuture("用户名或者密码错误"));
                        return;
                    }
                    user.setPassword(null);
                    resultHandler.handle(Future.succeededFuture(new UserDetail(user)));
                })
                .onFailure(err -> resultHandler.handle(Future.failedFuture("查找用户失败")));
    }
}
