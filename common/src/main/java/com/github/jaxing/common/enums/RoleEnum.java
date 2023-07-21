package com.github.jaxing.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author cjxin
 * @date 2023/07/20
 */
@Getter
@AllArgsConstructor
public enum RoleEnum implements EnumCode<String> {
    ROOT("root"),
    NORMAL("normal");
    private final String code;
}
