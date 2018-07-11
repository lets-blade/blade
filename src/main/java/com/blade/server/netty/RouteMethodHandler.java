package com.blade.server.netty;

import com.blade.exception.BladeException;
import com.blade.exception.InternalErrorException;
import com.blade.exception.NotFoundException;
import com.blade.kit.BladeCache;
import com.blade.kit.BladeKit;
import com.blade.kit.ReflectKit;
import com.blade.mvc.Const;
import com.blade.mvc.RouteContext;
import com.blade.mvc.WebContext;
import com.blade.mvc.annotation.JSON;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.handler.RequestHandler;
import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.handler.RouteHandler0;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.*;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.RouteMatcher;
import com.blade.mvc.ui.ModelAndView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.blade.kit.BladeKit.log404;
import static com.blade.server.netty.HttpConst.CONTENT_LENGTH;
import static com.blade.server.netty.HttpConst.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderNames.TRANSFER_ENCODING;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Http Server Handler
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
public class RouteMethodHandler implements RequestHandler<ChannelHandlerContext> {

    private final RouteMatcher routeMatcher  = WebContext.blade().routeMatcher();
    private final boolean      hasMiddleware = routeMatcher.getMiddleware().size() > 0;
    private final boolean      hasBeforeHook = routeMatcher.hasBeforeHook();
    private final boolean      hasAfterHook  = routeMatcher.hasAfterHook();

    public void handleResponse(Request request, Response response, ChannelHandlerContext context) {
        response.body().write(new BodyWriter<Void>() {
            @Override
            public Void onText(StringBody body) {
                return handleFullResponse(
                        createTextResponse(request, response, body.content()),
                        context,
                        request.keepAlive());
            }

            @Override
            public Void onStream(StreamBody body) {
                return handleStreamResponse(response, body.content(), context, request.keepAlive());
            }

            @Override
            public Void onView(ViewBody body) {
                try {
                    var sw = new StringWriter();
                    WebContext.blade().templateEngine().render(body.modelAndView(), sw);
                    response.contentType(Const.CONTENT_TYPE_HTML);

                    return handleFullResponse(
                            createTextResponse(request, response, sw.toString()),
                            context,
                            request.keepAlive());
                } catch (Exception e) {
                    log.error("Render view error", e);
                }
                return null;
            }

            @Override
            public Void onEmpty(EmptyBody emptyBody) {
                return handleFullResponse(
                        createTextResponse(request, response, ""),
                        context,
                        request.keepAlive());
            }

            @Override
            public Void onRawBody(RawBody body) {
                return handleFullResponse(body.httpResponse(), context, request.keepAlive());
            }
        });
    }

    public Void handleFullResponse(FullHttpResponse response, ChannelHandlerContext context, boolean keepAlive) {
        if (context.channel().isActive()) {
            if (!keepAlive) {
                context.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(HttpConst.CONNECTION, KEEP_ALIVE);
                context.write(response, context.voidPromise());
            }
            context.flush();
        }
        return null;
    }

    public Map<String, String> getDefaultHeader() {
        var map = new HashMap<String, String>();
        map.put(HttpConst.DATE.toString(), HttpServerInitializer.date.toString());
        map.put(HttpConst.X_POWER_BY.toString(), HttpConst.VERSION.toString());
        return map;
    }

    public Void handleStreamResponse(Response response, InputStream body,
                                     ChannelHandlerContext context, boolean keepAlive) {

        var httpResponse = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(response.statusCode()));

        httpResponse.headers().set(TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);

        response.headers().forEach((key, value) -> httpResponse.headers().set(key, value));
        context.write(response);

