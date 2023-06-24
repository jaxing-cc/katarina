package com.github.jaxing.common.utils;

import org.springframework.util.DigestUtils;

/**
 * @author cjxin
 * @date 2023/06/13
 */
public class SecurityUtils {

    public static String encode(String password) {
        return DigestUtils.md5DigestAsHex(new StringBuffer().append(password).reverse().toString().getBytes());
    }

    public static boolean match(String source, String target) {
        return target.equals(encode(source));
    }
}
