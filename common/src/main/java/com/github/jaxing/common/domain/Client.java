package com.github.jaxing.common.domain;

import com.github.jaxing.common.utils.VertxHolder;
import io.vertx.core.eventbus.EventBus;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cjxin
 * @date 2023/07/26
 */
@Data
@Builder
public class Client {

    public static final Map<String, Client> CLIENT_POOL = new ConcurrentHashMap<>();

    /**
     * 文本id
     */
    private String textAddressId;

    /**
     * 二进制id
     */
    private String binAddressId;

    /**
     * 用户id
     */
    private String uid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色
     */
    private Set<String> roles;

    /**
     * 将自己添加入客户端池中
     */
    public Client addSelf() {
        CLIENT_POOL.put(this.uid, this);
        return this;
    }

    /**
     * 将自己添加入客户端池中
     */
    public void removeSelf() {
        CLIENT_POOL.remove(this.uid);
    }

    public EventBus sendText(String text) {
        return VertxHolder.getVertx().eventBus().send(this.getTextAddressId(),text);
    }


}
