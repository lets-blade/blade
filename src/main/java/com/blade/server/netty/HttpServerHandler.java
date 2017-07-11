package com.blade.server.netty;

import com.blade.Blade;
import com.blade.exception.BladeException;
import com.blade.exception.ExceptionResolve;
import com.blade.kit.BladeKit;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.RouteViewResolve;
import com.blade.mvc.hook.Signature;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.blade.mvc.Const.ENV_KEY_PAGE_404;
import static com.blade.mvc.Const.ENV_KEY_PAGE_500;
import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

/**
 * @author biezhi
 *         2017/5/31
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public ExceptionResolve exceptionResolve;

    private final Blade blade;
    private final RouteMatcher routeMatcher;
    private final RouteViewResolve routeViewResolve;
    private final Set<String> statics;

    private final StaticFileHandler staticFileHandler;
    private final SessionHandler sessionHandler;

    private String page404, page500;

    HttpServerHandler(Blade blade, ExceptionResolve exceptionResolve) {
        this.blade = blade;
        this.statics = blade.getStatics();
        this.exceptionResolve = exceptionResolve;

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
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        if (is100ContinueExpected(fullHttpRequest)) {
            ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        }

        Request request = HttpRequest.build(ctx, fullHttpRequest, sessionHandler);
        Response response = HttpResponse.build(ctx, blade.templateEngine());

        // route signature
        Signature signature = Signature.builder().request(request).response(response).build();

        try {
            // request uri
            String uri = request.uri();
            log.debug("{}\t{}\t{}", request.protocol(), request.method(), uri);

            // write session
            WebContext.set(new WebContext(request, response));

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
            if (null != exceptionResolve && !exceptionResolve.handle(e, signature)) {
            } else {
                throw e;
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
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("error", cause);
        if (!ctx.channel().isActive()) {
            ctx.close();
            return;
        }
        Response response = WebContext.response();
        if (null != response) {
            response.status(500);
        }

        if (cause instanceof BladeException) {
            String error = cause.getMessage();

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
     * @param signature signature
     */
    private boolean routeHandle(Signature signature) throws Exception {
        Object target = signature.getRoute().getTarget();
        if (null == target) {
            Class<?> clazz = signature.getAction().getDeclaringClass();
            target = blade.getBean(clazz);
            signature.getRoute().setTarget(target);
        }
        if (signature.getRoute().getTargetType() == RouteHandler.class) {
            RouteHandler routeHandler = (RouteHandler) target;
            routeHandler.handle(signature.request(), signature.response());
            return false;
        } else {
            return routeViewResolve.handle(signature);
        }
    }

    private boolean invokeMiddleware(List<Route> middleware, Signature signature) throws BladeException {
        if (BladeKit.isEmpty(middleware)) {
            return true;
        }
        for (Route route : middleware) {
            WebHook webHook = (WebHook) route.getTarget();
            boolean flag = webHook.before(signature);
            if (!flag) return false;
        }
        return true;
    }

    /**
     * invoke hooks
     *
     * @param hooks     webHook list
     * @param signature http request
     * @return
     * @throws BladeException
     */
    private boolean invokeHook(List<Route> hooks, Signature signature) throws BladeException {
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