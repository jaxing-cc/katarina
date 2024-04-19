package com.github.jaxing.common.utils;

import com.github.jaxing.common.enums.EnumCode;

import java.util.Arrays;
import java.util.Random;

/**
 * @author cjxin
 * @date 2023/07/20
 */
public class CommonUtils {


    private static final Random random = new Random();

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T getEnumByCode(Enum<T>[] enums, Object code) {
        if (code == null) {
            return null;
        }
        return (T) Arrays.stream(enums).map(e -> (EnumCode) e).filter(e -> code.equals(e.getCode())).findFirst().orElse(null);
    }

    public static int random(int start, int end){
        return start + random.nextInt(end - start);
    }
}
