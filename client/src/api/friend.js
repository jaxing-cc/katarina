import request from '@/utils/request'

export function loadFriends(params) {
    let base = "friend/get?page=" + params.page;
    if (params.gid){
        base += "&gid=" + params.gid
    }
    return request.get(base);
}

export function loadGroup() {
    return request.get("friend/allGroup");
}
