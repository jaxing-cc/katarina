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

/**
 * 聊天列表add
 */

/**
 * 聊天列表remove
 */