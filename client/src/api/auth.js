import request from '@/utils/request'

export function login(params) {
    return request.post("auth/login", params);
}

export function register(params) {
    return request.post("auth/register", params);
}

export function getByUid(uid) {
    return request.get("api/user/" + uid);
}

export function getByKey(key) {
    return request.get("api/user/search/" + key);
}
