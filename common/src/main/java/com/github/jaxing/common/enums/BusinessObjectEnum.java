package com.github.jaxing.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessObjectEnum {

    POST("post", "动态"),

    COMMENT("comment", "评论");

    private final String code;

    private final String desc;


}
