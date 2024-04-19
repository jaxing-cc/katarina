package com.github.jaxing.common.enums;

import com.github.jaxing.common.domain.ChatMessage;
import com.github.jaxing.common.domain.Message;
import com.github.jaxing.common.game.poker.ddz.DdzContext;
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

    HEARTBEAT_OK(1000, String.class, true),

    CHAT_MESSAGE(1001, ChatMessage.class, true),

    DDZ_MESSAGE(1002, JsonObject.class, true),

    /*** 输入指令 ****/

    HEARTBEAT(10000, String.class, false),

    ;
    private final Integer code;

    private final Class<?> dataClass;

    public boolean output;

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
        if (data instanceof String ||
                data instanceof Integer ||
                data instanceof Double ||
                data instanceof Date ||
                data instanceof Long ||
                data instanceof Boolean ||
                data instanceof JsonObject
        ) {
            return new Message(this.code, data, new Date());
        }
        return new Message(this.code, JsonObject.mapFrom(data), new Date());
    }

}
