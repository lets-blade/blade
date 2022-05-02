package com.blade.websocket;

import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author biezhi,darren
 * @date 2017/10/30
 */
@Accessors(fluent = true)
public class WebSocketContext {

    @Getter
    private WebSocketSession      session;
    @Getter
    private String                message;
    private WebSocketHandler      handler;

    public WebSocketContext(WebSocketSession session,WebSocketHandler handler) {
        this.session = session;
        this.handler = handler;
    }
    public WebSocketContext(WebSocketSession session,WebSocketHandler handler,String message) {
        this(session,handler);
        this.message = message;
    }

    /**
     * post a message
     * @param value
     */
    public void message(String value) {
        session.handlerContext().writeAndFlush(new TextWebSocketFrame(value));
    }

    /**
     * Allows the user to disconnect the websocket
     */
    public void disconnect(){
        session.handlerContext().disconnect().addListener(ChannelFutureListener.CLOSE);
        handler.onDisConnect(this);
    }

}
