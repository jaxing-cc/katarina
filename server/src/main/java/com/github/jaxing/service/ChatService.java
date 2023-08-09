package com.github.jaxing.service;

import com.github.jaxing.common.domain.ChatMessage;
import com.github.jaxing.common.dto.ChatListItemVO;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;

import java.util.List;
import java.util.Map;


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
     * @param uid     用户
     * @param isGroup 群消息/个人消息
     */
    Future<Map<String, Integer>> offlineMessageCount(String uid, boolean isGroup);

    /**
     * 加载用户离线消息数量
     *
     * @param uid     用户
     * @param isGroup 群消息/个人消息
     */
    Future<Map<String, Integer>> offlineMessageCountAndUpdateChatList(String uid, boolean isGroup);

    /**
     * 查询聊天记录,分页
     *
     * @param uid      当前用户
     * @param targetId 目标用户/群
     * @param isGroup  是否是群
     * @param page     页码
     * @param size     页数量
     * @return 聊天记录
     */
    Future<List<ChatMessage>> loadChatMessageRecord(String uid, String targetId, boolean isGroup, Integer page, Integer size);

    /**
     * 消除离线消息标记
     *
     * @param uid      当前用户
     * @param targetId 目标用户/群
     * @param isGroup  是否是群
     * @return 是否成功
     */
    Future<Void> clearOfflineMessage(String uid, String targetId, boolean isGroup);
}
