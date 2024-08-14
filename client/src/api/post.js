import request from "@/utils/request";
import URL from "@/utils/common";
import url from "@/utils/common";

/**
 * 获取url
 */
export function save(data) {
    return request.post("/api/post", data)
}

/**
 * 搜索
 */
export function search(value, page) {
    let url = "/api/post?page=";
    if (!page) {
        page = 1;
    }
    url += page;
    if (value) {
        url += "&keyword=" + value;
    }
    return request.get(url)
}