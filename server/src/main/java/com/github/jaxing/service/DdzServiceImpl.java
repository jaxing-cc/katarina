package com.github.jaxing.service;

import com.github.jaxing.common.enums.game.poker.GameStatus;
import com.github.jaxing.common.enums.game.poker.PokerGame;
import com.github.jaxing.common.enums.game.poker.PokerGroupType;
import com.github.jaxing.common.game.Player;
import com.github.jaxing.common.game.poker.*;
import com.github.jaxing.common.game.poker.ddz.DdzContext;
import com.github.jaxing.common.utils.CommonUtils;
import com.github.jaxing.dto.vo.DdzRoomVO;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cjxin
 * @date 2024/04/16
 */
@Slf4j
@Service
public class DdzServiceImpl implements DdzService {

    @Resource
    private UserService userService;

    private PokerGameRule gameRule = PokerGame.DOU_DI_ZHU.getPokerGameRule();

    @Override
    public Future<List<DdzRoomVO>> roomList(String name) {
        List<DdzRoomVO> list = DdzContext.ROOM_MAP.values().stream().map(c -> {
            DdzRoomVO ddzRoomVO = new DdzRoomVO();
            ddzRoomVO.setStatus(c.getGameStatus());
            ddzRoomVO.setId(c.getId());
            ddzRoomVO.setName(c.getName());
            ddzRoomVO.setPlayerSize(c.getSize().get());
            return ddzRoomVO;
        }).collect(Collectors.toList());
        return Future.succeededFuture(list);
    }

    /**
     * 创建房间
     *
     * @return 房间id
     */
    @Override
    public Future<String> createRoomAndJoin(String uid, String name) {
        Promise<String> promise = Promise.promise();
        getPlayerByUid(uid).onSuccess(r -> promise.fail("已在房间内")).onFailure(t -> {
            Map<String, DdzContext> roomMap = DdzContext.ROOM_MAP;
            DdzContext room = new DdzContext(name);
            String id = room.getId();
            roomMap.put(id, room);
            joinRoom(uid, id).onFailure(promise::fail).onSuccess(v -> promise.complete(id));
        });
        return promise.future();
    }

    /**
     * 加入
     *
     * @param roomId 房间id
     */
    @Override
    public Future<Void> joinRoom(String uid, String roomId) {
        Promise<Void> promise = Promise.promise();
        getPlayerByUid(uid).onSuccess(r -> promise.fail("已在房间内")).onFailure(t -> getContextByRoomId(roomId).onSuccess(context -> {
            userService.findById(uid).onFailure(promise::fail).onSuccess(u -> {
                Player player = Player.builder()
                        .id(uid)
                        .ready(false)
                        .name(u.getName())
                        .avatar(u.getAvatar())
                        .gender(u.getGender())
                        .roomId(roomId)
                        .build();
                Player.PLAYER_MAP.put(player.getId(), player);
                context.addPlayer(player);
                promise.complete();
            });
        }).onFailure(promise::fail));
        return promise.future();
    }

    /**
     * 退出
     */
    @Override
    public Future<Void> exit(String uid) {
        Promise<Void> promise = Promise.promise();
        getPlayerByUid(uid).onFailure(promise::fail).onSuccess(
                player -> getContextByRoomId(player.getRoomId()).onFailure(promise::fail).onSuccess(context -> {
                    if (context.getGameStatus().equals(GameStatus.WAIT)) {
                        context.removePlayer(player);
                    } else {
                        context.removePlayer(player);
                        context.setGameStatus(GameStatus.WAIT);
                        context.setMaster(null);
                        context.setCurrent(null);
                        context.setCallList(new Integer[]{null, null, null});
                    }
                    promise.complete();
                })
        );
        return promise.future();
    }

    /**
     * 准备/开始
     */
    @Override
    public Future<Void> ready(String uid) {
        Promise<Void> promise = Promise.promise();
        getPlayerByUid(uid).onFailure(promise::fail).onSuccess(player -> getContextByRoomId(player.getRoomId()).onFailure(promise::fail).onSuccess(c -> {
            if (!c.getGameStatus().equals(GameStatus.WAIT)) {
                promise.fail("游戏进行中不能进行此操作");
                return;
            }
            player.setReady(!player.isReady());
            boolean allReady = true;
            for (Player p : c.getPlayers()) {
                if (p == null || !p.isReady()) {
                    allReady = false;
                    break;
                }
            }
            if (allReady) {
                c.setGameStatus(GameStatus.CALL);
                c.setPokerGroups(PokerFactory.wash());
                c.setCurrent(CommonUtils.random(0, 3));
                c.setMaster(c.getCurrent());
            }
            promise.complete();
        }));
        return promise.future();
    }


