package com.github.jaxing.common.enums;

import com.github.jaxing.common.domain.ChatMessage;
import com.github.jaxing.common.domain.Message;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

/**
 * @author cjxin
 * @date 2023/07/20
 */
@AllArgsConstructor
@Getter
public enum MessageTypeEnum implements EnumCode<Integer> {
    FAIL(0, String.class, true),

    CHAT_MESSAGE(1, ChatMessage.class, true),
    ;
    private final Integer code;

    private final Class<?> dataClass;

    public boolean publish;

    /**
     * 获取消息实体
     *
     * @param data 数据
     * @return 消息实体
     */
    public Message message(Object data) {
        if (!this.dataClass.equals(data.getClass())) {
            throw new RuntimeException("Message Type Mismatch");
        }
        return new Message(this.code, new JsonObject(), new Date());
    }

}
