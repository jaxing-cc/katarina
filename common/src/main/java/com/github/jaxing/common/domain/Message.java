package com.github.jaxing.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cjxin
 * @date 2023/07/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message<T> implements Serializable {
    /**
     * 类型
     */
    private Integer type;

    /**
     * 消息内容
     */
    private T content;

    /**
     * 创建时间
     */
    private Date createTime;
}