    /**
     * 叫
     *
     * @param v 分数
     */
    @Override
    public Future<Void> callMaster(String uid, Integer v) {
        Promise<Void> promise = Promise.promise();
        if (v == null || v < 0 || v > 3) {
            promise.fail("数据格式错误");
            return promise.future();
        }
        getPlayerByUid(uid).onFailure(promise::fail).onSuccess(player -> getContextByRoomId(player.getRoomId()).onFailure(promise::fail).onSuccess(c -> {
            if (!c.getGameStatus().equals(GameStatus.CALL)) {
                promise.fail("不能进行此操作");
                return;
            }
            Integer playerIndex = c.getPlayerMap().get(uid);
            Integer currentIndex = c.getCurrent();
            if (!playerIndex.equals(currentIndex)) {
                promise.fail("不是你的回合");
                return;
            }
            Integer[] callList = c.getCallList();
            //叫地主逻辑
            Integer max = getMax(callList)[0];
            // 数值小于上一个就return
            if (max != null && v != 0 && v <= max) {
                promise.fail("不能小于等于上家分数");
                return;
            }
            // 3直接开始
            if (v.equals(3)) {
                callList[currentIndex] = v;
                c.start(currentIndex);
            } else if (callList[currentIndex] == null) {
                callList[currentIndex] = v;
                if (Arrays.stream(callList).allMatch(Objects::nonNull)) {
                    if (Arrays.stream(callList).allMatch(num -> num.equals(0))) {
                        // 全不叫给地主
                        c.start(c.getMaster());
                    } else {
                        // 顶到3给最大的
                        Integer[] maxInfo = getMax(callList);
                        if (maxInfo[0] == 3) {
                            c.start(maxInfo[1]);
                        } else {
                            c.goNext();
                        }
                    }
                } else {
                    c.goNext();
                }
            } else {
                callList[currentIndex] = v;
                if (v == 0) {
                    c.start(getMax(callList)[1]);
                } else {
                    c.start(currentIndex);
                }
            }
            promise.complete();
        }));
        return promise.future();
    }

    private Integer[] getMax(Integer[] callList) {
        Integer max = null;
        Integer index = null;
        for (int i = 0; i < callList.length; i++) {
            if (max == null && callList[i] != null) {
                max = callList[i];
                index = i;
            } else if (callList[i] != null && callList[i] > max) {
                max = callList[i];
                index = i;
            }
        }
        return new Integer[]{max, index};
    }

    /**
     *
     */
    @Override
    public Future<Void> pop(String uid, List<Byte> ids) {
        Promise<Void> promise = Promise.promise();
        getPlayerByUid(uid).onFailure(promise::fail).onSuccess(player -> getContextByRoomId(player.getRoomId()).onFailure(promise::fail).onSuccess(c -> {
            String exp = PokerFactory.getExpByIds(ids);
            ComparablePokerGroup targetGroup = ComparablePokerGroup.get(exp, PokerGame.DOU_DI_ZHU);
            if (targetGroup == null) {
                promise.fail("错误出牌");
                return;
            }
            Integer playerIndex = c.getPlayerMap().get(uid);
            Integer currentIndex = c.getCurrent();
            if (!playerIndex.equals(currentIndex)) {
                promise.fail("不是你的回合");
                return;
            }
            List<Byte> lastPush = c.getLastPush();
            Integer lastPushIndex = c.getLastPushIndex();
            PokerGroup pokerGroup = c.getPokerGroups()[playerIndex];
            if (ObjectUtils.isEmpty(lastPush)) {
                if (ObjectUtils.isEmpty(ids)) {
                    promise.fail("此轮必须出牌");
                    return;
                }
                pop(pokerGroup, targetGroup, c, ids, currentIndex, promise);
            } else {
                String lastPushExp = PokerFactory.getExpByIds(lastPush);
                ComparablePokerGroup lastPushExpGroup = ComparablePokerGroup.get(lastPushExp, PokerGame.DOU_DI_ZHU);
                if (targetGroup.getType().equals(PokerGroupType.PASS)) {
                    if (currentIndex.equals(lastPushIndex)) {
                        promise.fail("此轮必须出牌");
                        return;
                    }
                    c.goNext();
                    promise.complete();
                } else if (currentIndex.equals(lastPushIndex) || gameRule.compareTo(targetGroup, lastPushExpGroup)) {
                    pop(pokerGroup, targetGroup, c, ids, currentIndex, promise);
                } else {
                    promise.fail("无法出牌");
                }
            }
        }));
        return promise.future();
    }

    /**
     * 重开
     */
    @Override
    public Future<Void> restart(String uid) {
        Promise<Void> promise = Promise.promise();
        getPlayerByUid(uid).onFailure(promise::fail).onSuccess(player -> getContextByRoomId(player.getRoomId()).onFailure(promise::fail).onSuccess(c -> {
            c.setGameStatus(GameStatus.WAIT);
            c.setMaster(null);
            c.setCurrent(null);
            c.setCallList(new Integer[]{null, null, null});
            c.setLastPush(null);
            c.setLastPushIndex(null);
            for (Player cPlayer : c.getPlayers()) {
                if (cPlayer != null){
                    cPlayer.setReady(false);
                }
            }
            promise.complete();
        }));
        return promise.future();
    }

    private void pop(PokerGroup pokerGroup,
                     ComparablePokerGroup targetGroup,
                     DdzContext c, List<Byte> ids, Integer currentIndex,
                     Promise<Void> promise) {
        if (pokerGroup.pop(targetGroup) == null) {
            promise.fail("无法出牌");
        } else {
            if (pokerGroup.getSize() == 0) {
                c.setGameStatus(GameStatus.FINISH);
            } else {
                c.goNext();
            }
            c.setLastPush(ids);
            c.setLastPushIndex(currentIndex);
            promise.complete();
        }
    }

    /**
     * 获取用户上下文
     *
     * @param roomId 房间号
     * @return 上下文
     */
    private Future<DdzContext> getContextByRoomId(String roomId) {
        Map<String, DdzContext> roomMap = DdzContext.ROOM_MAP;
        DdzContext context = roomMap.get(roomId);
        if (context == null) {
            return Future.failedFuture("房间不存在");
        }
        return Future.succeededFuture(context);
    }

    /**
     * 获取玩家信息
     *
     * @param uid 用户id
     * @return 玩家信息
     */
    private Future<Player> getPlayerByUid(String uid) {
        Player player = Player.PLAYER_MAP.get(uid);
        if (ObjectUtils.isEmpty(player)) {
            return Future.failedFuture("未加入房间");
        }
        return Future.succeededFuture(player);
    }
}
