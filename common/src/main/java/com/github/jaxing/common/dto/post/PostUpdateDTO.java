package com.github.jaxing.common.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PostUpdateDTO {

    private String id;

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
     * 发布
     */
    private Boolean publish;
}
