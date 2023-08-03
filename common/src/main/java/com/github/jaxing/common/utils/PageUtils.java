package com.github.jaxing.common.utils;

/**
 * @author cjxin
 * @date 2023/08/03
 */
public class PageUtils {

    public static int getSkip(int page, int num) {
        return (page - 1) * num;
    }
}
