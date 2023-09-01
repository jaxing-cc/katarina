package com.github.jaxing.controller;

import com.github.jaxing.common.domain.R;
import com.github.jaxing.utils.ConfigUtils;
import com.github.jaxing.utils.HttpRegister;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.ext.mongo.GridFsUploadOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cjxin
 * @date 2023/09/01
 */
@Component
public class FileController extends HttpRegister {

    @Resource
    private MongoClient mongoClient;

    //64f1b0ccbca80d366bb8b849
    @Override
    protected void start(Router router, Vertx vertx) {
        router.put("/api/file").handler(context -> {
            List<FileUpload> fileUploads = context.fileUploads();
            if (fileUploads.isEmpty()) {
                context.json(R.fail("上传内容为空"));
            }
            mongoClient.createGridFsBucketService(ConfigUtils.get("mongodb.fs.name")).onFailure(t -> context.json(R.fail(t))).onSuccess(client -> {
                FileUpload fileUpload = fileUploads.get(0);
                client.uploadFile(fileUpload.uploadedFileName())
                        .onFailure(t -> context.json(R.fail(t)))
                        .onSuccess(id -> context.json(R.ok(id)));
            });
        });

        router.get("/file/:key").handler(context -> {
            String key = context.pathParam("key");
            mongoClient.createGridFsBucketService(ConfigUtils.get("mongodb.fs.name")).onFailure(t -> context.json(R.fail(t))).onSuccess(client -> {
                client.downloadById(context.response(), key);
            });
        });

        router.delete("/file/:key").handler(context -> {
            String key = context.pathParam("key");
            mongoClient.createGridFsBucketService(ConfigUtils.get("mongodb.fs.name")).onFailure(t -> context.json(R.fail(t))).onSuccess(client -> {
                client.delete(key).onFailure(t -> context.json(R.fail(t))).onSuccess(v -> context.json(R.ok()));
            });
        });
    }
}
