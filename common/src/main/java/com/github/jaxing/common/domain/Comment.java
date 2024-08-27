package com.github.jaxing.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 评论
 */
@Data
public class Comment {

    /**
     * 评论id
     */
    @JsonProperty("_id")
    private String id;

    /**
     * 回复评论id
     */
    private String replyId;

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
    private String targetType;

    /**
     * 评论人id
     */
    private String uid;

    /**
     * 删除评论，默认false
     */
    private Boolean deleted;

    /**
     * 创建时间
     */
    private Long createTime;

}
