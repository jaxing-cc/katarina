package com.github.jaxing.job;

import com.github.jaxing.common.domain.Client;
import com.github.jaxing.common.domain.Message;
import com.github.jaxing.common.enums.MessageTypeEnum;
import com.github.jaxing.common.game.Player;
import com.github.jaxing.common.game.poker.ddz.DdzContext;
import com.github.jaxing.service.DdzService;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author cjxin
 * @date 2024/04/19
 */
@Slf4j
@Component
public class DdzJob implements Handler<Long> {
    /**
     * Something has happened, so handle it.
     *
     * @param event the event to handle
     */
    @Resource
    private DdzService ddzService;

    @Override
    public void handle(Long event) {
        log.info("cur:{}", Client.CLIENT_POOL.size());
        Map<String, Client> clientPool = Client.CLIENT_POOL;
        Map<String, DdzContext> roomMap = DdzContext.ROOM_MAP;
        Player.PLAYER_MAP.forEach((k, v) -> {
            Client client = clientPool.get(k);
            if (client == null) {
                // 用户已经离线
                ddzService.exit(k);
                return;
            }
            DdzContext context = roomMap.get(v.getRoomId());
            if (context != null){
                String text = MessageTypeEnum.DDZ_MESSAGE.message(context.toJson()).toString();
                client.sendText(text);
                log.info(text);
            }
        });
    }
}
