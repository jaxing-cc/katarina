package com.github.jaxing.utils;


import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Bodies;
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.common.dsl.SchemaBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static io.vertx.json.schema.common.dsl.Keywords.maxLength;
import static io.vertx.json.schema.common.dsl.Keywords.minLength;
import static io.vertx.json.schema.common.dsl.Schemas.booleanSchema;
import static io.vertx.json.schema.common.dsl.Schemas.numberSchema;
import static io.vertx.json.schema.common.dsl.Schemas.stringSchema;
import static io.vertx.json.schema.draft7.dsl.Keywords.maximum;
import static io.vertx.json.schema.draft7.dsl.Keywords.minimum;

/**
 * @author cjxin
 * @date 2023/07/19
 */
@Component
public class ValidationUtils {

    public static final Map<String, SchemaBuilder> SCHEMA_BUILDER_MAP = new HashMap<>();

    static {
        //用户相关
        SCHEMA_BUILDER_MAP.put("username", stringSchema().with(minLength(3)).with(maxLength(10)));
        SCHEMA_BUILDER_MAP.put("password", stringSchema().with(minLength(6)).with(maxLength(20)));
        SCHEMA_BUILDER_MAP.put("name", stringSchema().with(minLength(1)).with(maxLength(10)));
        SCHEMA_BUILDER_MAP.put("gender", numberSchema().with(maximum(2)).with(minimum(0)));

        SCHEMA_BUILDER_MAP.put("chatContent", stringSchema().with(maxLength(500)).with(minLength(1)));
        SCHEMA_BUILDER_MAP.put("chatContentType", numberSchema().with(maximum(2)).with(minimum(0)));
        //通用
        SCHEMA_BUILDER_MAP.put("oid", stringSchema().with(maxLength(24)).with(minLength(24)));
        SCHEMA_BUILDER_MAP.put("boolean", booleanSchema());
    }

    @Resource
    private SchemaParser schemaParser;

    public ValidationHandler validationJson(SchemaBuilder schemaBuilder) {
        return ValidationHandlerBuilder.create(schemaParser)
                .body(Bodies.json(schemaBuilder))
                .build();
    }

    public SchemaBuilder get(String key) {
        return SCHEMA_BUILDER_MAP.get(key);
    }
}
