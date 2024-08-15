package com.github.jaxing.common.domain;

/**
 * 评论
 */
public class Comment {

    /**
     * 评论id
     */
    private String id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论目标id
     */
    private String targetId;

    /**
     * 评论目标类型
     */
    private Integer targetType;

    /**
     * 评论人id
     */
    private String uid;

    /**
     * 父评论id
     */
    private String parentId;
}
