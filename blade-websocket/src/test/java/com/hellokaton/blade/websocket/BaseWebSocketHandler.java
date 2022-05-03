package com.hellokaton.blade.websocket;

import com.hellokaton.blade.websocket.annotaion.OnClose;
import com.hellokaton.blade.websocket.annotaion.OnOpen;

/**
 * @author darren
 * @description
 * @date 2018/12/18 13:29
 */
public abstract class BaseWebSocketHandler {


    @OnOpen
    public void OnOpen(WebSocketContext ctx) {
        System.out.println("ws from annotation @OnOpen:" + ctx.session().uuid());
    }

    @OnClose
    public void OnClose(WebSocketContext ctx) {
        System.out.println("ws from annotation @OnClose:" + ctx.session().uuid() + " disconnect");
    }
}
