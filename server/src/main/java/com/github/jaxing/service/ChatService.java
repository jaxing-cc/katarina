package com.github.jaxing.service;

import com.github.jaxing.common.domain.ChatMessage;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;


/**
 * @author cjxin
 * @date 2023/07/31
 */
public interface ChatService {
    /**
     * 发送信息
     *
     * @param currentUser 当前用户
     * @param chatMessage 消息
     * @return 是否成功
     */
    Future<Void> sendChatMessage(User currentUser, ChatMessage chatMessage);
}
