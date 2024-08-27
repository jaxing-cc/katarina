package com.github.jaxing.controller;


import com.github.jaxing.common.domain.R;
import com.github.jaxing.service.ThumbupService;
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
public class ThumbupController extends HttpRegister {


    @Resource
    private ThumbupService thumbupService;

    @Resource
    private ValidationUtils validationUtils;

    @Override
    public void start(Router router, Vertx vertx) {

        router.post("/api/thumbup").handler(validationUtils.validationJson(objectSchema()
                .property("targetId", validationUtils.get("oid"))
                .property("type", stringSchema())
        )).handler(context -> {
            JsonObject body = context.body().asJsonObject();
            thumbupService.like(
                    context.user().principal().getString("uid"),
                    body.getString("targetId"),
                    body.getString("type")
            ).onFailure(t -> context.json(R.fail(t))).onSuccess(t -> context.json(R.ok(t)));
        });
    }
}




