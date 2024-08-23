import request from "@/utils/request";

/**
 * 保存
 */
export function thumbup(data) {
    return request.post("/auth/thumbup", data)
}