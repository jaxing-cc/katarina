import request from "@/utils/request";
import URL from "@/utils/common";

/**
 * 获取url
 */
export function getFileUrl(key) {
    return URL.baseURL + "/file/" + key;
}

/**
 * 上传文件
 */
export function upload(file) {
    let formData = new FormData();
    formData.append("file", file)
    return request.put("/api/file/", formData, {
        headers: {'Content-Type': 'multipart/form-data'}
    })
}