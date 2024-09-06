package com.github.jaxing.common.game.poker.ddz;

import com.github.jaxing.common.enums.game.poker.PokerGame;
import com.github.jaxing.common.enums.game.poker.PokerGroupType;
import com.github.jaxing.common.game.poker.ComparablePokerGroup;
import com.github.jaxing.common.game.poker.PokerFactory;
import com.github.jaxing.common.game.poker.PokerGameRule;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author cjxin
 * @date 2023/10/10
 */
public class DdzGameRule implements PokerGameRule {

    @Override
    public PokerGame getGame() {
        return PokerGame.DOU_DI_ZHU;
    }

    @Override
    public boolean compareTo(ComparablePokerGroup a, ComparablePokerGroup b) {
        int[] aCountArray = a.getCountArray();
        PokerGroupType aType = a.getType();
        int[] bCountArray = b.getCountArray();
        PokerGroupType bType = b.getType();
        //王炸
        if (aType == PokerGroupType.JOKER) {
            return true;
        }
        if (bType == PokerGroupType.JOKER) {
            return false;
        }
        //存在炸弹时
        if (bType == PokerGroupType.FOUR) {
            if (aType == PokerGroupType.FOUR) {
                return compareAlone(find(bCountArray, 4), find(aCountArray, 4));
            } else {
                return false;
            }
        } else {
            if (aType == PokerGroupType.FOUR) {
                return true;
            }
        }
        //异常情况
        if (a.getSize() != b.getSize() || a.getType() != b.getType()) {
            return false;
        }
        switch (bType) {
            case ONE:
            case MANY_ONE:
                return compareAlone(find(bCountArray, 1), find(aCountArray, 1));
            case TWO:
            case MANY_TWO:
                return compareAlone(find(bCountArray, 2), find(aCountArray, 2));
            case THREE:
            case THREE_AND_ONE:
            case THREE_AND_TWO:
            case AIRPLANE:
            case AIRPLANE_AND_ONE:
            case AIRPLANE_AND_TWO:
                return compareAlone(find(bCountArray, 3), find(aCountArray, 3));
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


    @Override
    public PokerGroupType checkInput(String s) {
        if ("p".equals(s) || "".equals(s)) {
            return PokerGroupType.PASS;
        }
        if (!Pattern.matches("[0-9|aAjJqQkKsS]{1,17}", s)) {
            return null;
        }
        char[] chars = s.toCharArray();
        int len = chars.length;
        int[] countArray = PokerFactory.getPokerArray(chars);
        int temp = 0;
        PokerGroupType result = null;
        switch (len) {
            case 1:
                result = PokerGroupType.ONE;
                break;
            case 2:
                if (chars[0] != 'S' && chars[0] != 's' && chars[0] == chars[1]) {
                    result = PokerGroupType.TWO;
                } else if ((chars[0] == 'S' && chars[1] == 's') || (chars[0] == 's' && chars[1] == 'S')) {
                    result = PokerGroupType.JOKER;
                }
                break;
            case 3:
                if (chars[0] != 's' && chars[0] != 'S' && chars[0] == chars[1] && chars[1] == chars[2]) {
                    result = PokerGroupType.THREE;
                }
                break;
            case 4:
                if (chars[0] != 's' && chars[0] != 'S' && chars[0] == chars[1] && chars[1] == chars[2] && chars[2] == chars[3]) {
                    result = PokerGroupType.FOUR;
                } else if ((temp = checkThreeAndX(countArray)) != 0) {
                    result = temp == 1 ? PokerGroupType.THREE_AND_ONE : PokerGroupType.THREE_AND_TWO;
                }
                break;
            default:
                if ((temp = checkThreeAndX(countArray)) != 0) {
                    result = temp == 1 ? PokerGroupType.THREE_AND_ONE : PokerGroupType.THREE_AND_TWO;
                } else {
                    result = checkOtherType(countArray);
                }
        }
        return result;
    }

    /**
     * 检查其他类型
     *
     * @param countArray 计数数组
     * @return 牌组类型
     */
    private PokerGroupType checkOtherType(int[] countArray) {
        Set<Integer> set = new HashSet<>();
        for (int i : countArray) {
            if (i > 3) {
                return null;
            }
            if (i != 0) {
                set.add(i);
            }
        }
        if (set.contains(3)) {
            int size = PokerFactory.getContinuousNumberSize(3, countArray);
            int twoSize = 0;
            int oneSize = 0;
            for (int i = 1; i <= 13; i++) {
                if (countArray[i] == 2) {
                    twoSize++;
                }
                if (countArray[i] == 1) {
                    oneSize++;
                }
            }
            if (size >= 2) {
                if (oneSize == 0 && twoSize == 0) {
                    return PokerGroupType.AIRPLANE;
                } else if (oneSize == 0 && twoSize == size) {
                    return PokerGroupType.AIRPLANE_AND_TWO;
                } else if (oneSize == size && twoSize == 0) {
                    return PokerGroupType.AIRPLANE_AND_ONE;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else if (set.contains(2)) {
            return PokerFactory.getContinuousNumberSize(2, countArray) >= 3 ? set.size() > 1 ? null : PokerGroupType.MANY_TWO : null;
        } else if (set.contains(1)) {
            return PokerFactory.getContinuousNumberSize(1, countArray) >= 5 ? set.size() > 1 ? null : PokerGroupType.MANY_ONE : null;
        }
        return null;
    }

    /**
     * 检查三带类型
     *
     * @param countArray 计数数组
     * @return 0标识无法识别 1标识三带一 2标识三带对
     */
    private int checkThreeAndX(int[] countArray) {
        int three = 0;
        int mainNum = 0;
        int two = 0;
        int one = 0;
        for (int i = 1; i < countArray.length; i++) {
            if (countArray[i] != 0) {
                switch (countArray[i]) {
                    case 0:
                        break;
                    case 1:
                        one++;
                        break;
                    case 2:
                        two++;
                        break;
                    case 3:
                        three++;
                        mainNum = i;
                        break;
                    default:
                        return 0;
                }
            }
        }
        if (three != 1 || (two + one) != 1) {
            return 0;
        }
        return mainNum <= 13 ? one == 0 ? 2 : 1 : 0;
    }
}
