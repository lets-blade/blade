package com.blade.server.netty;

import com.blade.Blade;
import com.blade.mvc.websocket.WebSocketContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Http Server Handler
 *
 * @author biezhi,darren
 * 2017/5/31,
 */
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;
    private Blade blade;


    public WebSocketHandler(Blade blade) {
        this.blade = blade;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
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
        if (isWebSocketPath(req)) {
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(req.uri(), null, true);
            this.handshaker = wsFactory.newHandshaker(req);
            if (this.handshaker == null) {
                //Return that we need cannot not support the web socket version
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                this.handshaker.handshake(ctx.channel(), req);
                this.blade.webSocketHandler().onConnect(new WebSocketContext(ctx));
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
            this.blade.webSocketHandler().onDisConnect(new WebSocketContext(ctx));
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
        WebSocketContext wsCtx = new WebSocketContext(ctx);
        wsCtx.setReqText(((TextWebSocketFrame) frame).text());
        this.blade.webSocketHandler().onText(wsCtx);
    }


    private boolean isWebSocketPath(HttpRequest req){
        return req != null
                && Objects.equals(req.uri(),blade.webSocketPath())
                && req.decoderResult().isSuccess()
                && "websocket".equals(req.headers().get("Upgrade"));
    }

}