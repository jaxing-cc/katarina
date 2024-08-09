package com.github.jaxing.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 帖子实体
 */
@Data
public class Post {

    //状态准备
    public static final int STATE_PREPARE = 0;

    //状态发布
    public static final int STATE_PUBLISH = 1;

    //状态删除
    public static final int STATE_DELETED = -1;


    @JsonProperty("_id")
    private String id;

    /**
     * 用户id
     */
    private String uid;

    /**
     * 标题(markdown)
     */
    private String title;

    /**
     * 封面(markdown)
     */
    private String cover;

    /**
     * 动态图片key列表
     */
    private List<String> images;

    /**
     * 内容
     */
    private String content;

    /**
     * 状态 草稿0、已发布1、已删除-1
     */
    private Integer state;

    /**
     * 类型 markdown、普通动态
     */
    private Boolean markdown;

    /**
     * 上次更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
