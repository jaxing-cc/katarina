import request from "@/utils/request";

/**
 * 保存
 */
export function queryComment(page, replyId, targetId, type) {
    return request.get("/api/comment/" + type +"/" + targetId + "?pageNum=" + page + "&replyId=" + replyId);
}