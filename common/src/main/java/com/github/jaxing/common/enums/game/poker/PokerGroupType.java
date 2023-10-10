package com.github.jaxing.common.enums.game.poker;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;

@Getter
@AllArgsConstructor
public enum PokerGroupType {
    PASS(PokerGame.DOU_DI_ZHU, "过", (byte) 0),
    ONE(PokerGame.DOU_DI_ZHU, "单张", (byte) 1),
    TWO(PokerGame.DOU_DI_ZHU, "对子", (byte) 2),
    THREE(PokerGame.DOU_DI_ZHU, "三带零", (byte) 3),
    THREE_AND_ONE(PokerGame.DOU_DI_ZHU, "三带一", (byte) 4),
    THREE_AND_TWO(PokerGame.DOU_DI_ZHU, "三带二", (byte) 5),
    FOUR(PokerGame.DOU_DI_ZHU, "炸弹", (byte) 6),
    MANY_ONE(PokerGame.DOU_DI_ZHU, "顺子", (byte) 7),
    MANY_TWO(PokerGame.DOU_DI_ZHU, "对子的顺子", (byte) 8),
    AIRPLANE(PokerGame.DOU_DI_ZHU, "飞机带零", (byte) 9),
    AIRPLANE_AND_ONE(PokerGame.DOU_DI_ZHU, "飞机带单", (byte) 10),
    AIRPLANE_AND_TWO(PokerGame.DOU_DI_ZHU, "飞机带对", (byte) 11),
    JOKER(PokerGame.DOU_DI_ZHU, "王炸", (byte) 12);

    public static HashMap<Byte, PokerGroupType> map = new HashMap<>();

    static {
        map.put((byte) 0, PokerGroupType.PASS);
        map.put((byte) 1, PokerGroupType.ONE);
        map.put((byte) 2, PokerGroupType.TWO);
        map.put((byte) 3, PokerGroupType.THREE);
        map.put((byte) 4, PokerGroupType.THREE_AND_ONE);
        map.put((byte) 5, PokerGroupType.THREE_AND_TWO);
        map.put((byte) 6, PokerGroupType.FOUR);
        map.put((byte) 7, PokerGroupType.MANY_ONE);
        map.put((byte) 8, PokerGroupType.MANY_TWO);
        map.put((byte) 9, PokerGroupType.AIRPLANE);
        map.put((byte) 10, PokerGroupType.AIRPLANE_AND_ONE);
        map.put((byte) 11, PokerGroupType.AIRPLANE_AND_TWO);
        map.put((byte) 12, PokerGroupType.JOKER);
    }

    private final PokerGame game;

    private final String name;

    private final byte code;


    @Override
    public String toString() {
        return name;
    }
}
