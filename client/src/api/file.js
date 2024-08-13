import request from "@/utils/request";
import URL from "@/utils/common";

/**
 * 获取url
 */
export function getFileUrl(key) {
    return URL.baseURL + "/file/" + key;
}

/**
 * 获取url，key为空返回默认头像
 */
export function getAvatarUrlOrDefault(key, gender) {
    return key ? URL.baseURL + "/file/" + key : 'avatar-' + (gender === 1 ? '1' : '2') + ".jpg"
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

/**
 * 上传文件
 */
export function remove(key) {
    return request.delete("/api/file/"+ key)
}