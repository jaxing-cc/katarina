import request from '@/utils/request'

/**
 * 查询房间列表
 */
export function roomList(name) {
    if (name) {
        return request.get("/api/game/ddz/room?name=" + name);
    }
    return request.get("/api/game/ddz/room");
}

/**
 * 创建房间列表
 */
export function createRoom(name) {
    return request.post("/api/game/ddz/room/" + name);
}

/**
 * 加入房间列表
 */
export function joinRoom(id) {
    return request.put("/api/game/ddz/room/" + id);
}

/**
 * 退出房间列表
 */
export function exitRoom() {
    return request.delete("/api/game/ddz/room");
}


/**
 * 查询房间列表
 */
export function readyRequest() {
    return request.post("/api/game/ddz/ready");
}