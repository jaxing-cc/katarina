package com.github.jaxing.dto.vo;

import com.github.jaxing.common.enums.game.poker.GameStatus;
import lombok.Data;

/**
 * @author cjxin
 * @date 2024/04/17
 */
@Data
public class DdzRoomVO {

    /**
     * 房间名
     */
    private String name;

    /**
     * 房间id
     */
    private String id;

    /**
     * 人数
     */
    private int playerSize;

    /**
     * 状态
     */
    private GameStatus status;

}
