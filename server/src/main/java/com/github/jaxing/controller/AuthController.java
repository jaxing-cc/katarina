package com.github.jaxing.controller;

import com.github.jaxing.auth.UsernameAndPasswordProvider;
import com.github.jaxing.common.domain.R;
import com.github.jaxing.common.dto.RegisterRequestDTO;
import com.github.jaxing.service.UserService;
import com.github.jaxing.utils.ConfigUtils;
import com.github.jaxing.utils.HttpRegister;
import com.github.jaxing.utils.ValidationUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.json.schema.common.dsl.NumberKeyword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import static io.vertx.json.schema.common.dsl.Schemas.*;
import static io.vertx.json.schema.draft7.dsl.Keywords.*;

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
    private UserService userService;

    @Resource
    private ValidationUtils validationUtils;

    @Override
    public void start(Router router, Vertx vertx) {
        //最先进行的拦截请求
        router.route("/api/*")
                .handler(JWTAuthHandler.create(jwtAuth)).failureHandler(context -> {
            context.json(R.fail(context.failure()));
        });

        /**
         * 登录
         */
        router.post("/auth/login").handler(validationUtils.validationJson(objectSchema()
                .property("username", validationUtils.get("username"))
                .property("password", validationUtils.get("password"))
        )).handler(http -> {
            JsonObject credentials = http.body().asJsonObject();
            passwordProvider
                    .authenticate(new UsernamePasswordCredentials(credentials.getString("username"), credentials.getString("password")))
                    .onComplete(authResp -> {
                        if (authResp.succeeded()) {
                            User user = authResp.result();
                            http.json(R.ok(jwtAuth.generateToken(user.attributes(),
                                    new JWTOptions().setExpiresInSeconds(ConfigUtils.getAsInteger("jwt.timeout")))));
                        } else {
                            http.json(R.fail(authResp.cause()));
                        }
                    });
        });

        /**
         * 注册
         */
        router.post("/auth/register").handler(validationUtils.validationJson(objectSchema()
                .property("username", validationUtils.get("username"))
                .property("password", validationUtils.get("password"))
                .property("name", validationUtils.get("name"))
                .property("gender", validationUtils.get("gender"))
        )).handler(http -> {
            JsonObject body = http.body().asJsonObject();
            userService.register(body.mapTo(RegisterRequestDTO.class))
                    .onComplete(asyncResult -> {
                        if (asyncResult.succeeded()) {
                            http.json(R.ok(null));
                        } else {
                            http.json(R.fail(asyncResult.cause()));
                        }
                    });
        });
    }
}
