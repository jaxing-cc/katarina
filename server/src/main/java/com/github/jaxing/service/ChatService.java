package com.github.jaxing.service;

import com.github.jaxing.common.domain.ChatMessage;
import com.github.jaxing.common.dto.ChatListItemVO;
import com.github.jaxing.common.dto.OfflineMessageCountVO;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;

import java.util.List;


/**
 * @author cjxin
 * @date 2023/07/31
 */
public interface ChatService {
    /**
     * 发送信息
     *
     * @param currentUser 当前用户
     * @param chatMessage 消息
     * @return 是否成功
     */
    Future<Void> sendChatMessage(User currentUser, ChatMessage chatMessage);

    /**
     * 增加聊天列表
     *
     * @param cUid 当前用户
     * @param tUid 目标用户
     */
    Future<Void> addChatListItem(String cUid, String tUid);

    /**
     * 删除聊天列表
     *
     * @param cUid 当前用户
     * @param tUid 目标用户
     */
    Future<Void> deleteChatListItem(String cUid, String tUid);

    /**
     * 查询用户的聊天列表
     *
     * @param uid 当前用户
     */
    Future<List<ChatListItemVO>> chatList(String uid, Integer page, Integer size);

    /**
     * 加载用户离线消息数量
     *
     * @param uid 用户
     */
    Future<List<OfflineMessageCountVO>> offlineMessageCount(String uid);
}
