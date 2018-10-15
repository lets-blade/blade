package com.blade.server.netty;

import com.blade.exception.BladeException;
import com.blade.exception.NotFoundException;
import com.blade.kit.BladeCache;
import com.blade.mvc.LocalContext;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.HttpResponse;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.RouteMatcher;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.concurrent.FastThreadLocal;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.blade.kit.BladeKit.log404;
import static com.blade.kit.BladeKit.log500;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Http Server Handler
 *
 * @author biezhi
 * 2018/10/15
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final FastThreadLocal<LocalContext> LOCAL_CONTEXT = new FastThreadLocal<>();

    private final StaticFileHandler  staticFileHandler  = new StaticFileHandler(WebContext.blade());
    private final RouteMethodHandler routeMethodHandler = new RouteMethodHandler();

    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        if (LOCAL_CONTEXT.get() != null && LOCAL_CONTEXT.get().hasDecoder()) {
            LOCAL_CONTEXT.get().decoder().cleanFiles();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        String  remoteAddress = ctx.channel().remoteAddress().toString();
        Request request       = HttpRequest.build(remoteAddress, msg);
        if (null == request) {
            return;
        }
        String uri    = request.uri();
        String method = request.method();

        if (request.isPart()) {
            this.executePart(ctx, request, uri, method);
            return;
        }

        // content has not been read yet
        if (!request.chunkIsEnd() && !request.readChunk()) {
            return;
        }

        try {
            executor.submit(new AsyncRunner(routeMethodHandler, WebContext.get()));
        } finally {
            WebContext.remove();
            LOCAL_CONTEXT.remove();
        }
    }

    public static LocalContext getLocalContext() {
        return LOCAL_CONTEXT.get();
    }

    public static void setLocalContext(LocalContext localContext) {
        LOCAL_CONTEXT.remove();
        LOCAL_CONTEXT.set(localContext);
    }

    private void executePart(ChannelHandlerContext ctx, Request request, String uri, String method) {
        Response response = new HttpResponse();
        WebContext.set(new WebContext(request, response, ctx));
        WebContext.get().setLocalContext(LOCAL_CONTEXT.get());
        try {
            if (isStaticFile(method, uri)) {
                staticFileHandler.handle(ctx, request, response);
                LOCAL_CONTEXT.remove();
                WebContext.remove();
            } else {
                RouteMatcher routeMatcher = WebContext.blade().routeMatcher();
                Route        route        = routeMatcher.lookupRoute(method, uri);
                if (null == route) {
                    String paddingMethod = BladeCache.getPaddingMethod(method);
                    log404(log, paddingMethod, uri);
                    throw new NotFoundException(uri);
                }
            }
        } catch (Exception e) {
            this.exceptionCaught(uri, method, e);
            routeMethodHandler.finishWrite(ctx, request, response);
            LOCAL_CONTEXT.remove();
            WebContext.remove();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("IO线程处理完毕：" + Thread.currentThread().getThreadGroup() + ":" + Thread.currentThread().getName());
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!ExceptionHandler.isResetByPeer(cause)) {
            log.error(cause.getMessage(), cause);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(500));
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private boolean isStaticFile(String method, String uri) {
        if ("POST".equals(method)) {
            return false;
        }
        Optional<String> result = WebContext.blade().getStatics().stream().filter(s -> s.equals(uri) || uri.startsWith(s)).findFirst();
        return result.isPresent();
    }

    private void exceptionCaught(String uri, String method, Exception e) {
        if (e instanceof BladeException) {
        } else {
            log500(log, method, uri);
        }
        if (null != WebContext.blade().exceptionHandler()) {
            WebContext.blade().exceptionHandler().handle(e);
        } else {
            log.error("Request Exception", e);
        }
    }

}