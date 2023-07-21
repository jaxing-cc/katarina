package com.github.jaxing.common.enums;

import com.github.jaxing.common.domain.ChatMessage;
import com.github.jaxing.common.domain.Message;
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
    CHAT(0, ChatMessage.class),
    ;
    private final Integer code;

    private final Class<?> dataClass;

    /**
     * 获取消息实体
     *
     * @param data 数据
     * @param <T>  类型
     * @return 消息实体
     */
    public <T> Message<T> message(T data) {
        if (!this.dataClass.equals(data.getClass())) {
            throw new RuntimeException("Message Type Mismatch");
        }
        return new Message<>(this.code, data, new Date());
    }


}
