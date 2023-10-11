package com.github.jaxing.common.enums.game.poker;

import com.github.jaxing.common.game.poker.PokerGameRule;
import com.github.jaxing.common.game.poker.ddz.DdzGameRule;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author cjxin
 * @date 2023/10/10
 */
@AllArgsConstructor
@Getter
public enum PokerGame {

    DOU_DI_ZHU(new DdzGameRule());

    private final PokerGameRule pokerGameRule;
}
