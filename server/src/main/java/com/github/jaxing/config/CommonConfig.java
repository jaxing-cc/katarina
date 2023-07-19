package com.github.jaxing.config;

import com.github.jaxing.utils.ConfigUtils;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public SchemaParser schemaParser() {
        return SchemaParser.createDraft7SchemaParser(SchemaRouter.create(ConfigUtils.getVertx(),
                new SchemaRouterOptions()));
    }
}
