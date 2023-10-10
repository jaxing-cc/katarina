package com.github.jaxing.common.game.poker;

import com.github.jaxing.common.enums.game.poker.PokerType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Poker {

    private final byte id;

    private final byte value;

    private final PokerType type;

    @Override
    public String toString() {
        return id + ":" + type + "@" + value;
    }
}
