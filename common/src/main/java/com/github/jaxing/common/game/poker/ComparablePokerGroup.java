package com.github.jaxing.common.game.poker;

import com.github.jaxing.common.enums.game.poker.PokerGame;
import com.github.jaxing.common.enums.game.poker.PokerGroupType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComparablePokerGroup {
    private int[] countArray;
    private PokerGroupType type;
    private int size;

    public static ComparablePokerGroup get(String exp, PokerGame pokerGame) {
        PokerGameRule pokerGameRule = pokerGame.getPokerGameRule();
        PokerGroupType pokerGroupType = pokerGameRule.checkInput(exp);
        if (pokerGroupType == null) {
            return null;
        }
        ComparablePokerGroup comparablePokerGroup = new ComparablePokerGroup();
        comparablePokerGroup.setSize(exp.length());
        comparablePokerGroup.setCountArray(PokerFactory.getPokerArray(exp.toCharArray()));
        comparablePokerGroup.setType(pokerGroupType);
        return comparablePokerGroup;
    }
}
