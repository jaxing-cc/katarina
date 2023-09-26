package com.github.jaxing.utils;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author cjxin
 * @date 2023/09/26
 */
@Component
@Slf4j
public class CacheUtils {

    public static final String EXPIRE = "3600";

    @Resource
    private RedisAPI redisAPI;

    /**
     * 缓存的获取数据
     *
     * @param redisKey       key
     * @param supplier       查询方法
     * @param convertFunc    结果转换方法
     * @param redisQueryFunc redis查询方法
     * @param redisSaveFunc  redis保存方法
     * @param <R>            结果类型
     * @return
     */
    public <R> Future<R> get(String redisKey,
                             Supplier<Future<R>> supplier,
                             Function<Response, R> convertFunc,
                             BiFunction<RedisAPI, String, Future<Response>> redisQueryFunc,
                             Function<R, Future<Object>> redisSaveFunc) {
        Promise<R> promise = Promise.promise();
        redisAPI.exists(Collections.singletonList(redisKey)).onSuccess(exist -> {
            if (exist.toBoolean()) {
                redisQueryFunc.apply(redisAPI, redisKey).onSuccess(res -> {
                    promise.complete(convertFunc.apply(res));
                }).onFailure(err -> {
                    log.error("query redis fail", err);
                    supplier.get().onFailure(promise::fail).onSuccess(promise::complete);
                });
            } else {
                supplier.get().onFailure(promise::fail).onSuccess(r ->
                        redisSaveFunc.apply(r).onFailure(promise::fail).onSuccess(v ->
                                redisAPI.expire(Arrays.asList(redisKey, EXPIRE)).onFailure(promise::fail).onSuccess(e -> promise.complete(r))
                        )
                );
            }
        }).onFailure(existsErr -> supplier.get().onFailure(promise::fail).onSuccess(promise::complete));
        return promise.future();
    }
}
