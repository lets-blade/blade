package netty_hello;

import com.blade.mvc.annotation.OnClose;
import com.blade.mvc.annotation.OnOpen;
import com.blade.mvc.websocket.WebSocketContext;

/**
 * @author darren
 * @description
 * @date 2018/12/18 13:29
 */
public abstract class BaseWebSocketHandler {


    @OnOpen
    public void OnOpen(WebSocketContext ctx) {
        System.out.println("ws from annotation @OnOpen:" + ctx.getSession().getUuid());
    }

    @OnClose
    public void OnClose(WebSocketContext ctx) {
        System.out.println("ws from annotation @OnClose:" + ctx.getSession().getUuid() + " disconnect");
    }
}
