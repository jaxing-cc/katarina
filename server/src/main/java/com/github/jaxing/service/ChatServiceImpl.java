package com.github.jaxing.service;

import com.github.jaxing.common.domain.ChatListItem;
import com.github.jaxing.common.domain.ChatMessage;
import com.github.jaxing.common.domain.Client;
import com.github.jaxing.common.domain.UserInfo;
import com.github.jaxing.common.dto.ChatListItemVO;
import com.github.jaxing.common.dto.OfflineMessageCountVO;
import com.github.jaxing.common.enums.CollectionEnum;
import com.github.jaxing.common.enums.MessageTypeEnum;
import com.github.jaxing.common.utils.PageUtils;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cjxin
 * @date 2023/07/31
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatService chatService;

    @Resource
    private UserService userService;

    @Resource
    private MongoClient mongoClient;

    /**
     * 发送信息
     *
     * @param currentUser 当前用户
     * @param chatMessage 消息
     * @return 是否成功
     */
    @Override
    public Future<Void> sendChatMessage(User currentUser, ChatMessage chatMessage) {
        Promise<Void> promise = Promise.promise();
        if (chatMessage.getGroupMessage()) {
            promise.fail("暂不支持群组");
            return promise.future();
        }
        Client client = Client.CLIENT_POOL.get(chatMessage.getTo());
        chatMessage.setMessageId(ObjectId.get().toHexString());
        chatMessage.setCreateTime(System.currentTimeMillis());
        chatMessage.setFrom(currentUser.principal().getString("uid"));
        if (ObjectUtils.isEmpty(client)) {
            //离线消息
            chatMessage.setOfflineMessage(true);
        } else {
            chatMessage.setOfflineMessage(false);
            client.sendText(MessageTypeEnum.CHAT_MESSAGE.message(chatMessage).toString());
        }
        mongoClient.insert(CollectionEnum.message_bucket.name(), JsonObject.mapFrom(chatMessage), res -> {
            if (res.succeeded()) {
                promise.complete(null);
            } else {
                promise.fail(res.cause());
            }
        });
        return promise.future();
    }

    /**
     * 增加聊天列表
     *
     * @param cUid 当前用户
     * @param tUid 目标用户
     */
    @Override
    public Future<Void> addChatListItem(String cUid, String tUid) {
        Promise<Void> promise = Promise.promise();
        long creatTime = System.currentTimeMillis();
        mongoClient.findOneAndReplaceWithOptions(
                CollectionEnum.chat_list.name(),
                JsonObject.of("$and", JsonArray.of(JsonObject.of("uid", cUid), JsonObject.of("chatTargetUid", tUid))),
                JsonObject.mapFrom(new ChatListItem(cUid, tUid, creatTime)),
                new FindOptions().setFields(JsonObject.of("_id", 0)),
                new UpdateOptions().setUpsert(true),
                res -> {
                    if (res.succeeded()) {
                        promise.complete(null);
                    } else {
                        promise.fail(res.cause());
                    }
                });
        return promise.future();
    }

    /**
     * 删除聊天列表
     *
     * @param cUid 当前用户
     * @param tUid 目标用户
     */
    @Override
    public Future<Void> deleteChatListItem(String cUid, String tUid) {
        Promise<Void> promise = Promise.promise();
        mongoClient.removeDocuments(
                CollectionEnum.chat_list.name(),
                JsonObject.of("$and", JsonArray.of(JsonObject.of("uid", cUid), JsonObject.of("chatTargetUid", tUid))),
                res -> {
                    if (res.succeeded()) {
                        promise.complete(null);
                    } else {
                        promise.fail(res.cause());
                    }
                });

        return promise.future();
    }

    /**
     * 查询用户的聊天列表,分页一次加载20页
     *
     * @param uid  当前用户
     * @param size
     */
    @Override
    public Future<List<ChatListItemVO>> chatList(String uid, Integer page, Integer size) {
        Promise<List<ChatListItemVO>> promise = Promise.promise();
        mongoClient.findWithOptions(
                CollectionEnum.chat_list.name(),
                JsonObject.of("uid", uid),
                new FindOptions()
                        .setFields(JsonObject.of("_id", 0))
                        .setSkip(PageUtils.getSkip(page, size))
                        .setLimit(size)
                        .setSort(JsonObject.of("createTime", -1)))
                .onFailure(promise::fail)
                .onSuccess(res -> {
                    List<ChatListItem> itemList = res.stream().map(j -> j.mapTo(ChatListItem.class)).collect(Collectors.toList());
                    if (itemList.isEmpty()) {
                        promise.complete(Collections.emptyList());
                        return;
                    }
                    Object[] $oids = itemList.stream().map(ChatListItem::getChatTargetUid).distinct().map(id -> JsonObject.of("$oid", id)).toArray();
                    mongoClient.findWithOptions(
                            CollectionEnum.user.name(),
                            JsonObject.of("_id", JsonObject.of("$in", JsonArray.of($oids))),
                            new FindOptions().setFields(JsonObject.of("_id", 1, "name", 1, "avatar", 1, "gender", 1))
                    ).onFailure(promise::fail).onSuccess(usersResp -> {
                        Map<String, JsonObject> userInfo = usersResp.stream().collect(Collectors.toMap(u -> u.getString("_id"), u -> u));
                        promise.complete(itemList.stream().map(i -> {
                            ChatListItemVO chatListItemVO = new ChatListItemVO();
                            JsonObject entries = userInfo.get(i.getChatTargetUid());
                            chatListItemVO.setInfo(i);
                            if (!ObjectUtils.isEmpty(entries)) {
                                chatListItemVO.setUser(entries.mapTo(UserInfo.class));
                            }
                            return chatListItemVO;
                        }).collect(Collectors.toList()));
                    });
                });
        return promise.future();
    }

    /**
     * 加载用户离线消息数量
     *
     * @param uid 用户
     */
    @Override
    public Future<List<OfflineMessageCountVO>> offlineMessageCount(String uid) {
        Promise<List<OfflineMessageCountVO>> promise = Promise.promise();
        mongoClient.findWithOptions(
                CollectionEnum.message_bucket.name(),
                JsonObject.of("$and", JsonArray.of(JsonObject.of("to", uid), JsonObject.of("offlineMessage", true))),
                new FindOptions().setFields(JsonObject.of("from", 1, "_id", 1))
        ).onFailure(promise::fail).onSuccess(res -> {
            Map<String, Integer> map = new HashMap<>();
            for (JsonObject msgSender : res) {
                String fromId = msgSender.getString("from");
                map.put(fromId, map.getOrDefault(fromId, 0) + 1);
            }
            promise.complete(map.entrySet().stream().map(e -> new OfflineMessageCountVO(e.getKey(), e.getValue())).collect(Collectors.toList()));
        });
        return promise.future();
    }

}
