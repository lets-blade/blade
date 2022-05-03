package com.hellokaton.blade.server;

import com.hellokaton.blade.annotation.Path;
import com.hellokaton.blade.exception.BladeException;
import com.hellokaton.blade.exception.InternalErrorException;
import com.hellokaton.blade.exception.NotFoundException;
import com.hellokaton.blade.kit.BladeCache;
import com.hellokaton.blade.kit.BladeKit;
import com.hellokaton.blade.kit.ReflectKit;
import com.hellokaton.blade.mvc.HttpConst;
import com.hellokaton.blade.mvc.RouteContext;
import com.hellokaton.blade.mvc.WebContext;
import com.hellokaton.blade.mvc.handler.RequestHandler;
import com.hellokaton.blade.mvc.handler.RouteHandler;
import com.hellokaton.blade.mvc.hook.WebHook;
import com.hellokaton.blade.mvc.http.Cookie;
import com.hellokaton.blade.mvc.http.*;
import com.hellokaton.blade.mvc.route.Route;
import com.hellokaton.blade.mvc.route.RouteMatcher;
import com.hellokaton.blade.mvc.ui.ModelAndView;
import com.blade.reflectasm.MethodAccess;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedStream;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.hellokaton.blade.server.NettyHttpConst.CONTENT_LENGTH;
import static com.hellokaton.blade.server.NettyHttpConst.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderNames.TRANSFER_ENCODING;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Http Server Handler
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
public class RouteMethodHandler implements RequestHandler {

    private final RouteMatcher routeMatcher = WebContext.blade().routeMatcher();
    private final boolean hasMiddleware = routeMatcher.getMiddleware().size() > 0;
    private final boolean hasBeforeHook = routeMatcher.hasBeforeHook();
    private final boolean hasAfterHook = routeMatcher.hasAfterHook();

    @Override
    public void handle(WebContext webContext) throws Exception {
        RouteContext context = new RouteContext(webContext.getRequest(), webContext.getResponse());

        // if execution returns false then execution is interrupted
        String uri = context.uri();
        Route route = webContext.getRoute();
        if (null == route) {
            throw new NotFoundException(context.uri());
        }

        // init route, request parameters, route action method and parameter.
        context.initRoute(route);

        // execution middleware
        if (hasMiddleware && !invokeMiddleware(routeMatcher.getMiddleware(), context)) {
            return;
        }
        context.injectParameters();

        // web hook before
        if (hasBeforeHook && !invokeHook(routeMatcher.getBefore(uri), context)) {
            return;
        }

        // execute
        this.routeHandle(context);

        // webHook
        if (hasAfterHook) {
            this.invokeHook(routeMatcher.getAfter(uri), context);
        }
    }

