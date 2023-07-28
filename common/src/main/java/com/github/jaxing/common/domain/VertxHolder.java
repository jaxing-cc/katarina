package com.github.jaxing.common.domain;

import io.vertx.core.Vertx;

/**
 * @author cjxin
 * @date 2023/07/26
 */
public class VertxHolder {

    private static final Vertx vertx = Vertx.vertx();

    public static Vertx getVertx() {
        return vertx;
    }
}
