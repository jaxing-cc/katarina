package com.github.jaxing.socket;

import com.github.jaxing.common.domain.Client;
import com.github.jaxing.common.domain.Message;
import com.github.jaxing.common.enums.MessageTypeEnum;
import com.github.jaxing.common.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cjxin
 * @date 2023/07/26
 */
@Component
@Slf4j
public class WsHandler implements ApplicationContextAware {

    private static final Map<MessageTypeEnum, MessageHandler> MAP = new HashMap<>();

    public void handle(Message message, Client client) {
        MessageTypeEnum messageType = CommonUtils.getEnumByCode(MessageTypeEnum.values(), message.getType());
        if (ObjectUtils.isEmpty(messageType)) {
            client.sendText(MessageTypeEnum.FAIL.message("消息类型不能为空").toString());
            return;
        }
        MessageHandler messageHandler = MAP.get(messageType);
        messageHandler.handle(message, client, message.getData().mapTo(messageType.getDataClass()));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] beanNamesForType = applicationContext.getBeanNamesForType(MessageHandler.class);
        for (String name : beanNamesForType) {
            MessageHandler handler = (MessageHandler) applicationContext.getBean(name);
            MessageTypeEnum messageTypeEnum = handler.messageType();
            if (!ObjectUtils.isEmpty(messageTypeEnum)) {
                MAP.put(messageTypeEnum, handler);
            }
        }
    }
}
