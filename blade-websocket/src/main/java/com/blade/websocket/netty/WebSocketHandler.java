package com.blade.websocket.netty;

import com.blade.Blade;
import com.blade.websocket.WebSocketContext;
import com.blade.websocket.WebSocketHandlerWrapper;
import com.blade.websocket.WebSocketSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * Http Server Handler
 *
 * @author biezhi,darren
 * 2017/5/31,
 */
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;
    private WebSocketSession session;
    private com.blade.websocket.WebSocketHandler handler;
    private String uri;
    private Blade blade;


    public WebSocketHandler(Blade blade) {
        this.blade = blade;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            handleHttpRequest(ctx, (HttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            initHandlerWrapper();
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        } else {
            ReferenceCountUtil.retain(msg);
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
                this.session = new WebSocketSession(ctx);
                this.uri = req.uri();
                initHandlerWrapper();
                //Allows the user to send messages in the event of onConnect
                CompletableFuture.completedFuture(new WebSocketContext(this.session,this.handler))
                        .thenAcceptAsync(this.handler::onConnect,ctx.executor());
            }
        } else {
            ReferenceCountUtil.retain(req);
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
            this.handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            CompletableFuture.completedFuture(new WebSocketContext(this.session,this.handler))
                    .thenAcceptAsync(this.handler::onDisConnect);
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException("unsupported frame type: " + frame.getClass().getName());
        }
        CompletableFuture.completedFuture(new WebSocketContext(this.session,this.handler,((TextWebSocketFrame) frame).text()))
                .thenAcceptAsync(this.handler::onText,ctx.executor());
    }


    private boolean isWebSocketRequest(HttpRequest req){
//        return req != null
//                && (this.handler = this.blade.routeMatcher().getWebSocket(req.uri())) != null
//                && req.decoderResult().isSuccess()
//                && "websocket".equals(req.headers().get("Upgrade"));
        return false;
    }

    private void initHandlerWrapper(){
        if(this.handler != null && this.handler instanceof WebSocketHandlerWrapper){
            ((WebSocketHandlerWrapper) this.handler).setPath(this.uri);
        }
    }

}