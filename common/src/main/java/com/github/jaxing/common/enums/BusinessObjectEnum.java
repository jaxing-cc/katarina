package com.github.jaxing.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum BusinessObjectEnum {

    POST("post", "动态"),

    COMMENT("comment", "评论");

    private final String code;

    private final String desc;

    public static BusinessObjectEnum get(String code){
        return Arrays.stream(values()).filter(v -> v.getCode().equals(code)).findFirst().orElse(null);
    }

}
