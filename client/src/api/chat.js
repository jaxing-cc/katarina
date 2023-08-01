import request from '@/utils/request'

export function sendMessage(params) {
    return request.post("api/send", params);
}
