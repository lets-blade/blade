package netty_hello;

import com.blade.mvc.annotation.WebSocket;
import com.blade.mvc.handler.WebSocketHandler;
import com.blade.mvc.websocket.WebSocketContext;

/**
 * @author darren
 * @date 2018-12-10 21:27
 */
@WebSocket(value="/websocket_anno_value",path = {"/websocket_anno_path","/websocket_anno_value"})
public class CustomWebSocketHandler implements WebSocketHandler {
    @Override
    public void onConnect(WebSocketContext ctx) {
        System.out.println("connect success:session="+ctx.getSession().getUuid());
    }

    @Override
    public void onText(WebSocketContext ctx) {
        System.out.println(ctx.getSession().getUuid() + " said:" + ctx.getReqText());
    }

    @Override
    public void onDisConnect(WebSocketContext ctx) {
        System.out.println(ctx.getSession().getUuid() + " disconnect");
    }
}
