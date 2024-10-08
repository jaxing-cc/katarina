package com.github.jaxing.utils;

import com.github.jaxing.common.domain.R;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author cjxin
 * @date 2023/06/13
 */
public abstract class HttpRegister {

    public static final int MAX_BODY_SIZE = 1024 * 1024 * 3;

    private static Logger log = LoggerFactory.getLogger(HttpRegister.class);

    /**
     * 预制配置拦截器
     */
    private static final HttpRegister COMMON_HTTP_REGISTER = new HttpRegister() {
        @Override
        protected void start(Router r,Vertx vertx) {
            r.errorHandler(400, ctx -> ctx.json(R.resp(false, "服务器拒绝请求", null)));
            r.route("/*").order(0)
                    .handler(CorsHandler.create()
                                    .addOrigin("http://localhost")
                                    .addOrigin("http://101.42.249.167")
                                    .allowedMethods(new HashSet<>(Arrays.asList(
                                            HttpMethod.GET,
                                            HttpMethod.POST,
                                            HttpMethod.OPTIONS,
                                            HttpMethod.DELETE,
                                            HttpMethod.PATCH,
                                            HttpMethod.PUT
                                    ))))
                    .handler(BodyHandler.create().setUploadsDirectory(System.getProperty("java.io.tmpdir")).setBodyLimit(MAX_BODY_SIZE))
                    .handler(http -> {
                        http.response().setChunked(true);
                        http.next();
                    });
        }
    };

    protected abstract void start(Router router, Vertx vertx);

    public static void registerHttp(ApplicationContext context, Router router, Vertx vertx) {
        COMMON_HTTP_REGISTER.start(router, vertx);
        String[] beanNames = context.getBeanNamesForType(HttpRegister.class);
        for (String beanName : beanNames) {
            HttpRegister httpRegister = context.getBean(beanName, HttpRegister.class);
            httpRegister.start(router, vertx);
        }
    }

}
