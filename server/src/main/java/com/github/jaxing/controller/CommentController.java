package com.github.jaxing.controller;

import com.github.jaxing.common.domain.R;
import com.github.jaxing.service.CommentService;
import com.github.jaxing.utils.HttpRegister;
import com.github.jaxing.utils.ValidationUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

import static io.vertx.json.schema.common.dsl.Schemas.objectSchema;
import static io.vertx.json.schema.common.dsl.Schemas.stringSchema;

@Controller
@Slf4j
public class CommentController extends HttpRegister {

    @Resource
    private CommentService commentService;

    @Resource
    private ValidationUtils validationUtils;

    @Override
    protected void start(Router router, Vertx vertx) {
        // 评论查询
        router.get("/api/comment/:type/:targetId").handler(context -> {
            commentService.query(
                    Integer.valueOf(context.request().getParam("pageNum")),
                    context.user().principal().getString("uid"),
                    context.request().getParam("replyId"),
                    context.pathParam("targetId"),
                    context.pathParam("type")
            ).onFailure(t -> context.json(R.fail(t))).onSuccess(t -> context.json(R.ok(t)));
        });

        //评论
        router.post("/api/comment").handler(validationUtils.validationJson(objectSchema()
                .property("content", validationUtils.get("chatContent"))
                .property("targetId", validationUtils.get("oid"))
                .property("replyId", stringSchema())
                .property("type", stringSchema())
        )).handler(context -> {
            JsonObject body = context.body().asJsonObject();
            commentService.save(
                    context.user().principal().getString("uid"),
                    body.getString("replyId"),
                    body.getString("content"),
                    body.getString("targetId"),
                    body.getString("type")
            ).onFailure(t -> context.json(R.fail(t))).onSuccess(t -> context.json(R.ok()));
        });
    }
}
