package com.github.jaxing.controller;

import com.github.jaxing.auth.UsernameAndPasswordProvider;
import com.github.jaxing.common.Constant;
import com.github.jaxing.common.domain.R;
import com.github.jaxing.utils.ConfigUtils;
import com.github.jaxing.utils.HttpRegister;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.authorization.Authorizations;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.authorization.JWTAuthorization;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * @author cjxin
 * @date 2023/06/13
 */
@Controller
@Slf4j
public class AuthController extends HttpRegister {

    @Resource
    private UsernameAndPasswordProvider passwordProvider;

    @Resource
    private JWTAuth jwtAuth;

    @Resource
    private JWTAuthorization jwtAuthorization;

    @Override
    public void start(Router router) {
        //最先进行的拦截请求
        router.route("/api/*").handler(JWTAuthHandler.create(jwtAuth)).failureHandler(context -> {
            HttpServerResponse response = context.response();
            response.end(R.fail(context.failure()));
        });

        router.get("/api/test").handler(res -> {
            HttpServerResponse response = res.response();
            response.end(R.ok(null));
        });

        /**
         * 登录
         */
        router.post("/auth/login").handler(http -> {
            HttpServerResponse response = http.response();
            JsonObject credentials = http.body().asJsonObject();
            passwordProvider
                    .authenticate(new UsernamePasswordCredentials(credentials.getString("username"), credentials.getString("password")))
                    .onComplete(authResp -> {
                        if (authResp.succeeded()) {
                            User user = authResp.result();
                            response.end(R.ok(jwtAuth.generateToken(user.attributes(), new JWTOptions().setExpiresInSeconds(ConfigUtils.getAsInteger("jwt.timeout")))));
                        } else {
                            response.end(R.fail(authResp.cause()));
                        }
                    });
        });
    }
}
