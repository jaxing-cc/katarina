package com.github.jaxing.common.game.poker;

import com.github.jaxing.common.enums.game.poker.PokerGroupType;
import com.github.jaxing.common.enums.game.poker.PokerType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 玩家持有的牌组
 */
@Getter
public class PokerGroup {

    //牌组大小
    private byte size;

    //牌组链表
    private PokerGroupItem head;

    //链表尾部
    private PokerGroupItem tail;

    public PokerGroup() {
        size = 0;
        head = null;
        tail = null;
    }

    /**
     * 放入一张牌
     *
     * @param item 牌
     */
    public void put(PokerGroupItem item) {
        if (size == 0) {
            head = item;
            tail = item;
        } else {
            //头插法
            item.setNext(head);
            item.setLast(null);
            head.setLast(item);
            head = item;
        }
        size++;
    }

    /**
     * 放入一组牌
     */
    public void put(PokerGroup pokerGroup) {
        byte size = pokerGroup.getSize();
        if (size == 0) {
            return;
        }
        PokerGroupItem head = pokerGroup.getHead();
        this.size += size;
        tail.setNext(head);
        head.setLast(tail);
        tail = pokerGroup.getTail();
    }

    /**
     * 出牌
     * @param pokerGroup 牌组
     * @return 被删除的牌的id列表
     */
    public byte[] pop(ComparablePokerGroup pokerGroup) {
        int size = pokerGroup.getSize();
        if (this.size < size) {
            return null;
        }
        //检查是否可以出牌
        int[] array = PokerFactory.getPokerArray(this);
        int[] targetArray = pokerGroup.getCountArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] < targetArray[i]) {
                return null;
            } else {
                array[i] = targetArray[i];
            }
        }
        byte[] answer = new byte[size];
        List<PokerGroupItem> list = new ArrayList<>();
        forEach(o -> {
            byte val = o.val();
            if (val == 0) {
                if (o.get().getType() == PokerType.JOKER) {
                    val = 14;
                } else {
                    val = 15;
                }
            }
            if (array[val] != 0) {
                array[val]--;
                list.add(o);
            }
        });
        for (int i = 0; i < size; i++) {
            PokerGroupItem pokerGroupItem = list.get(i);
            answer[i] = pokerGroupItem.getPokerId();
            remove(pokerGroupItem);
        }
        return answer;
    }

    public byte[] getPokersIndex() {
        byte[] answer = new byte[size];
        PokerGroupItem p = head;
        for (int i = 0; i < size; i++) {
            answer[i] = p.getPokerId();
            p = p.getNext();
        }
        return answer;
    }

    public boolean contain(byte[] value) {
        return false;
    }



    /**
     * 删除一张牌
     *
     * @param item 牌
     */
    private void remove(PokerGroupItem item) {
        PokerGroupItem next = item.getNext();
        PokerGroupItem last = item.getLast();
        if (size == 1) {
            head = null;
            tail = null;
        } else if (next == null) {
            //最后一个
            last.setNext(null);
            tail = last;
        } else if (last == null) {
            //第一个
            head = next;
            next.setLast(null);
        } else {
            last.setNext(next);
            next.setLast(last);
        }
        size--;
    }

    /**
     * 链表归并排序
     * 时间复杂度 log(n)
     */
    public void sort() {
        head = sort(head, size);
        tail = head;
        while (tail.getNext() != null) {
            tail = tail.getNext();
        }
    }

    /**
     * 链表归并排序
     * 时间复杂度 log(n)
     */
    private PokerGroupItem sort(PokerGroupItem head, int size) {
        if (size == 1) {
            return head;
        }
        int mid = size / 2;
        PokerGroupItem l = head;
        PokerGroupItem r = PokerFactory.cut(head, mid);
        l = sort(l, mid);
        r = sort(r, size - mid);
        return PokerFactory.merge(l, r);
    }

    @Override
    public String toString() {
        PokerGroupItem point = head;
        StringBuilder sb = new StringBuilder();
        while (point != null) {
            Poker poker = point.get();
            sb.append('|').append(poker.getType().tag).append(poker.getValue());
            point = point.getNext();
        }
        sb.append('|').append("--剩余:").append(size);
        return sb.toString();
    }

    public void forEach(Consumer<PokerGroupItem> loopFunctionInterface) {
        PokerGroupItem point = head;
        while (point != null) {
            loopFunctionInterface.accept(point);
            point = point.getNext();
        }
    }
}
