package com.github.jaxing.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author cjxin
 * @date 2023/06/13
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {
    other(0),
    M(1),
    W(2);
    private final Integer code;
}
