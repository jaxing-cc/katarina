package com.github.jaxing.common.utils;

import com.github.jaxing.common.enums.EnumCode;

import java.util.Arrays;

/**
 * @author cjxin
 * @date 2023/07/20
 */
public class CommonUtils {

    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> T getEnumByCode(Enum<T>[] enums, Object code) {
        if (code == null) {
            return null;
        }
        return (T) Arrays.stream(enums).map(e -> (EnumCode) e).filter(e -> code.equals(e.getCode())).findFirst().orElse(null);
    }
}
