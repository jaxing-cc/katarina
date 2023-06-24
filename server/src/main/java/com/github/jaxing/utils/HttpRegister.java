package com.github.jaxing.utils;

import com.github.jaxing.common.domain.R;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cjxin
 * @date 2023/06/13
 */
public abstract class HttpRegister {

    private static Logger log = LoggerFactory.getLogger(HttpRegister.class);

    /**
     * 预制配置拦截器
     */
    private static final HttpRegister COMMON_HTTP_REGISTER = new HttpRegister() {
        @Override
        protected void start(Router r) {
            r.route("/*").order(0)
                    .handler(
                            CorsHandler.create().addOrigin("http://localhost").allowedMethods(new HashSet<>(Arrays.asList(
                                            HttpMethod.GET,
                                            HttpMethod.POST,
                                            HttpMethod.OPTIONS,
                                            HttpMethod.DELETE,
                                            HttpMethod.PATCH,
                                            HttpMethod.PUT
                                    ))))
                    .handler(BodyHandler.create())//参数处理
                    .handler(http -> {
                        http.response().setChunked(true)
                                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
//                                .putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,"*")
                        ;
                        http.next();
                    });
        }
    };

    protected abstract void start(Router router);

    public static void registerHttp(ApplicationContext context, Router router) {
        COMMON_HTTP_REGISTER.start(router);
        String[] beanNames = context.getBeanNamesForType(HttpRegister.class);
        for (String beanName : beanNames) {
            log.info("[{}] 已被注册", beanName);
            HttpRegister httpRegister = context.getBean(beanName, HttpRegister.class);
            httpRegister.start(router);
        }
    }

}
