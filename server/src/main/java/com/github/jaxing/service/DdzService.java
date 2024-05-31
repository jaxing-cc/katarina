package com.github.jaxing.service;

import com.github.jaxing.dto.vo.DdzRoomVO;
import io.vertx.core.Future;

import java.util.List;


/**
 * @author cjxin
 * @date 2024/04/16
 */
public interface DdzService {

    /**
     * 查询房间列表
     *
     * @param name 房间名
     * @return 房间列表
     */
    Future<List<DdzRoomVO>> roomList(String name);

    /**
     * 创建房间
     *
     * @return 房间id
     */
    Future<String> createRoomAndJoin(String uid, String name);

    /**
     * 加入
     *
     * @param roomId 房间id
     */
    Future<Void> joinRoom(String uid, String roomId);

    /**
     * 退出
     */
    Future<Void> exit(String uid);

    /**
     * 准备/开始
     */
    Future<Void> ready(String uid);

    /**
     * 叫
     *
     * @param v 分数
     */
    Future<Void> callMaster(String uid, Integer v);

    /**
     * 出
     */
    void pop(String uid, List<Byte> ids);

}
