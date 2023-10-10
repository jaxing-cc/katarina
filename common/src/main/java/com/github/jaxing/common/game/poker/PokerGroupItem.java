package com.github.jaxing.common.game.poker;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PokerGroupItem {
    private byte pokerId;
    private PokerGroupItem next;
    private PokerGroupItem last;

    public PokerGroupItem() {
    }

    public PokerGroupItem(byte id) {
        this.pokerId = id;
    }

    public Poker get() {
        return PokerFactory.get(pokerId);
    }

    public byte val() {
        return get().getValue();
    }

    @Override
    public String toString() {
        return get().toString();
    }
}
