package com.blade.server.netty;

import com.blade.Blade;
import com.blade.kit.DateKit;
import com.blade.kit.NamedThreadFactory;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.handler.RequestExecution;
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

import static com.blade.server.netty.HttpConst.BUSINESS_THREAD_POOL;

/**
 * Http Server Handler
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public final RouteMatcher      routeMatcher;
    public final Set<String>       statics;
    public final StaticFileHandler staticFileHandler;
    public final ExceptionHandler  exceptionHandler;
    public static SessionHandler SESSION_HANDLER = null;
    public final boolean hasMiddleware;
    public final boolean hasBeforeHook;
    public final boolean hasAfterHook;

    public static volatile CharSequence date = new AsciiString(DateKit.gmtDate(LocalDateTime.now()));

    static {
        BUSINESS_THREAD_POOL.setThreadFactory(new NamedThreadFactory("task@"));
    }

    HttpServerHandler(Blade blade, ScheduledExecutorService service) {
        this.statics = blade.getStatics();

        service.scheduleWithFixedDelay(() -> date = new AsciiString(DateKit.gmtDate(LocalDateTime.now())), 1000, 1000, TimeUnit.MILLISECONDS);

        this.exceptionHandler = blade.exceptionHandler();

        this.routeMatcher = blade.routeMatcher();
        this.staticFileHandler = new StaticFileHandler(blade);

        this.hasMiddleware = routeMatcher.getMiddleware().size() > 0;
        this.hasBeforeHook = routeMatcher.hasBeforeHook();
        this.hasAfterHook = routeMatcher.hasAfterHook();

        HttpServerHandler.SESSION_HANDLER = blade.sessionManager() != null ? new SessionHandler(blade) : null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        if (log.isDebugEnabled()) {
            log.debug("Received IO request {}", ctx);
        }
        BUSINESS_THREAD_POOL.execute(new RequestExecution(ctx, fullHttpRequest.copy(), this));
        if (log.isDebugEnabled()) {
            log.debug("IO request processing ends {}", ctx);
        }
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