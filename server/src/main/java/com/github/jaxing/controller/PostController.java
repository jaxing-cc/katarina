package com.github.jaxing.controller;

import com.github.jaxing.common.domain.R;
import com.github.jaxing.common.domain.UserInfo;
import com.github.jaxing.common.dto.post.PostSaveDTO;
import com.github.jaxing.service.PostService;
import com.github.jaxing.utils.HttpRegister;
import com.github.jaxing.utils.ValidationUtils;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

import static io.vertx.json.schema.common.dsl.Schemas.objectSchema;

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
         * 创建
         */
        router.post("/api/post").handler(validationUtils.validationJson(objectSchema()
                .property("markdown", validationUtils.get("boolean"))
        )).handler(context -> postService.save(context.body().asJsonObject().mapTo(PostSaveDTO.class),
                        context.user().principal().getString("uid"))
                .onFailure(t -> context.json(R.fail(t)))
                .onSuccess(u -> context.json(R.ok())));


        router.put("/api/post").handler(validationUtils.validationJson(objectSchema()
                .property("markdown", validationUtils.get("boolean"))
        )).handler(context -> {});
    }
}
