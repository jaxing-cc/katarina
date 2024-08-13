package com.github.jaxing.service;

import com.github.jaxing.common.domain.R;
import com.github.jaxing.utils.ConfigUtils;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.FileUpload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author cjxin
 * @date 2023/09/04
 */
@Component
public class FileServiceImpl implements FileService {

    @Resource
    private MongoClient mongoClient;

    /**
     * 文件上传
     *
     * @param fileUpload 文件
     * @return 文件id
     */
    @Override
    public Future<String> upload(FileUpload fileUpload) {
        Promise<String> promise = Promise.promise();
        mongoClient.createGridFsBucketService(ConfigUtils.get("mongodb.fs.name"))
                .onFailure(promise::fail)
                .onSuccess(
                        client -> client.uploadFile(fileUpload.uploadedFileName())
                                .onFailure(promise::fail).onSuccess(promise::complete)
                );
        return promise.future();
    }

    /**
     * 删除
     *
     * @param key 文件key
     */
    @Override
    public Future<Void> delete(String key) {
        Promise<Void> promise = Promise.promise();
        mongoClient.createGridFsBucketService(ConfigUtils.get("mongodb.fs.name"))
                .onFailure(promise::fail)
                .onSuccess(
                        client -> client.delete(key)
                                .onFailure(t -> {
                                    if (t.getMessage().startsWith("No file found with the ObjectId")) {
                                        promise.complete();
                                    } else {
                                        promise.fail(t);
                                    }
                                }).onSuccess(promise::complete)
                );
        return promise.future();
    }


}
