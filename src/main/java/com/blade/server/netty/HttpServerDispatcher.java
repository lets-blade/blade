package com.blade.server.netty;

import com.blade.exception.BladeException;
import com.blade.kit.BladeCache;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.http.*;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static com.blade.kit.BladeKit.log200;
import static com.blade.kit.BladeKit.log500;
import static com.blade.mvc.Const.ENV_KEY_SESSION_KEY;
import static com.blade.mvc.Const.REQUEST_COST_TIME;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Http Server Handler
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerDispatcher extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Set<String>        statics            = WebContext.blade().getStatics();
    private final ExceptionHandler   exceptionHandler   = WebContext.blade().exceptionHandler();
    private final StaticFileHandler  staticFileHandler  = new StaticFileHandler(WebContext.blade());
    private final RouteMethodHandler routeMethodHandler = new RouteMethodHandler();
    private final String             sessionKey         = WebContext.blade().environment().get(ENV_KEY_SESSION_KEY, HttpConst.DEFAULT_SESSION_KEY);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        String remoteAddress = ctx.channel().remoteAddress().toString();

        boolean isStatic = false;
        Instant start    = Instant.now();

        Request  request  = HttpRequest.build(req, remoteAddress);
        Response response = new HttpResponse();

        // request uri
        String uri = request.uri();

        String method = BladeCache.getPaddingMethod(request.method());

        // set context request and response
        WebContext.set(new WebContext(request, response));

        try {
            if (isStaticFile(uri)) {
                staticFileHandler.handle(ctx, request, response);
                isStatic = true;
            } else {
                routeMethodHandler.handle(ctx, request, response);
            }
            long cost = log200(log, start, method, uri);
            request.attribute(REQUEST_COST_TIME, cost);
        } catch (Exception e) {
            this.exceptionCaught(uri, method, e);
        } finally {
            if (!isStatic) {
                boolean keepAlive = request.keepAlive();
                Session session   = request.session();
                if (null != session) {
                    Cookie cookie = new Cookie();
                    cookie.name(sessionKey);
                    cookie.value(session.id());
                    cookie.httpOnly(true);
                    cookie.secure(request.isSecure());
                    response.cookie(cookie);
                }
                routeMethodHandler.handleResponse(response, ctx, keepAlive);
            }
            WebContext.remove();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
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

    private boolean isStaticFile(String uri) {
        Optional<String> result = statics.stream().filter(s -> s.equals(uri) || uri.startsWith(s)).findFirst();
        return result.isPresent();
    }

    private void exceptionCaught(String uri, String method, Exception e) {
        if (e instanceof BladeException) {
        } else {
            log500(log, method, uri);
        }
        if (null != exceptionHandler) {
            exceptionHandler.handle(e);
        } else {
            log.error("Request Exception", e);
        }
    }

}