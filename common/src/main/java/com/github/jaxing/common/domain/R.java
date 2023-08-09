package com.github.jaxing.common.domain;

import io.vertx.core.json.JsonObject;
import lombok.Data;

/**
 * @author cjxin
 * @date 2023/05/08
 */
@Data
public class R<T> {

    private boolean success;

    private String msg;

    private T data;

    public static <T> R<T> ok(T data) {
        return resp(true, "ok", data);
    }

    public static <T> R<T> ok() {
        return resp(true, "ok", null);
    }

    public static R<Void> fail(Throwable throwable) {
        return resp(false, throwable.getMessage(), null);
    }

    public static <T> R<T> resp(boolean success, String msg, T data) {
        R<T> res = new R<>();
        res.setSuccess(success);
        res.setMsg(msg);
        res.setData(data);
        return res;
    }

    public String toJsonString() {
        return JsonObject.mapFrom(this).toString();
    }
}
