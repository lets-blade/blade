package com.blade.mvc.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Data;

/**
 * @author biezhi
 * @date 2017/10/30
 */
@Data
public class WebSocketContext {

    private ChannelHandlerContext ctx;
    private WebSocketSession      session;
    private String                reqText;

    public WebSocketContext(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.session = new WebSocketSession(ctx.channel());
    }

    public void message(String value) {
        ctx.writeAndFlush(new TextWebSocketFrame(value));
    }

}
