package com.github.jaxing.service;

import com.github.jaxing.common.domain.ChatMessage;
import com.github.jaxing.common.domain.Client;
import com.github.jaxing.common.enums.CollectionEnum;
import com.github.jaxing.common.enums.MessageTypeEnum;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.mongo.MongoClient;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * @author cjxin
 * @date 2023/07/31
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatService chatService;

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
        // mongoClient.insert(CollectionEnum.message_bucket.name(), JsonObject.mapFrom(chatMessage), res -> {
        //     if (res.succeeded()) {
        //         promise.complete(null);
        //     } else {
        //         promise.fail(res.cause());
        //     }
        // });
        promise.complete(null);
        return promise.future();
    }

}
