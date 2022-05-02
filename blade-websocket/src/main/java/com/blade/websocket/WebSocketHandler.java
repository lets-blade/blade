package com.blade.websocket;

/**
 * @author hellokaton
 * @date 2022/5/2
 */
public interface WebSocketHandler {

    void onConnect(WebSocketContext ctx);

    void onText(WebSocketContext ctx);

    void onDisConnect(WebSocketContext ctx);

}
