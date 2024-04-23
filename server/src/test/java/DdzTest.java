import com.github.jaxing.common.game.Player;
import com.github.jaxing.common.game.poker.Poker;
import com.github.jaxing.common.game.poker.PokerFactory;
import com.github.jaxing.common.game.poker.ddz.DdzContext;
import com.github.jaxing.service.DdzService;
import com.github.jaxing.service.DdzServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * @author cjxin
 * @date 2024/04/16
 */
public class DdzTest {


    @Test
    public void test(){
        String a = "a";
        String b = "b";
        String c = "c";

        DdzService service = new DdzServiceImpl();
        // String roomId = service.createRoomAndJoin(a);
        // print();
        // service.joinRoom(b, roomId);
        // print();
        // service.joinRoom(c, roomId);
        // print();
        // service.exit(b);
        // print();
        // service.joinRoom(b, roomId);
        // print();
        for (int i = 0; i < 54; i++) {
            Poker p = PokerFactory.get((byte) i);
            System.out.printf("{\"id\":%d, \"value\":%d, \"type\": %s},\n",p.getId(), p.getValue(), p.getType().name);
        }

    }

    private void print(){
        Map<String, DdzContext> roomMap = DdzContext.ROOM_MAP;
        for (Player value : Player.PLAYER_MAP.values()) {
            System.out.println(value);
        }
        for (DdzContext value : roomMap.values()) {
            System.out.println(value);
        }
    }
}
