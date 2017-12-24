package com.blade.server.netty;

import com.blade.Blade;
import com.blade.kit.DateKit;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.handler.RequestInvoker;
import com.blade.mvc.route.RouteMatcher;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AsciiString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Http Server Handler
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    static RouteMatcher      routeMatcher;
    static Set<String>       statics;
    static StaticFileHandler staticFileHandler;
    static RequestInvoker    requestInvoker;
    static ExceptionHandler  exceptionHandler;
    static SessionHandler SESSION_HANDLER = null;

    static boolean hasMiddleware;
    static boolean hasBeforeHook;
    static boolean hasAfterHook;

    static volatile CharSequence date = new AsciiString(DateKit.gmtDate(LocalDateTime.now()));

    HttpServerHandler(Blade blade, ScheduledExecutorService service) {
        HttpServerHandler.statics = blade.getStatics();

        service.scheduleWithFixedDelay(() -> date = new AsciiString(DateKit.gmtDate(LocalDateTime.now())), 1000, 1000, TimeUnit.MILLISECONDS);

        HttpServerHandler.exceptionHandler = blade.exceptionHandler();

        HttpServerHandler.routeMatcher = blade.routeMatcher();
        HttpServerHandler.requestInvoker = new RequestInvoker(blade);
        HttpServerHandler.staticFileHandler = new StaticFileHandler(blade);

        HttpServerHandler.hasMiddleware = routeMatcher.getMiddleware().size() > 0;
        HttpServerHandler.hasBeforeHook = routeMatcher.hasBeforeHook();
        HttpServerHandler.hasAfterHook = routeMatcher.hasAfterHook();
        HttpServerHandler.SESSION_HANDLER = blade.sessionManager() != null ? new SessionHandler(blade) : null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        new RequestExecution(ctx, fullHttpRequest.copy()).run();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        if (ctx.channel().isOpen() && ctx.channel().isActive() && ctx.channel().isWritable()) {
            ctx.flush();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (null != exceptionHandler) {
            exceptionHandler.handle((Exception) cause);
        } else {
            log.error("Blade Invoke Error", cause);
        }
        if (ctx.channel().isOpen() && ctx.channel().isActive() && ctx.channel().isWritable()) {
            ctx.close();
        }
    }

}