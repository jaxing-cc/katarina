package com.github.jaxing.common.game.poker;


import com.github.jaxing.common.enums.game.poker.PokerType;
import sun.reflect.SignatureIterator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 扑克牌工厂
 */
public class PokerFactory {

    public static Poker[] POKER_MAP;

    public static byte MAX_POKER_SIZE = 54;

    static {
        POKER_MAP = new Poker[MAX_POKER_SIZE];
        byte index = 0;
        byte val = 1;
        while (index < MAX_POKER_SIZE - 2) {
            String tag;
            if (val <= 9){
                tag = String.valueOf(val);
            }else if (val == 10){
                tag = "0";
            }else if (val == 11){
                tag = "j";
            }else if (val == 12){
                tag = "q";
            }else {
                tag = "k";
            }
            POKER_MAP[index] = new Poker(index, val, PokerType.SPADE, tag);
            index++;
            POKER_MAP[index] = new Poker(index, val, PokerType.HEARTS, tag);
            index++;
            POKER_MAP[index] = new Poker(index, val, PokerType.CLUBS, tag);
            index++;
            POKER_MAP[index] = new Poker(index, val, PokerType.DIAMONDS, tag);
            index++;
            val++;
        }
        POKER_MAP[index] = new Poker(index, (byte) 14, PokerType.JOKER, "s");
        index++;
        POKER_MAP[index] = new Poker(index, (byte) 15, PokerType.SUPER_JOKER, "S");
    }

    public static Poker get(byte id) {
        return POKER_MAP[id];
    }

    /***
     * 返回一个大小为4的牌组
     * 0:底牌 3张
     * 1:玩家1 17张
     * 2:玩家2 17张
     * 3:玩家3 17张
     * 时间复杂度 n
     * @return
     */
    public static PokerGroup[] wash() {
        byte[] all = new byte[MAX_POKER_SIZE];
        for (byte i = 0; i < MAX_POKER_SIZE; i++) {
            all[i] = i;
        }
        byte item;
        for (byte i = 0; i < MAX_POKER_SIZE; i++) {
            byte target = (byte) (Math.random() * 54);
            if (i != target) {
                item = all[i];
                all[i] = all[target];
                all[target] = item;
            }
        }
        PokerGroup[] answer = new PokerGroup[4];
        for (byte i = 0; i < 4; i++) {
            answer[i] = new PokerGroup();
        }
        for (byte i = 0; i < MAX_POKER_SIZE; i++) {
            if (i < 17) {
                answer[0].put(new PokerGroupItem(all[i]));
            } else if (i < 34) {
                answer[1].put(new PokerGroupItem(all[i]));
            } else if (i < 51) {
                answer[2].put(new PokerGroupItem(all[i]));
            } else {
                answer[3].put(new PokerGroupItem(all[i]));
            }
        }
        for (PokerGroup pokerGroup : answer) {
            pokerGroup.sort();
        }
        return answer;
    }

    /**
     * 归并两个有序链表
     *
     * @param l1 链表1
     * @param l2 链表2
     */
    public static PokerGroupItem merge(PokerGroupItem l1, PokerGroupItem l2) {
        PokerGroupItem answerHead = null;
        PokerGroupItem answerPoint = null;
        while (l1 != null && l2 != null) {
            PokerGroupItem next;
            if (l1.val() < l2.val()) {
                next = l1;
                l1 = cut(l1, 1);
            } else {
                next = l2;
                l2 = cut(l2, 1);
            }
            if (answerHead == null) {
                answerHead = next;
                answerPoint = next;
            } else {
                answerPoint.setNext(next);
                next.setLast(answerPoint);
                answerPoint = answerPoint.getNext();
            }
        }
        if (l1 != null) {
            if (answerHead == null) {
                answerHead = l1;
            } else {
                answerPoint.setNext(l1);
                l1.setLast(answerPoint);
            }
        }
        if (l2 != null) {
            if (answerHead == null) {
                answerHead = l2;
            } else {
                answerPoint.setNext(l2);
                l2.setLast(answerPoint);
            }
        }
        return answerHead;
    }

    /**
     * 切掉一个链表前n个节点，返回后边半段
     */
    public static PokerGroupItem cut(PokerGroupItem linkedlist, int n) {
        if (n == 0) {
            return null;
        }
        for (int i = 0; i < n; i++) {
            linkedlist = linkedlist.getNext();
            if (linkedlist == null) {
                return null;
            }
        }
        PokerGroupItem last = linkedlist.getLast();
        linkedlist.setLast(null);
        if (last != null) {
            last.setNext(null);
        }
        return linkedlist;
    }


    /**
     * 获取计数数组
     */
    public static int[] getPokerArray(char[] chars) {
        int[] countArray = new int[16];
        for (char aChar : chars) {
            if (aChar >= '0' && aChar <= '9') {
                if (aChar == '0') {
                    countArray[10]++;
                } else {
                    countArray[aChar - '0']++;
                }
            } else if (aChar == 's') {
                countArray[14]++;
            } else if (aChar == 'S') {
                countArray[15]++;
            } else if (aChar == 'K' || aChar == 'k') {
                countArray[13]++;
            } else if (aChar == 'Q' || aChar == 'q') {
                countArray[12]++;
            } else if (aChar == 'J' || aChar == 'j') {
                countArray[11]++;
            } else if (aChar == 'A' || aChar == 'a') {
                countArray[1]++;
            }
        }
        return countArray;
    }

    /**
     * 获取计数数组
     */
    public static int[] getPokerArray(PokerGroup pokerGroup) {
        int[] countArray = new int[16];
        pokerGroup.forEach(i -> {
            Poker poker = i.get();
            countArray[poker.getValue()]++;
        });
        return countArray;
    }

    /**
     * 获取计数数组
     */
    public static int[] getPokerArray(List<Byte> ids) {
        int[] countArray = new int[16];
        ids.stream().map(id -> POKER_MAP[id]).forEach(p ->
                countArray[p.getValue()]++
        );
        return countArray;
    }

    /**
     * 返回数组中值为 val 的连续长度
     */
    public static int getContinuousNumberSize(int val, int[] countArray) {
        int count = 0;
        int i = 2;
        while (i <= 13 && countArray[i] != val) {
            i++;
        }
        if (i == 2) {
            return -1;
        }
        while (i <= 13 && countArray[i] == val) {
            count++;
            i++;
        }
        if (i == 14) {
            count += countArray[1] == val ? 1 : 0;
        }
        return count;
    }

    /**
     * id获取表达式
     */
    public static String getExpByIds(List<Byte> ids){
        StringBuffer sb = new StringBuffer();
        for (Byte id : ids) {
            sb.append(PokerFactory.get(id).getTag());
        }
        return sb.toString();
    }
}

