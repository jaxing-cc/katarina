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
