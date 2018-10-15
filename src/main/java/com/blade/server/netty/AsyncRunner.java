package com.blade.server.netty;

import com.blade.exception.BladeException;
import com.blade.kit.BladeCache;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.http.Cookie;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.http.Session;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

import static com.blade.kit.BladeKit.log200;
import static com.blade.kit.BladeKit.log500;
import static com.blade.mvc.Const.ENV_KEY_SESSION_KEY;
import static com.blade.mvc.Const.REQUEST_COST_TIME;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author biezhi
 * @date 2018/10/15
 */
@Slf4j
public class AsyncRunner implements Runnable {

    private ChannelHandlerContext ctx;
    private Request               request;
    private Response              response;
    private WebContext            webContext;
    private Instant               started;
    private RouteMethodHandler    routeMethodHandler;

    public AsyncRunner(RouteMethodHandler routeMethodHandler, WebContext webContext) {
        this.routeMethodHandler = routeMethodHandler;
        this.ctx = webContext.getHandlerContext();
        this.request = webContext.getRequest();
        this.response = webContext.getResponse();
        this.webContext = webContext;
        if (WebContext.blade().allowCost()) {
            this.started = Instant.now();
        }
    }

    @Override
    public void run() {
        log.info("开始异步执行...");
        WebContext.set(webContext);

        String uri    = request.uri();
        String method = request.method();
        try {
            routeMethodHandler.handle(ctx, request, response);
            if (WebContext.blade().allowCost()) {
                String paddingMethod = BladeCache.getPaddingMethod(method);
                long   cost          = log200(log, started, paddingMethod, uri);
                request.attribute(REQUEST_COST_TIME, cost);
            }
        } catch (Exception e) {
            this.exceptionCaught(uri, method, e);
        } finally {
            routeMethodHandler.finishWrite(ctx, request, response);
            WebContext.remove();
        }
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

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!ExceptionHandler.isResetByPeer(cause)) {
            log.error(cause.getMessage(), cause);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(500));
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

}
