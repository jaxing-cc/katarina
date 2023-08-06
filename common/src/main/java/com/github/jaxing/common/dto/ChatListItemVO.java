package com.github.jaxing.common.dto;

import com.github.jaxing.common.domain.ChatListItem;
import com.github.jaxing.common.domain.UserInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author cjxin
 * @date 2023/08/03
 */
@Data
public class ChatListItemVO implements Serializable {

    private ChatListItem info;

    private UserInfo user;

    private String lastMessage;

    private Integer offlineMsgCount;
}
