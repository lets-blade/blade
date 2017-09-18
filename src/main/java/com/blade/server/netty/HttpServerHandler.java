package com.blade.server.netty;

import com.blade.Blade;
import com.blade.exception.BladeException;
import com.blade.exception.NotFoundException;
import com.blade.kit.BladeKit;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.handler.RouteViewResolve;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
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

import java.util.List;
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
    private final RouteViewResolve  routeViewResolve;
    private final ExceptionHandler  exceptionHandler;

    HttpServerHandler(Blade blade) {
        this.blade = blade;
        this.statics = blade.getStatics();
        this.exceptionHandler = blade.exceptionHandler();

        this.routeMatcher = blade.routeMatcher();
        this.routeViewResolve = new RouteViewResolve(blade);
        this.staticFileHandler = new StaticFileHandler(blade);
        this.sessionHandler = blade.sessionManager() != null ? new SessionHandler(blade) : null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
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
            log.info("{}\t{}\t{}", request.protocol(), request.method(), uri);

            // write session
            WebContext.set(new WebContext(request, response));

            if (isStaticFile(uri)) {
                staticFileHandler.handle(ctx, request, response);
                return;
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
            if (!invokeMiddleware(routeMatcher.getMiddleware(), signature)) {
                this.sendFinish(response);
                return;
            }

            // web hook before
            if (!invokeHook(routeMatcher.getBefore(uri), signature)) {
                this.sendFinish(response);
                return;
            }

            // execute
            signature.setRoute(route);
            this.routeHandle(signature);

            // webHook
            this.invokeHook(routeMatcher.getAfter(uri), signature);

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

    /**
     * Actual routing method execution
     *
     * @param signature signature
     */
    private void routeHandle(Signature signature) throws Exception {
        Object target = signature.getRoute().getTarget();
        if (null == target) {
            Class<?> clazz = signature.getAction().getDeclaringClass();
            target = blade.getBean(clazz);
            signature.getRoute().setTarget(target);
        }
        if (signature.getRoute().getTargetType() == RouteHandler.class) {
            RouteHandler routeHandler = (RouteHandler) target;
            routeHandler.handle(signature.request(), signature.response());
        } else {
            routeViewResolve.handle(signature);
        }
    }

    private boolean invokeMiddleware(List<Route> middleware, Signature signature) throws BladeException {
        if (BladeKit.isEmpty(middleware)) {
            return true;
        }
        for (Route route : middleware) {
            WebHook webHook = (WebHook) route.getTarget();
            boolean flag    = webHook.before(signature);
            if (!flag) return false;
        }
        return true;
    }

    /**
     * invoke hooks
     *
     * @param hooks     webHook list
     * @param signature http request
     * @return return invoke hook is abort
     */
    private boolean invokeHook(List<Route> hooks, Signature signature) throws Exception {
        for (Route route : hooks) {
            if (route.getTargetType() == RouteHandler.class) {
                RouteHandler routeHandler = (RouteHandler) route.getTarget();
                routeHandler.handle(signature.request(), signature.response());
            } else {
                boolean flag = routeViewResolve.invokeHook(signature, route);
                if (!flag) return false;
            }
        }
        return true;
    }

    private void sendFinish(Response response) {
        if (!response.isCommit()) {
            response.body(Unpooled.EMPTY_BUFFER);
        }
    }

}