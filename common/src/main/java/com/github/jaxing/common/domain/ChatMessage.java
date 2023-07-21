package com.github.jaxing.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cjxin
 * @date 2023/07/21
 */
@Data
public class ChatMessage implements Serializable {

    private String fromUid;

    private String toUid;

    private String content;

    /**
     * 0文本 1md文本 2图片 3其他文件
     */
    private Integer type;

    private Boolean read;

    private Date createTime;

    public ChatMessage() {
        this.read = false;
    }
}
