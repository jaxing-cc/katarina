package com.github.jaxing.service;

import io.vertx.core.Future;
import io.vertx.ext.web.FileUpload;

/**
 * @author cjxin
 * @date 2023/09/04
 */
public interface FileService {

    /**
     * 文件上传
     *
     * @param fileUpload 文件
     * @return 文件id
     */
    Future<String> upload(FileUpload fileUpload);

    /**
     * 删除
     * @param key 文件key
     */
    Future<Void> delete(String key);
}
