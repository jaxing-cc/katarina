package com.github.jaxing.common.enums.game.poker;

public enum PokerType {
    //0
    SPADE("黑桃",'♠'),
    //1
    HEARTS("红心",'♥'),
    //2
    CLUBS("梅花",'♣'),
    //3
    DIAMONDS("方片",'♦'),
    //4
    JOKER("小王",'s'),
    //5
    SUPER_JOKER("大王",'S');
    public String name;
    public char tag;
    PokerType(String name,char tag){
        this.name = name;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return name;
    }
}
