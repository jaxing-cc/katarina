package com.github.jaxing;

import com.github.jaxing.utils.ConfigUtils;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * @author cjxin
 * @date 2023/06/13
 */
@Slf4j
public class Starter {
    public static void main(String[] args) {
        // 初始化配置文件
        try {
            ConfigUtils.init();
        } catch (IOException e) {
            log.error("初始化配置失败");
            return;
        }
        Vertx vertx = ConfigUtils.getVertx();
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.github.jaxing.**");
        vertx.deployVerticle(applicationContext.getBean(ServerVerticle.class), asyncResult -> {
            if (asyncResult.failed()) {
                asyncResult.cause().printStackTrace();
                log.error("start server fail:" + asyncResult.cause());
                vertx.close();
            }
        });
    }
}
