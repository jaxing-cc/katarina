package com.github.jaxing.common.dto.post;

import lombok.Data;

import java.util.List;

@Data
public class PostSaveDTO {

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
     * 类型 markdown、普通动态
     */
    private Boolean markdown;
}
