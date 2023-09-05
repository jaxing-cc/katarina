package com.github.jaxing.common.enums;

/**
 * @author cjxin
 * @date 2023/06/13
 */
public enum CollectionEnum {


    /**
     * 用户
     *
     * @see com.github.jaxing.common.domain.UserInfo
     */
    user,

    /**
     * 角色
     *
     * @see com.github.jaxing.common.domain.UserRole
     */
    role,

    /**
     * 消息桶（离线消息、历史记录、定时任务清理）
     *
     * @see com.github.jaxing.common.domain.ChatMessage
     */
    message_bucket,

    /**
     * 用户的聊天列表
     *
     * @see com.github.jaxing.common.domain.ChatListItem
     */
    chat_list,

    follow_list
}
