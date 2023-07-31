package com.github.jaxing.socket;

import com.github.jaxing.common.domain.Client;
import com.github.jaxing.common.domain.Message;
import com.github.jaxing.common.enums.MessageTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @author cjxin
 * @date 2023/07/28
 */
@Component
public class HeartbeatHandler implements MessageHandler {

    @Override
    public MessageTypeEnum messageType() {
        return MessageTypeEnum.HEARTBEAT;
    }

    @Override
    public void handle(Message message, Client client, Object data) {
        client.sendText(MessageTypeEnum.HEARTBEAT_OK.message(data).toString());
    }
}
