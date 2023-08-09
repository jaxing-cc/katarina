package com.github.jaxing.controller;


import com.github.jaxing.common.Constant;
import com.github.jaxing.common.domain.ChatMessage;
import com.github.jaxing.common.domain.Client;
import com.github.jaxing.common.domain.Message;
import com.github.jaxing.common.domain.R;
import com.github.jaxing.common.enums.MessageTypeEnum;
import com.github.jaxing.service.ChatService;
import com.github.jaxing.socket.WsHandler;
import com.github.jaxing.utils.HttpRegister;
import com.github.jaxing.utils.ValidationUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.validation.builder.Parameters;
import io.vertx.json.schema.draft7.dsl.Keywords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.stream.Collectors;

import static io.vertx.json.schema.common.dsl.Schemas.numberSchema;
import static io.vertx.json.schema.common.dsl.Schemas.objectSchema;

@Component
@Slf4j
public class ChatController extends HttpRegister {

    @Resource
    private JWTAuth jwtAuth;

    @Resource
    private WsHandler messageHandler;

    @Resource
    private ValidationUtils validationUtils;

    @Resource
    private ChatService chatService;

    @Override
    protected void start(Router router, Vertx vertx) {
        router.route("/ws").handler(context -> {
            jwtAuth.authenticate(new TokenCredentials(context.request().getParam(Constant.AUTHORIZATION))).onSuccess(user ->
                    context.request()
                            .toWebSocket()
                            .onFailure(t -> context.json(R.fail(t)))
                            .onSuccess(serverWebSocket -> {
                                JsonObject principal = user.principal();
                                Client client = Client.builder().binAddressId(serverWebSocket.binaryHandlerID()).textAddressId(serverWebSocket.textHandlerID())
                                        .uid(principal.getString("uid")).username(principal.getString("username"))
                                        .roles(principal.getJsonArray("roles").stream().map(r -> (String) r).collect(Collectors.toSet()))
                                        .build().addSelf();
                                log.info("size:{}, uid:{}",
                                        Client.CLIENT_POOL.size(),
                                        Client.CLIENT_POOL.values().stream().map(Client::getUid).collect(Collectors.joining("-"))
                                );
                                serverWebSocket.handler(buffer -> {
                                    Message message = null;
                                    try {
                                        message = new JsonObject(buffer.toString()).mapTo(Message.class);
                                    } catch (Exception e) {
                                        log.error("parse message error:{}", e.getMessage());
                                        vertx.eventBus().send(client.getTextAddressId(), MessageTypeEnum.FAIL.message("消息格式错误"));
                                        return;
                                    }
                                    messageHandler.handle(message, client);
                                });

                                serverWebSocket.closeHandler(voidResp -> {
                                    client.removeSelf();
                                });

                                serverWebSocket.exceptionHandler(throwable -> {
                                    log.error("WebSocket Error", throwable);
                                });
                            }))
                    .onFailure(event -> context.json(R.resp(false, "没有权限", null)));
        });

        /** 离线消息 **/

        router.get("/api/offline-msg/count").handler(context ->
                chatService.offlineMessageCountAndUpdateChatList(context.user().principal().getString("uid"), false)
                        .onSuccess(r -> context.json(R.ok(r)))
                        .onFailure(t -> context.json(R.fail(t)))
        );

        router.put("/api/offline-msg/:targetId").handler(context ->
                chatService.clearOfflineMessage(context.user().principal().getString("uid"), context.pathParam("targetId"), false)
                        .onSuccess(r -> context.json(R.ok()))
                        .onFailure(t -> context.json(R.fail(t)))
        );


        router.post("/api/msg/send").handler(validationUtils.validationJson(objectSchema()
                .property("to", validationUtils.get("oid"))
                .property("groupMessage", validationUtils.get("boolean"))
                .property("content", validationUtils.get("chatContent"))
                .property("contentType", validationUtils.get("chatContentType"))
                .property("createTime", validationUtils.get("number"))
        )).handler(context -> chatService.sendChatMessage(context.user(),
                context.body().asJsonObject().mapTo(ChatMessage.class))
                .onSuccess(r -> context.json(R.ok(null)))
                .onFailure(t -> context.json(R.fail(t)))
        );

        /** chatList相关功能 **/

        router.get("/api/chat-list/:page/:size")
                .handler(validationUtils.builder()
                        .pathParameter(Parameters.param("page", numberSchema().with(Keywords.minimum(1)).defaultValue(1)))
                        .pathParameter(Parameters.param("size", numberSchema().defaultValue(10)))
                        .build())
                .handler(
                        context -> chatService.chatList(context.user().principal().getString("uid"),
                                Integer.parseInt(context.pathParam("page")), Integer.parseInt(context.pathParam("size")))
                                .onSuccess(list -> context.json(R.ok(list)))
                                .onFailure(t -> {
                                    t.printStackTrace();
                                    context.json(R.fail(t));
                                })
                );

        router.post("/api/chat-list/:uid").handler(
                context -> chatService.addChatListItem(context.user().principal().getString("uid"),
                        context.pathParam("uid"))
                        .onSuccess(r -> context.json(R.ok(null)))
                        .onFailure(t -> context.json(R.fail(t)))
        );

        router.delete("/api/chat-list/:uid").handler(
                context -> chatService.deleteChatListItem(context.user().principal().getString("uid"),
                        context.pathParam("uid"))
                        .onSuccess(r -> context.json(R.ok(null)))
                        .onFailure(t -> context.json(R.fail(t)))
        );
    }
}
