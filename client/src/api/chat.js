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
export function loadChatList(page, size) {
    return request.get("api/chat-list/" + page + "/" + size);
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
/**
 * 未读消息count
 */
export function loadOfflineMsgCount() {
    return request.get("/api/msg/offline-count");
}