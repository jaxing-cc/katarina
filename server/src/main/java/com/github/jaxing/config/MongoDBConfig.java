package com.github.jaxing.config;

import com.github.jaxing.common.utils.VertxHolder;
import com.github.jaxing.utils.ConfigUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoGridFsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cjxin
 * @date 2023/06/12
 */
@Configuration
public class MongoDBConfig {

    @Bean
    public MongoClient mongoClient() {
        Vertx vertx = VertxHolder.getVertx();
        JsonObject config = new JsonObject();
        config.put("connection_string", ConfigUtils.get("mongodb.url"));
        config.put("db_name", ConfigUtils.get("mongodb.db", "katarina"));
        config.put("username", ConfigUtils.get("mongodb.username"));
        config.put("password", ConfigUtils.get("mongodb.password"));
        config.put("useObjectId", true);
        return MongoClient.create(vertx, config);
    }
}
