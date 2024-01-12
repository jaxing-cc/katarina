package com.github.jaxing.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cjxin
 * @date 2024/01/12
 */
@Getter
public enum ChatGroupEnum {
    COMMON_CHAT_GROUP("Public Channel", "public", null)
    ;

    ChatGroupEnum(String name, String code, String auth) {
        this.name = name;
        this.code = code;
        this.auth = auth;
    }

    private final String name;

    private final String code;

    private final String auth;

    public static List<ChatGroupEnum> chatGroupEnums(List<String> roles){
        return Arrays.stream(values()).filter(g -> g.auth == null || roles.stream().anyMatch(g.auth::contains)).collect(Collectors.toList());
    }
}