    public FullHttpResponse handleResponse(Request request, com.hellokaton.blade.mvc.http.Response response, ChannelHandlerContext context) {
        Session session = request.session();
        if (null != session) {
            Cookie cookie = new Cookie();
            cookie.name(WebContext.sessionKey());
            cookie.value(session.id());
            cookie.httpOnly(true);
            cookie.secure(request.isSecure());
            response.cookie(cookie);
        }

        FullHttpResponse fullHttpResponse = response.body().write(new BodyWriter() {
            @Override
            public FullHttpResponse onByteBuf(ByteBuf byteBuf) {
                return createResponseByByteBuf(response, byteBuf);
            }

            @Override
            public FullHttpResponse onView(ViewBody body) {
                try {
                    var sw = new StringWriter();
                    WebContext.blade().templateEngine().render(body.modelAndView(), sw);
                    Objects.requireNonNull(WebContext.response())
                            .contentType(HttpConst.CONTENT_TYPE_HTML);
                    return this.onByteBuf(Unpooled.copiedBuffer(sw.toString().getBytes(StandardCharsets.UTF_8)));
                } catch (Exception e) {
                    log.error("Render view error", e);
                }
                return null;
            }

            @Override
            public FullHttpResponse onRawBody(RawBody body) {
                return body.httpResponse();
            }

            @Override
            public FullHttpResponse onByteBuf(Object byteBuf) {
                var httpResponse = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(response.statusCode()));

                for (Map.Entry<String, String> next : response.headers().entrySet()) {
                    httpResponse.headers().set(next.getKey(), next.getValue());
                }

                // Write the initial line and the header.
                if (request.keepAlive()) {
                    httpResponse.headers().set(NettyHttpConst.CONNECTION, KEEP_ALIVE);
                }
                context.write(httpResponse, context.voidPromise());

                ChannelFuture lastContentFuture = context.writeAndFlush(byteBuf);
                if (!request.keepAlive()) {
                    lastContentFuture.addListener(ChannelFutureListener.CLOSE);
                }
                return null;
            }

        });
        if (request.keepAlive()) {
            fullHttpResponse.headers().set(NettyHttpConst.CONNECTION, KEEP_ALIVE);
        }
        return fullHttpResponse;
    }

    private void setDefaultHeaders(HttpHeaders headers) {
        headers.set(NettyHttpConst.DATE, HttpServerInitializer.date);
        headers.set(NettyHttpConst.X_POWER_BY, NettyHttpConst.HEADER_VERSION);
    }

    public Void handleStreamResponse(com.hellokaton.blade.mvc.http.Response response, InputStream body,
                                     ChannelHandlerContext context, boolean keepAlive) {

        var httpResponse = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(response.statusCode()));

        httpResponse.headers().set(TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        setDefaultHeaders(httpResponse.headers());

        for (Map.Entry<String, String> next : response.headers().entrySet()) {
            httpResponse.headers().set(next.getKey(), next.getValue());
        }

        context.write(response);

        context.write(new ChunkedStream(body));
        ChannelFuture lastContentFuture = context.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
        return null;
    }

    private FullHttpResponse createResponseByByteBuf(com.hellokaton.blade.mvc.http.Response response, ByteBuf byteBuf) {

        Map<String, String> headers = response.headers();

        var httpResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(response.statusCode()), byteBuf);

        httpResponse.headers().set(CONTENT_LENGTH, httpResponse.content().readableBytes());
        setDefaultHeaders(httpResponse.headers());

        if (response.cookiesRaw().size() > 0) {
            this.appendCookie(response, httpResponse);
        }

        for (Map.Entry<String, String> next : headers.entrySet()) {
            httpResponse.headers().set(NettyHttpConst.getAsciiString(next.getKey()), next.getValue());
        }
        return httpResponse;
    }

    private void appendCookie(com.hellokaton.blade.mvc.http.Response response, DefaultFullHttpResponse httpResponse) {
        for (io.netty.handler.codec.http.cookie.Cookie next : response.cookiesRaw()) {
            httpResponse.headers().add(NettyHttpConst.SET_COOKIE,
                    io.netty.handler.codec.http.cookie.ServerCookieEncoder.LAX.encode(next));
        }
    }

    /**
     * Actual routing method execution
     *
     * @param context route context
     */
    private void routeHandle(RouteContext context) {
        Object target = context.routeTarget();
        if (null == target) {
            Class<?> clazz = context.routeAction().getDeclaringClass();
            target = WebContext.blade().getBean(clazz);
        }
        if (context.targetType() == RouteHandler.class) {
            RouteHandler routeHandler = (RouteHandler) target;
            routeHandler.handle(context);
        } else {
            Method actionMethod = context.routeAction();
            Class<?> returnType = actionMethod.getReturnType();

            Path path = target.getClass().getAnnotation(Path.class);
            com.hellokaton.blade.annotation.response.Response response = actionMethod.getAnnotation(com.hellokaton.blade.annotation.response.Response.class);

            boolean responseJson = (null != response && response.contentType().toLowerCase().contains("json")) || (null != path && path.responseJson());
            if (responseJson) {
                if (!context.isIE()) {
                    context.contentType(HttpConst.CONTENT_TYPE_JSON);
                } else {
                    context.contentType(HttpConst.CONTENT_TYPE_HTML);
                }
            } else if (null != response) {
                context.contentType(response.contentType());
            }

            int len = actionMethod.getParameterTypes().length;

            MethodAccess methodAccess = BladeCache.getMethodAccess(target.getClass());

            Object returnParam = methodAccess.invoke(
                    target, actionMethod.getName(), len > 0 ?
                            context.routeParameters() : null);

            if (null == returnParam) {
                return;
            }

            if (responseJson) {
                context.json(returnParam);
                return;
            }
            if (returnType == String.class) {
                context.body(
                        ViewBody.of(new ModelAndView(returnParam.toString()))
                );
                return;
            }
            if (returnType == ModelAndView.class) {
                context.body(
                        ViewBody.of((ModelAndView) returnParam)
                );
            }
        }
    }

    /**
     * Invoke WebHook
     *
     * @param context   current execute route handler signature
     * @param hookRoute current webhook route handler
     * @return Return true then next handler, and else interrupt request
     * @throws Exception throw like parse param exception
     */
    private boolean invokeHook(RouteContext context, Route hookRoute) throws Exception {
        Method hookMethod = hookRoute.getAction();
        Object target = WebContext.blade().ioc().getBean(hookRoute.getTargetType());
        if (null == target) {
            Class<?> clazz = hookRoute.getAction().getDeclaringClass();
            target = WebContext.blade().ioc().getBean(clazz);
            hookRoute.setTarget(target);
        }

        // execute
        int len = hookMethod.getParameterTypes().length;
        hookMethod.setAccessible(true);

        Object returnParam;
        if (len > 0) {
            if (len == 1) {
                MethodAccess methodAccess = BladeCache.getMethodAccess(target.getClass());
                returnParam = methodAccess.invoke(target, hookMethod.getName(), context);
            } else if (len == 2) {
                MethodAccess methodAccess = BladeCache.getMethodAccess(target.getClass());

                returnParam = methodAccess.invoke(target, hookMethod.getName(),
                        context.request(), context.response());
            } else {
                throw new InternalErrorException("Bad web hook structure");
            }
        } else {
            returnParam = ReflectKit.invokeMethod(target, hookMethod);
        }

        if (null == returnParam) return true;

        Class<?> returnType = returnParam.getClass();
        if (returnType == Boolean.class || returnType == boolean.class) {
            return Boolean.valueOf(returnParam.toString());
        }
        return true;
    }

    private boolean invokeMiddleware(List<Route> middleware, RouteContext context) throws BladeException {
        if (BladeKit.isEmpty(middleware)) {
            return true;
        }
        for (Route route : middleware) {
            WebHook webHook = (WebHook) WebContext.blade().ioc().getBean(route.getTargetType());
            boolean flag = webHook.before(context);
            if (!flag) return false;
        }
        return true;
    }

    /**
     * invoke hooks
     *
     * @param hooks   webHook list
     * @param context http request
     * @return return invoke hook is abort
     */
    private boolean invokeHook(List<Route> hooks, RouteContext context) throws Exception {
        for (Route hook : hooks) {
            if (hook.getTargetType() == RouteHandler.class) {
                RouteHandler routeHandler = (RouteHandler) hook.getTarget();
                routeHandler.handle(context);
                if (context.isAbort()) {
                    return false;
                }
            } else {
                boolean flag = this.invokeHook(context, hook);
                if (!flag) return false;
            }
        }
        return true;
    }

}