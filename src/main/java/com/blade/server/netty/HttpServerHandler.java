package com.blade.server.netty;

import com.blade.exception.BladeException;
import com.blade.exception.InternalErrorException;
import com.blade.exception.NotFoundException;
import com.blade.kit.BladeCache;
import com.blade.kit.BladeKit;
import com.blade.kit.PathKit;
import com.blade.kit.ReflectKit;
import com.blade.mvc.Const;
import com.blade.mvc.RouteContext;
import com.blade.mvc.WebContext;
import com.blade.mvc.annotation.JSON;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.handler.RouteHandler0;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.*;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.HttpResponse;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.RouteMatcher;
import com.blade.mvc.ui.ModelAndView;
import com.blade.reflectasm.MethodAccess;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.stream.ChunkedStream;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

import static com.blade.kit.BladeKit.*;
import static com.blade.mvc.Const.REQUEST_COST_TIME;
import static com.blade.server.netty.HttpConst.CONTENT_LENGTH;
import static com.blade.server.netty.HttpConst.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderNames.TRANSFER_ENCODING;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Http Server Handler
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Set<String>       statics           = WebContext.blade().getStatics();
    private final RouteMatcher      routeMatcher      = WebContext.blade().routeMatcher();
    private final ExceptionHandler  exceptionHandler  = WebContext.blade().exceptionHandler();
    private final boolean           hasMiddleware     = routeMatcher.getMiddleware().size() > 0;
    private final boolean           hasBeforeHook     = routeMatcher.hasBeforeHook();
    private final boolean           hasAfterHook      = routeMatcher.hasAfterHook();
    private final StaticFileHandler staticFileHandler = new StaticFileHandler(WebContext.blade());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        String remoteAddress = ctx.channel().remoteAddress().toString();

        boolean isStatic  = false;
        boolean keepAlive = isKeepAlive(req);

        Instant start = Instant.now();

        Request  request  = HttpRequest.build(req, remoteAddress);
        Response response = new HttpResponse();

        // request uri
        String uri      = request.uri();
        String cleanUri = uri;
        if (!"/".equals(request.contextPath())) {
            cleanUri = PathKit.cleanPath(uri.replaceFirst(request.contextPath(), "/"));
        }

        String method = BladeCache.getPaddingMethod(request.method());

        // set context request and response
        WebContext.set(new WebContext(request, response));

        try {
            if (isStaticFile(cleanUri)) {
                staticFileHandler.handle(ctx, request, response);
                isStatic = true;
                return;
            }

            RouteContext context = new RouteContext(request, response);
            // if execution returns false then execution is interrupted
            if (!execution(ctx, keepAlive, context, cleanUri)) {
                return;
            }

            long cost = log200(log, start, method, uri);
            request.attribute(REQUEST_COST_TIME, cost);
        } catch (Exception e) {
            this.exceptionCaught(uri, method, e);
        } finally {
            if (!isStatic) {
                this.handleResponse(response, ctx, keepAlive);
            }
            WebContext.remove();
        }
    }

    private boolean execution(ChannelHandlerContext ctx, boolean keepAlive, RouteContext context, String cleanUri) throws Exception {

        Route route = routeMatcher.lookupRoute(context.method(), cleanUri);
        if (null == route) {
            log404(log, context.method(), context.uri());
            throw new NotFoundException(context.uri());
        }

        // init route, request parameters, route action method and parameter.
        context.initRoute(route);

        // execution middleware
        if (hasMiddleware && !invokeMiddleware(routeMatcher.getMiddleware(), context)) {
            context.body(EmptyBody.empty());
            handleResponse(context.response(), ctx, keepAlive);
            return false;
        }

        // web hook before
        if (hasBeforeHook && !invokeHook(routeMatcher.getBefore(cleanUri), context)) {
            context.body(EmptyBody.empty());
            handleResponse(context.response(), ctx, keepAlive);
            return false;
        }

        // execute
        this.routeHandle(context);

        // webHook
        if (hasAfterHook) {
            this.invokeHook(routeMatcher.getAfter(cleanUri), context);
        }
        return true;
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

    private void handleResponse(Response response, ChannelHandlerContext context, boolean keepAlive) {
        response.body().write(new BodyWriter<Void>() {
            @Override
            public Void onText(StringBody body) {
                handleFullResponse(
                        createFullResponse(response.statusCode(), keepAlive, response.headers(), response.cookiesRaw(), body.content()),
                        context, keepAlive);
                return null;
            }

            @Override
            public Void onStream(StreamBody body) {
                handleStreamResponse(response.statusCode(), response.headers(), body.content(), context, keepAlive);
                return null;
            }

            @Override
            public Void onView(ViewBody body) {
                StringWriter sw = new StringWriter();
                try {
                    WebContext.blade().templateEngine().render(body.modelAndView(), sw);
                    response.contentType(Const.CONTENT_TYPE_HTML);

                    handleFullResponse(
                            createFullResponse(response.statusCode(), keepAlive, response.headers(), response.cookiesRaw(), sw.toString()),
                            context, keepAlive);
                } catch (Exception e) {
                    log.error("Render view error", e);
                }
                return null;
            }

            @Override
            public Void onEmpty(EmptyBody emptyBody) {
                handleFullResponse(
                        createFullResponse(response.statusCode(), keepAlive, response.headers(), response.cookiesRaw(), ""),
                        context, keepAlive);
                return null;
            }

            @Override
            public Void onRawBody(RawBody body) {
                handleFullResponse(body.httpResponse(), context, keepAlive);
                return null;
            }
        });
    }

    private void handleFullResponse(FullHttpResponse response, ChannelHandlerContext context, boolean keepAlive) {
        if (context.channel().isActive()) {
            if (!keepAlive) {
                context.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(HttpConst.CONNECTION, KEEP_ALIVE);
                context.write(response, context.voidPromise());
            }
            context.flush();
        }
    }

    private Map<String, String> getDefaultHeader(boolean keepAlive) {
        Map<String, String> map = new HashMap<>();
        map.put(HttpConst.DATE.toString(), HttpServerInitializer.date.toString());
        map.put(HttpConst.X_POWER_BY.toString(), HttpConst.VERSION.toString());
        if (keepAlive) {
            map.put(HttpConst.CONNECTION.toString(), "keep-alive");
        }
        return map;
    }

    private void handleStreamResponse(int status, Map<String, String> headers, InputStream body,
                                      ChannelHandlerContext context, boolean keepAlive) {
        DefaultHttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(status));
        response.headers().set(TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        headers.forEach((key, value) -> response.headers().set(key, value));
        context.write(response);

        context.write(new ChunkedStream(body));
        ChannelFuture lastContentFuture = context.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private FullHttpResponse createFullResponse(int status, boolean keepAlive, Map<String, String> headers, Set<Cookie> cookies, String body) {
        headers.putAll(getDefaultHeader(keepAlive));

        if (cookies.size() > 0) {
            cookies.forEach(cookie -> headers.put(HttpConst.SET_COOKIE.toString(), io.netty.handler.codec.http.cookie.ServerCookieEncoder.LAX.encode(cookie)));
        }

        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, HttpResponseStatus.valueOf(status),
                body.isEmpty() ? Unpooled.buffer(0) : Unpooled.wrappedBuffer(body.getBytes(StandardCharsets.UTF_8)));

        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        headers.forEach((key, value) -> response.headers().set(key, value));
        return response;
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
        for (Route route : middleware) {
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
        for (Route hook : hooks) {
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