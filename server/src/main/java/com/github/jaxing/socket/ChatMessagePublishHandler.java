package com.github.jaxing.socket;

import com.github.jaxing.common.domain.ChatMessage;
import com.github.jaxing.common.domain.Client;
import com.github.jaxing.common.domain.Message;
import com.github.jaxing.common.enums.MessageTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @author cjxin
 * @date 2023/07/26
 */
@Component
public class ChatMessagePublishHandler implements MessageHandler {
    @Override
    public MessageTypeEnum messageType() {
        return MessageTypeEnum.CHAT_MESSAGE;
    }

    @Override
    public void handle(Message message, Client client, Object data) {
        ChatMessage content = (ChatMessage) data;
        System.out.println(content);
        System.out.println(message);
    }
}
