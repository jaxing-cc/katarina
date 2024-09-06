package com.github.jaxing.service;

import com.github.jaxing.common.enums.game.poker.GameStatus;
import com.github.jaxing.common.game.Player;
import com.github.jaxing.common.game.poker.PokerFactory;
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
                        //TODO 其他状态退出处理
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
            int lastIndex = (currentIndex + 2) % 3;
            if (!playerIndex.equals(currentIndex)) {
                promise.fail("不是你的回合");
                return;
            }
            Integer[] callList = c.getCallList();
            //叫地主逻辑
            Integer last = callList[lastIndex];
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
                }else{
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
    public void pop(String uid, List<Byte> ids) {
        // Player player = getPlayerByUid(uid);
        // DdzContext context = getContextByRoomId(player.getRoomId());
        // if (!context.getGameStatus().equals(GameStatus.UNDERWAY)) {
        //     throw new ServiceException("当前不能做此操作");
        // }
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
