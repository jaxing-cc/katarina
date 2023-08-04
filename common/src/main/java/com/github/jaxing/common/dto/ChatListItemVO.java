package com.github.jaxing.common.dto;

import com.github.jaxing.common.domain.ChatListItem;
import lombok.Data;

import java.io.Serializable;

/**
 * @author cjxin
 * @date 2023/08/03
 */
@Data
public class ChatListItemVO implements Serializable {

    private ChatListItem item;

    private String uid;

    private String avatar;

    private String name;

    private Integer gender;

    private String lastMessage;

    private String lastMessageUserId;
}
