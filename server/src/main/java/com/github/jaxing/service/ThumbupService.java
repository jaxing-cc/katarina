package com.github.jaxing.service;

import io.vertx.core.Future;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ThumbupService {

    /**
     * 点赞/取消
     */
    Future<Void>  like(String uid, String targetId, String business, boolean cancel);

    /**
     * 查询已点赞的对象id
     */
    Future<Set<String>> liked(Collection<String> targetId, String businessId, String uid);


    /**
     * 查询对象点赞数
     */
    Future<Map<String, Integer>> info(Set<String> targetId, String businessId);
}
