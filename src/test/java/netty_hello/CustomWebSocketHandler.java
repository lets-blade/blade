package netty_hello;

import com.blade.ioc.annotation.Inject;
import com.blade.mvc.annotation.WebSocket;
import com.blade.mvc.handler.WebSocketHandler;
import com.blade.mvc.websocket.WebSocketContext;

/**
 * @author darren
 * @date 2018-12-10 21:27
 */
@WebSocket("/ws_impl")
public class CustomWebSocketHandler implements WebSocketHandler {

    @Inject
    CService cService;

    @Override
    public void onConnect(WebSocketContext ctx) {
        cService.sayHello();
        System.out.println("ws from implements interface:onConnect:"+ctx.getSession().getUuid());
    }

    @Override
    public void onText(WebSocketContext ctx) {
        System.out.println("ws from implements interface:onText:"+ctx.getSession().getUuid() + " said:" + ctx.getReqText());
    }

    @Override
    public void onDisConnect(WebSocketContext ctx) {
        System.out.println("ws from implements interface:onDisConnect:"+ctx.getSession().getUuid() + " disconnect");
    }
}
