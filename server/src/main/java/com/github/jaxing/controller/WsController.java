package com.github.jaxing.controller;


import com.github.jaxing.common.Constant;
import com.github.jaxing.common.domain.R;
import com.github.jaxing.utils.HttpRegister;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class WsController extends HttpRegister {

    @Resource
    private JWTAuth jwtAuth;


    @Override
    protected void start(Router router) {
        router.route("/ws").handler(context -> {
            jwtAuth.authenticate(new TokenCredentials(context.request().getHeader(Constant.AUTHORIZATION))).onSuccess(user ->
                    context.request()
                            .toWebSocket()
                            .onFailure(t -> context.json(R.fail(t)))
                            .onSuccess(serverWebSocket -> {
                                serverWebSocket.handler(buffer -> {
                                    System.out.println(buffer);
                                });

                                serverWebSocket.closeHandler(voidResp -> {

                                });

                                serverWebSocket.exceptionHandler(throwable -> {
                                    log.error("WebSocket Error", throwable);
                                });
                            }))
                    .onFailure(event -> context.json(R.resp(false, "没有权限", null)));
        });
    }
}
