package com.github.jaxing.utils;

import io.vertx.ext.web.RoutingContext;


/**
 * @author cjxin
 * @date 2024/04/16
 */
public class CommonUtils {
    /**
     * 请求中获取uid
     * @param context 上下文
     * @return uid
     */
    public static String getUid(RoutingContext context){
        return context.user().principal().getString("uid");
    }
}
