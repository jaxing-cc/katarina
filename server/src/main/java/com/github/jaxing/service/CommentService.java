package com.github.jaxing.service;

import com.github.jaxing.common.dto.CommentVO;
import io.vertx.core.Future;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CommentService {
    /**
     * 保存评论
     */
    Future<Void> save(String uid, String replyId, String content, String targetId, String type);

    /**
     * 查询评论
     */
    Future<List<CommentVO>> query(Integer pageNum, String uid, String replyId, String targetId, String type);

    /**
     * 查询评论id对应子评论数量
     */
    Future<Map<String, Integer>> queryChildSize(Collection<String> commentId);
}
