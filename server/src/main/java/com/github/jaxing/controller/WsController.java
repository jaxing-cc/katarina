package com.github.jaxing.controller;


import com.github.jaxing.utils.HttpRegister;
import io.vertx.ext.web.Router;
import org.springframework.stereotype.Component;

@Component
public class WsController extends HttpRegister {

    @Override
    protected void start(Router router) {
//        router.route("ws").handler(context -> {
//            context.request().toWebSocket().onSuccess(serverWebSocket -> {
//                serverWebSocket.closeHandler(voidResp -> {
//
//                });
//
//                serverWebSocket.exceptionHandler(throwable -> {
//
//                });
//            });
//
//        });
    }
}
