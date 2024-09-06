package com.github.jaxing;

import com.github.jaxing.common.enums.BusinessObjectEnum;
import com.github.jaxing.job.DdzJob;
import com.github.jaxing.service.CommentService;
import com.github.jaxing.service.PostService;
import com.github.jaxing.service.ThumbupService;
import com.github.jaxing.utils.ConfigUtils;
import com.github.jaxing.utils.HttpRegister;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;


/**
 * @author cjxin
 * @date 2023/06/12
 */
@Slf4j
@Component
public class ServerVerticle extends AbstractVerticle implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Resource
    private DdzJob ddzJob;

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer(new HttpServerOptions().setRegisterWebSocketWriteHandlers(true));
        Router router = Router.router(vertx);
        vertx.setPeriodic(1000 , ddzJob);
        HttpRegister.registerHttp(applicationContext, router, vertx);
        server.requestHandler(router).listen(ConfigUtils.getAsInteger("server.port")).onComplete(httpServerAsyncResult -> {
            if (httpServerAsyncResult.succeeded()) {
                startPromise.complete();
            } else {
                startPromise.fail(httpServerAsyncResult.cause());
            }
        });
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
