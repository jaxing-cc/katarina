import request from '@/utils/request'

/**
 * 发送消息
 */
export function sendMessage(params) {
    return request.post("api/msg/send", params);
}

/**
 * 获取聊天列表
 */
export function loadChatList(page) {
    return request.get("api/chat-list/" + page);
}

/**
 * 聊天列表add
 */
export function addChatListItem(uid) {
    return request.post("api/chat-list/" + uid);
}
/**
 * 聊天列表remove
 */
export function deleteChatListItem(uid) {
    return request.delete("api/chat-list/" + uid);
}