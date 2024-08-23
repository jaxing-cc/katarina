package com.github.jaxing.controller;

import com.github.jaxing.common.domain.R;
import com.github.jaxing.common.dto.post.PostSaveDTO;
import com.github.jaxing.common.dto.post.PostUpdateDTO;
import com.github.jaxing.service.PostService;
import com.github.jaxing.utils.HttpRegister;
import com.github.jaxing.utils.ValidationUtils;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

import static io.vertx.json.schema.common.dsl.Schemas.objectSchema;
import static io.vertx.json.schema.common.dsl.Schemas.stringSchema;

@Controller
@Slf4j
public class PostController extends HttpRegister {

    @Resource
    private PostService postService;

    @Resource
    private ValidationUtils validationUtils;

    @Override
    protected void start(Router router, Vertx vertx) {

        /**
         * 查询
         */
        router.get("/api/post").handler(context -> postService.search(context.user().principal().getString("uid"),
                context.request().getParam("keyword"),
                Integer.parseInt(context.request().getParam("page"))).onFailure(t -> context.json(R.fail(t))).onSuccess(list -> context.json(R.ok(list))));

        /**
         * 查询
         */
        router.get("/api/post/:id").handler(context -> postService.findById(context.pathParam("id"))
                .onFailure(t -> context.json(R.fail(t))).onSuccess(list -> context.json(R.ok(list))));

        /**
         * 创建
         */
        router.post("/api/post").handler(validationUtils.validationJson(objectSchema()
                .property("title", stringSchema())
                .property("cover", stringSchema())
                .property("content", validationUtils.get("postContent"))
                .property("markdown", validationUtils.get("boolean"))
                .property("images", validationUtils.get("array"))
        )).handler(context -> postService.save(context.body().asJsonObject().mapTo(PostSaveDTO.class),
                        context.user().principal().getString("uid"))
                .onFailure(t -> context.json(R.fail(t)))
                .onSuccess(u -> context.json(R.ok())));

        /**
         * 更新
         */
        router.put("/api/post").handler(validationUtils.validationJson(objectSchema()
                .property("_id", validationUtils.get("oid"))
                .property("title", stringSchema())
                .property("cover", stringSchema())
                .property("content", validationUtils.get("postContent"))
                .property("images", validationUtils.get("array"))
                .property("publish", validationUtils.get("boolean"))
        )).handler(context -> postService.update(context.body().asJsonObject().mapTo(PostUpdateDTO.class))
                .onFailure(t -> context.json(R.fail(t)))
                .onSuccess(u -> context.json(R.ok())));
    }
}
