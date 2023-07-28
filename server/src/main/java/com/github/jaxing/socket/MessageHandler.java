package com.github.jaxing.socket;

import com.github.jaxing.common.domain.Client;
import com.github.jaxing.common.domain.Message;
import com.github.jaxing.common.enums.MessageTypeEnum;

/**
 * @author cjxin
 * @date 2023/07/26
 */
public interface MessageHandler {

    MessageTypeEnum messageType();

    void handle(Message message, Client client, Object data);
}
