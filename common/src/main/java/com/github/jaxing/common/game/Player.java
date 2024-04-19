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
     * 用户状态
     */
    private volatile boolean ready;

    /**
     * 房间id
     */
    private String roomId;
}
