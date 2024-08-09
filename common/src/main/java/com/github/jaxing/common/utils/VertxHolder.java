package com.github.jaxing.common.utils;

import io.vertx.core.Vertx;
import lombok.Getter;

/**
 * @author cjxin
 * @date 2023/07/26
 */
public class VertxHolder {

    @Getter
    private static final Vertx vertx = Vertx.vertx();

}
