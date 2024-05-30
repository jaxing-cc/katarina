package com.github.jaxing.common.game.poker.ddz;

import com.github.jaxing.common.enums.game.poker.GameStatus;
import com.github.jaxing.common.exception.ServiceException;
import com.github.jaxing.common.game.Player;
import com.github.jaxing.common.game.poker.ComparablePokerGroup;
import com.github.jaxing.common.game.poker.Poker;
import com.github.jaxing.common.game.poker.PokerFactory;
import com.github.jaxing.common.game.poker.PokerGroup;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cjxin
 * @date 2024/04/16
 */
@Data
public class DdzContext implements Serializable {

    public static final Map<String, DdzContext> ROOM_MAP = new ConcurrentHashMap<>();

    /**
     * 房间号
     */
    private final String id;

    /**
     * 房间名
     */
    private final String name;

    /**
     * 房主id
     */
    private String ownerId;

    /**
     * 玩家列表
     */
    private volatile Player[] players;

    /**
     * key:玩家id value:players下标
     */
    private Map<String, Integer> playerMap;

    /**
     * 当前状态
     */
    private volatile GameStatus gameStatus;

    /**
     * 当前玩家数量
     */
    private volatile AtomicInteger size;

    /**
     * 地主
     */
    private volatile Integer master;

    /**
     * 当前玩家
     */
    private volatile Integer current;

    /**
     * 牌组
     */
    private PokerGroup[] pokerGroups;

    /**
     * 上次出牌
     */
    private Poker[] lastPush;

    /**
     * 叫牌数据表
     */
    private int[] callList;

    public DdzContext(String name) {
        String randomId;
        while (ROOM_MAP.containsKey(randomId = UUID.randomUUID().toString().substring(0, 6))) {
            randomId = UUID.randomUUID().toString().substring(0, 6);
        }
        this.id = randomId;
        this.name = name;
        this.playerMap = new HashMap<>();
        this.players = new Player[3];
        this.gameStatus = GameStatus.WAIT;
        this.size = new AtomicInteger();
        this.size.set(0);
        this.pokerGroups = PokerFactory.wash();
        this.callList = new int[3];
    }

    /**
     * 添加player
     *
     * @param player player
     */
    public void addPlayer(Player player) {
        String uid = player.getId();
        if (this.size.get() == 0) {
            this.ownerId = uid;
        }
        if (this.size.get() == 3) {
            throw new ServiceException("房间人数已满");
        }
        if (this.playerMap.containsKey(uid)) {
            throw new ServiceException("用户已在房间内");
        }
        for (int i = 0; i < this.players.length; i++) {
            if (players[i] == null) {
                this.players[i] = player;
                this.playerMap.put(player.getId(), i);
                break;
            }
        }
        this.size.incrementAndGet();
    }

    public void removePlayer(Player player) {
        String uid = player.getId();
        Integer index = playerMap.get(uid);
        if (ObjectUtils.isEmpty(index)) {
            throw new ServiceException("未加入房间");
        }
        playerMap.remove(uid);
        players[index] = null;
        Player.PLAYER_MAP.remove(uid);
        if (this.size.decrementAndGet() == 0) {
            ROOM_MAP.remove(this.id);
        }
    }

    public JsonObject toJson() {
        JsonObject entries = new JsonObject();
        entries.put("id", this.id);
        entries.put("name", this.name);
        entries.put("ownerId", this.ownerId);
        entries.put("players", this.players);
        entries.put("playerMap", this.playerMap);
        entries.put("gameStatus", this.gameStatus);
        entries.put("size", this.size);
        entries.put("master", this.master);
        entries.put("current", this.current);
        entries.put("callList", this.callList);
        entries.put("lastPush", this.lastPush);
        if (ObjectUtils.isEmpty(this.pokerGroups)){
            entries.put("pokerGroups", Collections.EMPTY_LIST);
        }else{
            List<List<Byte>> lists = new ArrayList<>();
            for (PokerGroup pokerGroup : pokerGroups) {
                List<Byte> pokers = pokerGroup.toPokerArray();
                lists.add(pokers);
            }
            entries.put("pokerGroups", lists);
        }
        return entries;
    }
}
