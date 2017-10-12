package com.blade.server.netty;

import com.blade.Blade;
import com.blade.exception.NotFoundException;
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
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Set;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

/**
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Blade             blade;
    private final RouteMatcher      routeMatcher;
    private final Set<String>       statics;
    private final SessionHandler    sessionHandler;
    private final StaticFileHandler staticFileHandler;
    private final RequestInvoker    requestInvoker;
    private final ExceptionHandler  exceptionHandler;

    HttpServerHandler(Blade blade) {
        this.blade = blade;
        this.statics = blade.getStatics();
        this.exceptionHandler = blade.exceptionHandler();

        this.routeMatcher = blade.routeMatcher();
        this.requestInvoker = new RequestInvoker(blade);
        this.staticFileHandler = new StaticFileHandler(blade);
        this.sessionHandler = blade.sessionManager() != null ? new SessionHandler(blade) : null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        if (is100ContinueExpected(fullHttpRequest)) {
            ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        }

        Request  request  = HttpRequest.build(ctx, fullHttpRequest, sessionHandler);
        Response response = HttpResponse.build(ctx, blade.templateEngine());

        // route signature
        Signature signature = Signature.builder().request(request).response(response).build();

        try {

            // request uri
            String uri = request.uri();

            // write session
            WebContext.set(new WebContext(request, response));

            if (isStaticFile(uri)) {
                staticFileHandler.handle(ctx, request, response);
                return;
            } else {
                log.info("{}\t{}\t{}", request.protocol(), request.method(), uri);
            }

            Route route = routeMatcher.lookupRoute(request.method(), uri);
            if (null == route) {
                log.warn("Not Found\t{}", uri);
                throw new NotFoundException();
            }
            request.initPathParams(route);

            // get method parameters
            signature.setRoute(route);

            // middleware
            if (!requestInvoker.invokeMiddleware(routeMatcher.getMiddleware(), signature)) {
                this.sendFinish(response);
                return;
            }

            // web hook before
            if (!requestInvoker.invokeHook(routeMatcher.getBefore(uri), signature)) {
                this.sendFinish(response);
                return;
            }

            // execute
            signature.setRoute(route);
            requestInvoker.routeHandle(signature);

            // webHook
            requestInvoker.invokeHook(routeMatcher.getAfter(uri), signature);

        } catch (Exception e) {
            if (null != exceptionHandler) {
                exceptionHandler.handle(e);
            } else {
                log.error("Blade Invoke Error", e);
            }
        } finally {
            this.sendFinish(response);
            WebContext.remove();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (null != exceptionHandler) {
            exceptionHandler.handle((Exception) cause);
        } else {
            log.error("Blade Invoke Error", cause);
        }
        ctx.close();
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