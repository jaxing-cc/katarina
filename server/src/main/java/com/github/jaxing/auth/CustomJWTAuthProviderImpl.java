package com.github.jaxing.auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.jwt.impl.JWTAuthProviderImpl;

/**
 * @author cjxin
 * @date 2023/06/14
 */
public class CustomJWTAuthProviderImpl extends JWTAuthProviderImpl {
    public CustomJWTAuthProviderImpl(Vertx vertx, JWTAuthOptions config) {
        super(vertx, config);
    }

    @Override
    public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> resultHandler) {
        this.customAuthenticate(this.authenticate(credentials)).onComplete(resultHandler);
    }

    /**
     * 处理redis会话
     *
     * @param userFuture 用户异步结果
     */
    public Future<User> customAuthenticate(Future<User> userFuture) {
        if (userFuture.succeeded()) {
            //TODO 如果jwt校验成功，检查redis存储
        }
        return userFuture;
    }
}
