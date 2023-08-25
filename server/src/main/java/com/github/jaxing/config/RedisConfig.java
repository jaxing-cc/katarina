package com.github.jaxing.config;

import com.github.jaxing.common.utils.VertxHolder;
import com.github.jaxing.utils.ConfigUtils;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cjxin
 * @date 2023/08/25
 */
@Slf4j
@Configuration
public class RedisConfig {

    @Bean
    public RedisAPI redisClient() {
        Redis client = Redis.createClient(VertxHolder.getVertx(), new RedisOptions()
                .setConnectionString(ConfigUtils.get("redis.url"))
        );
        client.connect().onSuccess(conn -> log.info("redis connect success")).onFailure(err -> log.error("redis connect fail", err));
        return RedisAPI.api(client);
    }
}
