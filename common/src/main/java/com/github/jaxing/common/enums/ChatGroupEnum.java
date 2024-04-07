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
    COMMON_CHAT_GROUP("public room", "public-channel", "661266ffeb4544771a4c220b", null)
    ;

    ChatGroupEnum(String name, String code, String avatar, String auth) {
        this.name = name;
        this.code = code;
        this.auth = auth;
        this.avatar = avatar;
    }

    private final String name;

    private final String code;

    private final String auth;

    private final String avatar;

    public static List<ChatGroupEnum> chatGroupEnums(List<String> roles){
        return Arrays.stream(values()).filter(g -> g.auth == null || roles.stream().anyMatch(g.auth::contains)).collect(Collectors.toList());
    }
}
