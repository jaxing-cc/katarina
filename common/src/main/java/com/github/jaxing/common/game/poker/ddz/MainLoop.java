package com.github.jaxing.common.game.poker.ddz;

import com.github.jaxing.common.enums.game.poker.PokerGame;
import com.github.jaxing.common.exception.ServiceException;
import com.github.jaxing.common.game.PlayerDetails;
import com.github.jaxing.common.game.poker.ComparablePokerGroup;
import com.github.jaxing.common.game.poker.Poker;
import com.github.jaxing.common.game.poker.PokerFactory;
import com.github.jaxing.common.game.poker.PokerGroup;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cjxin
 * @date 2023/10/13
 */
@Data
public class MainLoop {

    private PokerGame pokerGame;

    private long startTime;

    private List<PlayerDetails> players;

    private Map<String, PokerGroup> pokerGroupMap;

    private Poker[] specialPokerGroup;

    private int masterIndex;

    private int loopIndex;

    private int lastPlayerIndex;

    private ComparablePokerGroup lastPokerGroup;

    /**
     * 初始化游戏
     *
     * @param a 玩家a
     * @param b 玩家b
     * @param c 玩家c
     */
    public MainLoop(PlayerDetails a, PlayerDetails b, PlayerDetails c, PokerGame game) {
        PokerGroup[] wash = PokerFactory.wash();
        AtomicInteger index = new AtomicInteger();
        this.specialPokerGroup = new Poker[3];
        wash[0].forEach(item -> this.specialPokerGroup[index.getAndIncrement()] = item.get());
        this.startTime = System.currentTimeMillis();
        this.players = Arrays.asList(a, b, c);
        this.loopIndex = this.masterIndex = (int) (Math.random() * players.size());
        this.lastPlayerIndex = -1;
        this.pokerGroupMap = new HashMap<>();
        this.pokerGroupMap.put(a.getId(), wash[1]);
        this.pokerGroupMap.put(b.getId(), wash[2]);
        this.pokerGroupMap.put(c.getId(), wash[3]);
        this.pokerGroupMap.get(master().getId()).put(wash[0]);
        pokerGroupMap.values().forEach(PokerGroup::sort);
    }

    /**
     * 地主
     *
     * @return 角色
     */
    public PlayerDetails master() {
        return players.get(this.masterIndex);
    }

    private String indexToPlayId(int index) {
        return this.players.get(index).getId();
    }

    public void play(String id, String exp) {
        if (!this.pokerGroupMap.containsKey(id)) {
            throw new ServiceException("操作失败");
        }
        ComparablePokerGroup comparablePokerGroup = ComparablePokerGroup.get(exp, pokerGame);
        if (comparablePokerGroup == null) {
            throw new ServiceException("出牌格式错误");
        }
        if (!indexToPlayId(this.loopIndex).equals(id)) {
            throw new ServiceException("当前不是你的回合");
        }
        byte[] pokerIds = null;
        PokerGroup pokers = this.pokerGroupMap.get(id);
        if (this.lastPlayerIndex < 0 ||
                indexToPlayId(this.lastPlayerIndex).equals(id)) {
            pokerIds = pokers.pop(comparablePokerGroup);
        }
        if (pokerIds == null) {
            throw new ServiceException("不满足出牌条件");
        }

    }

    public static void main(String[] args) {
        PlayerDetails a = PlayerDetails.builder().id("A").build();
        PlayerDetails b = PlayerDetails.builder().id("B").build();
        PlayerDetails c = PlayerDetails.builder().id("C").build();
        MainLoop obj = new MainLoop(a, b, c, PokerGame.DOU_DI_ZHU);
        JsonObject entries = JsonObject.mapFrom(obj);
        System.out.println(entries.toString());
    }
}
