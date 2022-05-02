package com.blade.websocket;

import com.blade.ioc.annotation.Inject;
import com.blade.websocket.annotaion.WebSocket;

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
        System.out.println("ws from implements interface:onConnect:"+ctx.session().uuid());
    }

    @Override
    public void onText(WebSocketContext ctx) {
        System.out.println("ws from implements interface:onText:"+ctx.session().uuid() + " said:" + ctx.message());
    }

    @Override
    public void onDisConnect(WebSocketContext ctx) {
        System.out.println("ws from implements interface:onDisConnect:"+ctx.session().uuid() + " disconnect");
    }
}
