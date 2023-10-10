package com.github.jaxing.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author cjxin
 * @date 2023/08/03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatListItem implements Serializable {

    private String uid;

    private String chatTargetUid;

    private Long createTime;
}
