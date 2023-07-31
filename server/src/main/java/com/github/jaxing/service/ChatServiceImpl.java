package com.github.jaxing.service;

import com.github.jaxing.common.domain.ChatMessage;
import com.github.jaxing.common.domain.Client;
import com.github.jaxing.common.enums.MessageTypeEnum;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @author cjxin
 * @date 2023/07/31
 */
@Service
public class ChatServiceImpl implements ChatService {
    /**
     * 发送信息
     *
     * @param currentUser 当前用户
     * @param chatMessage 消息
     * @return 是否成功
     */
    @Override
    public Future<Void> sendChatMessage(User currentUser, ChatMessage chatMessage) {
        if (chatMessage.getGroupMessage()) {
            return Future.failedFuture("暂不支持群组");
        }
        Client client = Client.CLIENT_POOL.get(chatMessage.getTo());
        if (ObjectUtils.isEmpty(client)){
            return Future.failedFuture("目标不在线");
        }
        chatMessage.setFrom(currentUser.subject());
        System.out.println(currentUser.subject());
        client.sendText(MessageTypeEnum.CHAT_MESSAGE.message(chatMessage).toString());
        return Future.succeededFuture();
    }
}
