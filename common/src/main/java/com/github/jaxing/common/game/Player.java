package com.github.jaxing.common.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cjxin
 * @date 2023/10/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    public static final Map<String, Player> PLAYER_MAP = new ConcurrentHashMap<>();

    /**
     * 用户id
     */
    private String id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 用户状态
     */
    private volatile boolean ready;

    /**
     * 房间id
     */
    private String roomId;
}
