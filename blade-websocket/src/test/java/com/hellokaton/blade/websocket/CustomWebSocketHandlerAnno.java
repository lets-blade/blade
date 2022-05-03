package com.hellokaton.blade.websocket;

import com.hellokaton.blade.websocket.annotaion.OnMessage;
import com.hellokaton.blade.websocket.annotaion.WebSocket;

/**
 * @author darren
 * @description
 * @date 2018/12/18 11:01
 */
@WebSocket("/ws_anno")
public class CustomWebSocketHandlerAnno extends BaseWebSocketHandler {

    @OnMessage
    public void OnMessage(WebSocketContext ctx) {
        System.out.println("ws from annotation @OnMessage:" + ctx.session().uuid() + " said:" + ctx.message());
    }

}
