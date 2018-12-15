package com.blade.server.netty;

import com.blade.Blade;
import com.blade.mvc.websocket.WebSocketContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Http Server Handler
 *
 * @author biezhi,darren
 * 2017/5/31,
 */
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;
    private WebSocketContext context;
    private com.blade.mvc.handler.WebSocketHandler handler;
    private Blade blade;


    public WebSocketHandler(Blade blade) {
        this.blade = blade;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            handleHttpRequest(ctx, (HttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) {
        if (isWebSocketRequest(req)) {
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(req.uri(), null, true);
            this.handshaker = wsFactory.newHandshaker(req);
            if (this.handshaker == null) {
                //Return that we need cannot not support the web socket version
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                this.handshaker.handshake(ctx.channel(), req);
                this.context = new WebSocketContext(ctx);
                this.handler.onConnect(context);
            }
        } else {
            ctx.fireChannelRead(req);
        }
    }

    /**
     * Only supported TextWebSocketFrame
     *
     * @param ctx
     * @param frame
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            this.handler.onDisConnect(this.context);
            this.handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException("unsupported frame type: " + frame.getClass().getName());
        }
        this.context.setReqText(((TextWebSocketFrame) frame).text());
        this.handler.onText(this.context);
    }


    private boolean isWebSocketRequest(HttpRequest req){
        return req != null
                && (this.handler = this.blade.routeMatcher().getWebSocket(req.uri())) != null
                && req.decoderResult().isSuccess()
                && "websocket".equals(req.headers().get("Upgrade"));
    }

}