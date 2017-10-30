package com.blade.mvc.handler;

import com.blade.mvc.websocket.WebSocketContext;

/**
 * @author biezhi
 * @date 2017/10/30
 */
public interface WebSocketHandler {

    void onConnect(WebSocketContext ctx);

    void onText(WebSocketContext ctx);

    void onDisConnect(WebSocketContext ctx);

}
