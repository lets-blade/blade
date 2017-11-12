package com.blade.server.netty;

import com.blade.Blade;
import com.blade.exception.NotFoundException;
import com.blade.kit.DateKit;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.handler.RequestInvoker;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.HttpResponse;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.RouteMatcher;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AsciiString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;
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

    private final RouteMatcher      routeMatcher;
    private final Set<String>       statics;
    private final StaticFileHandler staticFileHandler;
    private final RequestInvoker    requestInvoker;
    private final ExceptionHandler  exceptionHandler;
    public static SessionHandler SESSION_HANDLER = null;
    private final boolean hasMiddleware;
    private final boolean hasBeforeHook;
    private final boolean hasAfterHook;

    private volatile CharSequence date = new AsciiString(DateKit.gmtDate(LocalDateTime.now()));


    HttpServerHandler(Blade blade, ScheduledExecutorService service) {
        this.statics = blade.getStatics();

        service.scheduleWithFixedDelay(() -> date = new AsciiString(DateKit.gmtDate(LocalDateTime.now())), 1000, 1000, TimeUnit.MILLISECONDS);

        this.exceptionHandler = blade.exceptionHandler();

        this.routeMatcher = blade.routeMatcher();
        this.requestInvoker = new RequestInvoker(blade);
        this.staticFileHandler = new StaticFileHandler(blade);

        this.hasMiddleware = routeMatcher.getMiddleware().size() > 0;
        this.hasBeforeHook = routeMatcher.hasBeforeHook();
        this.hasAfterHook = routeMatcher.hasAfterHook();

        HttpServerHandler.SESSION_HANDLER = blade.sessionManager() != null ? new SessionHandler(blade) : null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        Request  request  = HttpRequest.build(ctx, fullHttpRequest);
        Response response = HttpResponse.build(ctx, date);
        boolean  isStatic = false;
        // route signature
        Signature signature = Signature.builder().request(request).response(response).build();
        try {

            // request uri
            String uri = request.uri();

            // write session
            WebContext.set(new WebContext(request, response));

            if (isStaticFile(uri)) {
                staticFileHandler.handle(ctx, request, response);
                isStatic = true;
                return;
            }

            Route route = routeMatcher.lookupRoute(request.method(), uri);
            if (null == route) {
                log.warn("Not Found\t{}", uri);
                throw new NotFoundException();
            }

            log.info("{}\t{}\t{}", request.protocol(), request.method(), uri);

            request.initPathParams(route);

            // get method parameters
            signature.setRoute(route);

            // middleware
            if (hasMiddleware && !requestInvoker.invokeMiddleware(routeMatcher.getMiddleware(), signature)) {
                this.sendFinish(response);
                return;
            }

            // web hook before
            if (hasBeforeHook && !requestInvoker.invokeHook(routeMatcher.getBefore(uri), signature)) {
                this.sendFinish(response);
                return;
            }

            // execute
            signature.setRoute(route);
            requestInvoker.routeHandle(signature);

            // webHook
            if (hasAfterHook) {
                requestInvoker.invokeHook(routeMatcher.getAfter(uri), signature);
            }
        } catch (Exception e) {
            if (null != exceptionHandler) {
                exceptionHandler.handle(e);
            } else {
                log.error("Blade Invoke Error", e);
            }
        } finally {
            if (!isStatic) this.sendFinish(response);
            WebContext.remove();
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

    private boolean isStaticFile(String uri) {
        Optional<String> result = statics.stream().filter(s -> s.equals(uri) || uri.startsWith(s)).findFirst();
        return result.isPresent();
    }

    private void sendFinish(Response response) {
        if (!response.isCommit()) {
            response.body(Unpooled.EMPTY_BUFFER);
        }
    }

}