import request from "@/utils/request";
import URL from "@/utils/common";

/**
 * 获取url
 */
export function save(data) {
    return request.post("/api/post", data)
}