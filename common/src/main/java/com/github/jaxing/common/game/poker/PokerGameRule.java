package com.github.jaxing.common.game.poker;

import com.github.jaxing.common.enums.game.poker.PokerGame;
import com.github.jaxing.common.enums.game.poker.PokerGroupType;

/**
 * @author cjxin
 * @date 2023/10/10
 */
public interface PokerGameRule {

    PokerGame getGame();

    /**
     * a 是否大于 b
     *
     * @param a 牌组a
     * @param b 牌组b
     * @return 是否大于
     */
    boolean compareTo(PokerGroup a, PokerGroup b);

    /**
     * 输入检查
     * @param s
     * @return
     */
    PokerGroupType checkInput(String s);
}
