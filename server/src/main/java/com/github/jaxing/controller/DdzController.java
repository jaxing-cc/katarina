package com.github.jaxing.controller;

import com.github.jaxing.common.domain.R;
import com.github.jaxing.common.dto.PokerPopDTO;
import com.github.jaxing.service.DdzService;
import com.github.jaxing.utils.CommonUtils;
import com.github.jaxing.utils.HttpRegister;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author cjxin
 * @date 2024/04/16
 */
@Component
@Slf4j
public class DdzController extends HttpRegister {

    @Resource
    private DdzService ddzService;

    @Override
    protected void start(Router router, Vertx vertx) {

        /**
         * 房间列表
         */
        router.get("/api/game/ddz/room").handler(context ->
                ddzService.roomList(context.request().getParam("name"))
                        .onFailure(t -> context.json(R.fail(t)))
                        .onSuccess(list -> context.json(R.ok(list)))
        );

        /**
         * 创建房间
         */
        router.post("/api/game/ddz/room/:name").handler(context ->
                ddzService.createRoomAndJoin(CommonUtils.getUid(context), context.pathParam("name"))
                        .onFailure(t -> context.json(R.fail(t)))
                        .onSuccess(list -> context.json(R.ok(list)))
        );

        /**
         * 加入房间
         */
        router.put("/api/game/ddz/room/:id").handler(context ->
                ddzService.joinRoom(CommonUtils.getUid(context), context.pathParam("id"))
                        .onFailure(t -> context.json(R.fail(t)))
                        .onSuccess(list -> context.json(R.ok(list)))
        );

        /**
         * 退出房间
         */
        router.delete("/api/game/ddz/room").handler(context ->
                ddzService.exit(CommonUtils.getUid(context))
                        .onFailure(t -> context.json(R.fail(t)))
                        .onSuccess(list -> context.json(R.ok(list)))
        );

        /**
         * 准备
         */
        router.post("/api/game/ddz/ready").handler(context ->
                ddzService.ready(CommonUtils.getUid(context))
                        .onFailure(t -> context.json(R.fail(t)))
                        .onSuccess(v -> context.json(R.ok()))
        );

        /**
         * 叫牌
         */
        router.post("/api/game/ddz/call/:value").handler(context ->
                ddzService.callMaster(CommonUtils.getUid(context),
                        Integer.valueOf(context.pathParam("value")))
                        .onFailure(t -> context.json(R.fail(t)))
                        .onSuccess(v -> context.json(R.ok()))
        );

        /**
         * 叫牌
         */
        router.post("/api/game/ddz/pop").handler(context ->
                ddzService.pop(CommonUtils.getUid(context),
                        context.body().asJsonObject().mapTo(PokerPopDTO.class).getIds()
                ).onFailure(t -> context.json(R.fail(t))).onSuccess(v -> context.json(R.ok()))
        );

        /**
         * 叫牌
         */
        router.post("/api/game/ddz/restart").handler(context ->
                ddzService.restart(CommonUtils.getUid(context))
                        .onFailure(t -> context.json(R.fail(t)))
                        .onSuccess(v -> context.json(R.ok()))
        );

    }
}