        context.write(new ChunkedStream(body));
        ChannelFuture lastContentFuture = context.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
        return null;
    }

    public FullHttpResponse createTextResponse(Request request, Response response, String body) {
        Map<String, String> headers = response.headers();
        headers.putAll(getDefaultHeader());

        if (response.cookiesRaw().size() > 0) {
            response.cookiesRaw().forEach(cookie -> headers.put(HttpConst.SET_COOKIE.toString(), io.netty.handler.codec.http.cookie.ServerCookieEncoder.LAX.encode(cookie)));
        }

        ByteBuf bodyBuf = Unpooled.wrappedBuffer(body.getBytes(StandardCharsets.UTF_8));

        var httpResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(response.statusCode()),
                null == bodyBuf ? Unpooled.buffer(0) : bodyBuf);

        httpResponse.headers().set(CONTENT_LENGTH, httpResponse.content().readableBytes());

        headers.forEach((key, value) -> httpResponse.headers().set(key, value));
        return httpResponse;
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
            context.route().setTarget(target);
        }
        if (context.targetType() == RouteHandler.class) {
            RouteHandler routeHandler = (RouteHandler) target;
            routeHandler.handle(context);
        } else if (context.targetType() == RouteHandler0.class) {
            RouteHandler0 routeHandler = (RouteHandler0) target;
            routeHandler.handle(context.request(), context.response());
        } else {
            Method   actionMethod = context.routeAction();
            Class<?> returnType   = actionMethod.getReturnType();

            Path path = target.getClass().getAnnotation(Path.class);
            JSON JSON = actionMethod.getAnnotation(JSON.class);

            boolean isRestful = (null != JSON) || (null != path && path.restful());

            // if request is restful and not InternetExplorer userAgent
            if (isRestful) {
                if (!context.isIE()) {
                    context.contentType(Const.CONTENT_TYPE_JSON);
                } else {
                    context.contentType(Const.CONTENT_TYPE_HTML);
                }
            }

            int len = actionMethod.getParameterTypes().length;

            MethodAccess methodAccess = BladeCache.getMethodAccess(target.getClass());
            Object       returnParam  = methodAccess.invoke(target, actionMethod.getName(), len > 0 ? context.routeParameters() : null);
            if (null == returnParam) {
                return;
            }

            if (isRestful) {
                context.json(returnParam);
                return;
            }
            if (returnType == String.class) {
                context.body(new ViewBody(new ModelAndView(returnParam.toString())));
                return;
            }
            if (returnType == ModelAndView.class) {
                context.body(new ViewBody((ModelAndView) returnParam));
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
        Object target     = hookRoute.getTarget();
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
                returnParam = methodAccess.invoke(target, hookMethod.getName(), context.request(), context.response());
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
        for (Route route: middleware) {
            WebHook webHook = (WebHook) route.getTarget();
            boolean flag    = webHook.before(context);
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
        for (Route hook: hooks) {
            if (hook.getTargetType() == RouteHandler.class) {
                RouteHandler routeHandler = (RouteHandler) hook.getTarget();
                routeHandler.handle(context);
            } else if (hook.getTargetType() == RouteHandler0.class) {
                RouteHandler0 routeHandler = (RouteHandler0) hook.getTarget();
                routeHandler.handle(context.request(), context.response());
            } else {
                boolean flag = this.invokeHook(context, hook);
                if (!flag) return false;
            }
        }
        return true;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Request request, Response response) throws Exception {
        RouteContext context = new RouteContext(request, response);
        // if execution returns false then execution is interrupted
        String uri = context.uri();

        Route route = routeMatcher.lookupRoute(context.method(), uri);
        if (null == route) {
            log404(log, context.method(), context.uri());
            throw new NotFoundException(context.uri());
        }

        // init route, request parameters, route action method and parameter.
        context.initRoute(route);

        // execution middleware
        if (hasMiddleware && !invokeMiddleware(routeMatcher.getMiddleware(), context)) {
            handleResponse(request, context.response(), ctx);
            return;
        }
        context.injectParameters();

        // web hook before
        if (hasBeforeHook && !invokeHook(routeMatcher.getBefore(uri), context)) {
            handleResponse(request, context.response(), ctx);
            return;
        }

        // execute
        this.routeHandle(context);

        // webHook
        if (hasAfterHook) {
            this.invokeHook(routeMatcher.getAfter(uri), context);
        }
    }

}