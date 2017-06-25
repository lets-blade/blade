package com.blade.server.netty;

import com.blade.Blade;
import com.blade.exception.BladeException;
import com.blade.kit.BladeKit;
import com.blade.metric.Connection;
import com.blade.metric.WebStatistics;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.RouteViewResolve;
import com.blade.mvc.hook.Invoker;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.HttpResponse;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.RouteHandler;
import com.blade.mvc.route.RouteMatcher;
import com.blade.mvc.ui.DefaultUI;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.blade.mvc.Const.*;
import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

/**
 * @author biezhi
 *         2017/5/31
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Blade blade;
    private final RouteMatcher routeMatcher;
    private final RouteViewResolve routeViewResolve;
    private final Set<String> statics;

    private final StaticFileHandler staticFileHandler;
    private final SessionHandler sessionHandler;

    private final Connection ci;
    private final boolean openMonitor;

    private String page404, page500;

    public HttpServerHandler(Blade blade, Connection ci) {
        this.blade = blade;
        this.statics = blade.getStatics();

        this.ci = ci;
        this.openMonitor = blade.environment().getBoolean(ENV_KEY_MONITOR_ENABLE, false);
        this.page404 = blade.environment().get(ENV_KEY_PAGE_404, null);
        this.page500 = blade.environment().get(ENV_KEY_PAGE_500, null);

        this.routeMatcher = blade.routeMatcher();
        this.routeViewResolve = new RouteViewResolve(blade);
        this.staticFileHandler = new StaticFileHandler(blade);
        this.sessionHandler = blade.sessionManager() != null ? new SessionHandler(blade) : null;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        if (openMonitor) {
            WebStatistics.me().addChannel(ctx.channel());
        }
    }

    private FullHttpRequest fullHttpRequest;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {

        this.fullHttpRequest = fullHttpRequest;

        if (is100ContinueExpected(fullHttpRequest)) {
            ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        }

        Request request = HttpRequest.build(ctx, fullHttpRequest, sessionHandler);
        Response response = HttpResponse.build(ctx, blade.templateEngine());

        // reuqest uri
        String uri = request.uri();
        log.debug("{}\t{}\t{}", request.protocol(), request.method(), uri);

        // write session
        WebContext.set(new WebContext(request, response));

        // web hook
        if (!invokeHook(routeMatcher.getBefore(uri), request, response)) {
            this.sendFinish(response);
            return;
        }

        if (isStaticFile(uri)) {
            staticFileHandler.handle(ctx, request, response);
            return;
        }

        Route route = routeMatcher.lookupRoute(request.method(), uri);
        if (null == route) {
            // 404
            response.notFound();
            String html = String.format(DefaultUI.VIEW_404, uri);
            if (null != page404) {
                response.render(page404);
            } else {
                response.html(html);
            }
            return;
        }
        request.initPathParams(route);

        // middlewares八
        if (!invokeMiddlewares(routeMatcher.getMiddlewares(), request, response)) {
            this.sendFinish(response);
            return;
        }

        // execute
        this.routeHandle(request, response, route);

        invokeHook(routeMatcher.getAfter(uri), request, response);

        this.sendFinish(response);
        WebContext.remove();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        if (openMonitor) {
            WebStatistics.me().registerRequestFromIp(WebStatistics.getIpFromChannel(ctx.channel()), LocalDateTime.now());
            if (fullHttpRequest != null) {
                ci.addUri(fullHttpRequest.getUri());
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        fullHttpRequest = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("error", cause);

        if (!ctx.channel().isActive()) {
            ctx.close();
            return;
        }
        Response response = WebContext.response();
        response.status(500);

        if (cause instanceof BladeException) {
            String error = cause.getMessage();

            String contentType = null != response ? response.contentType() : CONTENT_TYPE_TEXT;

            StringWriter sw = new StringWriter();
            PrintWriter writer = new PrintWriter(sw);

            if (null != page500) {
                cause.printStackTrace(writer);
                WebContext.request().attribute("error", error);
                WebContext.request().attribute("stackTrace", sw.toString());
                response.render(page500);
            } else {
                writer.write(String.format(DefaultUI.ERROR_START, cause.getClass() + " : " + cause.getMessage()));
                writer.write("\r\n");
                cause.printStackTrace(writer);
                writer.println(DefaultUI.HTML_FOOTER);
                error = sw.toString();
                response.html(error);
            }
            return;
        }
        response.body("Internal Server Error");
    }

    private boolean isStaticFile(String uri) {
        Optional<String> result = statics.stream().filter(s -> s.equals(uri) || uri.startsWith(s)).findFirst();
        return result.isPresent();
    }

    /**
     * Actual routing method execution
     *
     * @param request  request object
     * @param response response object
     * @param route    route object
     */
    private boolean routeHandle(Request request, Response response, Route route) throws Exception {
        Object target = route.getTarget();
        if (null == target) {
            Class<?> clazz = route.getAction().getDeclaringClass();
            target = blade.getBean(clazz);
            route.setTarget(target);
        }
        if (route.getTargetType() == RouteHandler.class) {
            RouteHandler routeHandler = (RouteHandler) target;
            routeHandler.handle(request, response);
            return false;
        } else {
            return routeViewResolve.handle(request, response, route);
        }
    }

    private boolean invokeMiddlewares(List<Route> middlewares, Request request, Response response) {
        if (BladeKit.isEmpty(middlewares)) {
            return true;
        }
        Invoker invoker = new Invoker(request, response);
        for (Route middleware : middlewares) {
            WebHook webHook = (WebHook) middleware.getTarget();
            boolean flag = webHook.before(invoker);
            if (!flag) return false;
        }
        return true;
    }

    /**
     * invoke hooks
     *
     * @param hooks
     * @param request
     * @param response
     * @return
     */
    private boolean invokeHook(List<Route> hooks, Request request, Response response) throws BladeException {
        for (Route route : hooks) {
            if (route.getTargetType() == RouteHandler.class) {
                RouteHandler routeHandler = (RouteHandler) route.getTarget();
                routeHandler.handle(request, response);
            } else {
                boolean flag = routeViewResolve.invokeHook(request, response, route);
                if (!flag) return false;
            }
        }
        return true;
    }

    private void sendFinish(Response response) {
        if (response.isCommit()) {
            return;
        }
        response.body(Unpooled.EMPTY_BUFFER);
    }

}