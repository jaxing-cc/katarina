package com.github.jaxing.common.game.poker;

import com.github.jaxing.common.enums.game.poker.PokerGroupType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComparablePokerGroup {
    private int[] countArray;
    private PokerGroupType type;
    private int size;

    public static ComparablePokerGroup getInstance(char[] array) {
        ComparablePokerGroup comparablePokerGroup = new ComparablePokerGroup();
        comparablePokerGroup.setSize(array.length);
        comparablePokerGroup.setCountArray(PokerFactory.getPokerArray(array));
        return comparablePokerGroup;
    }

    //如果 o 大于 this，返回true 小于返回false
    public boolean compareTo(ComparablePokerGroup o) {
        int[] target = o.getCountArray();
        PokerGroupType targetType = o.getType();
        //王炸
        if (targetType == PokerGroupType.JOKER) {
            return true;
        }
        if (type == PokerGroupType.JOKER) {
            return false;
        }
        //存在炸弹时
        if (this.type == PokerGroupType.FOUR) {
            if (targetType == PokerGroupType.FOUR) {
                return compareAlone(find(countArray, 4), find(target, 4));
            } else {
                return false;
            }
        } else {
            if (targetType == PokerGroupType.FOUR) {
                return true;
            }
        }
        //异常情况
        if (size != o.getSize() || o.getType() != this.type) {
            return false;
        }
        switch (this.type) {
            case ONE:
            case MANY_ONE:
                return compareAlone(find(countArray, 1), find(target, 1));
            case TWO:
            case MANY_TWO:
                return compareAlone(find(countArray, 2), find(target, 2));
            case THREE:
            case THREE_AND_ONE:
            case THREE_AND_TWO:
            case AIRPLANE:
            case AIRPLANE_AND_ONE:
            case AIRPLANE_AND_TWO:
                return compareAlone(find(countArray, 3), find(target, 3));
        }
        return false;
    }

    /**
     * 比较单张
     *
     * @param val1 计数数组下标
     * @param val2 计数数组下标
     * @return val1 是否小于 val2
     */
    private boolean compareAlone(int val1, int val2) {
        boolean a12 = val1 == 1 || val1 == 2;
        boolean b12 = val2 == 1 || val2 == 2;
        if (a12 && b12) {
            return val1 < val2;
        } else if (a12 || b12) {
            return b12 || (val2 == 14 || val2 == 15);
        } else {
            return val1 < val2;
        }
    }

    private int find(int[] countArray, int val) {
        for (int i = 1; i < countArray.length; i++) {
            if (countArray[i] == val) {
                return i;
            }
        }
        return 0;
    }
}
