package netty_hello;

import com.blade.mvc.annotation.OnMessage;
import com.blade.mvc.annotation.WebSocket;
import com.blade.mvc.websocket.WebSocketContext;

/**
 * @author darren
 * @description
 * @date 2018/12/18 11:01
 */
@WebSocket("/ws_anno")
public class CustomWebSocketHandlerAnno extends BaseWebSocketHandler {

    @OnMessage
    public void OnMessage(WebSocketContext ctx) {
        System.out.println("ws from annotation @OnMessage:" + ctx.getSession().getUuid() + " said:" + ctx.getReqText());
    }
}
