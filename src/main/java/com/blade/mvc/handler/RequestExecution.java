package com.blade.mvc.handler;

import com.blade.exception.BladeException;
import com.blade.exception.InternalErrorException;
import com.blade.exception.NotFoundException;
import com.blade.kit.*;
import com.blade.mvc.Const;
import com.blade.mvc.WebContext;
import com.blade.mvc.annotation.JSON;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.HttpResponse;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.RouteMatcher;
import com.blade.mvc.ui.ModelAndView;
import com.blade.reflectasm.MethodAccess;
import com.blade.server.netty.HttpConst;
import com.blade.server.netty.HttpServerInitializer;
import com.blade.server.netty.StaticFileHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.lang.invoke.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static com.blade.kit.BladeKit.*;
import static com.blade.mvc.Const.REQUEST_COST_TIME;

/**
 * Http Request Execution Handler
 *
 * @author biezhi
 * @date 2017/12/24
 */
@Slf4j
public class RequestExecution implements Runnable {

    private final ChannelHandlerContext ctx;
    private final FullHttpRequest       fullHttpRequest;
    private final ExceptionHandler      exceptionHandler = WebContext.blade().exceptionHandler();

    private final static Set<String>       STATICS             = WebContext.blade().getStatics();
    private final static RouteMatcher      ROUTE_MATCHER       = WebContext.blade().routeMatcher();
    private final static boolean           hasMiddleware       = ROUTE_MATCHER.getMiddleware().size() > 0;
    private final static boolean           hasBeforeHook       = ROUTE_MATCHER.hasBeforeHook();
    private final static boolean           hasAfterHook        = ROUTE_MATCHER.hasAfterHook();
    private final static StaticFileHandler STATIC_FILE_HANDLER = new StaticFileHandler(WebContext.blade());

    public RequestExecution(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        this.ctx = ctx;
        this.fullHttpRequest = fullHttpRequest;
    }

    @Override
    public void run() {

        Request  request  = HttpRequest.build(ctx, fullHttpRequest);
        Response response = HttpResponse.build(ctx, HttpServerInitializer.date);
        boolean  isStatic = false;
        // route signature
        Signature signature = Signature.builder().request(request).response(response).build();
        // request uri
        String uri      = request.uri();
        String cleanUri = uri;
        if (!"/".equals(request.contextPath())) {
            cleanUri = PathKit.cleanPath(uri.replaceFirst(request.contextPath(), "/"));
        }

        String method = BladeCache.getPaddingMethod(request.method());

        try {
            Instant start = Instant.now();
            // write session
            WebContext.set(new WebContext(request, response));

            if (isStaticFile(cleanUri)) {
                STATIC_FILE_HANDLER.handle(ctx, request, response);
                isStatic = true;
                return;
            }

            Route route = ROUTE_MATCHER.lookupRoute(request.method(), cleanUri);
            if (null == route) {
                log404(log, method, uri);
                throw new NotFoundException(uri);
            }

            request.initPathParams(route);

            // get method parameters
            signature.setRoute(route);

            // middleware
            if (hasMiddleware && !invokeMiddleware(ROUTE_MATCHER.getMiddleware(), signature)) {
                this.sendFinish(response);
                return;
            }

            // web hook before
            if (hasBeforeHook && !invokeHook(ROUTE_MATCHER.getBefore(cleanUri), signature)) {
                this.sendFinish(response);
                return;
            }

            // execute
            this.routeHandle(signature);

            // webHook
            if (hasAfterHook) {
                this.invokeHook(ROUTE_MATCHER.getAfter(cleanUri), signature);
            }

            this.render(response);

            long cost = log200(log, start, method, uri);
            request.attribute(REQUEST_COST_TIME, cost);
        } catch (Exception e) {
            if (e instanceof BladeException) {
            } else {
                log500(log, method, uri);
            }
            if (null != exceptionHandler) {
                exceptionHandler.handle(e);
            } else {
                log.error("Request execution error", e);
            }
        } finally {
            if (!isStatic) this.sendFinish(response);
            WebContext.remove();
        }
    }

    private void render(Response response) {
        if (!response.isCommit() && StringKit.isNotEmpty(response.modelAndView().getView())) {
            StringWriter sw = new StringWriter();
            try {
                WebContext.blade().templateEngine().render(response.modelAndView(), sw);
                ByteBuf          buffer           = Unpooled.wrappedBuffer(sw.toString().getBytes("utf-8"));
                FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.statusCode()), buffer);
                response.send(fullHttpResponse);
            } catch (Exception e) {
                log.error("Render view error", e);
            }
        }
    }

    /**
     * Actual routing method execution
     *
     * @param signature signature
     */
    public void routeHandle(Signature signature) throws Exception {
        Object target = signature.getRoute().getTarget();
        if (null == target) {
            Class<?> clazz = signature.getAction().getDeclaringClass();
            target = WebContext.blade().getBean(clazz);
            signature.getRoute().setTarget(target);
        }
        if (signature.getRoute().getTargetType() == RouteHandler.class) {
            RouteHandler routeHandler = (RouteHandler) target;
            routeHandler.handle(signature.request(), signature.response());
        } else {
            this.handle(signature);
        }
    }

    /**
     * handle route signature
     *
     * @param signature route request signature
     * @throws Exception throw like parse param exception
     */
    public void handle(Signature signature) throws Exception {
        try {
            Method   actionMethod = signature.getAction();
            Object   target       = signature.getRoute().getTarget();
            Class<?> returnType   = actionMethod.getReturnType();

            Response response = signature.response();

            Path path = target.getClass().getAnnotation(Path.class);
            JSON JSON = actionMethod.getAnnotation(JSON.class);

            boolean isRestful = (null != JSON) || (null != path && path.restful());

            // if request is restful and not InternetExplorer userAgent
            if (isRestful && !signature.request().userAgent().contains(HttpConst.IE_UA)) {
                signature.response().contentType(Const.CONTENT_TYPE_JSON);
            }

            int len = actionMethod.getParameterTypes().length;

            MethodAccess methodAccess = BladeCache.getMethodAccess(target.getClass());
            Object       returnParam  = methodAccess.invoke(target, actionMethod.getName(), len > 0 ? signature.getParameters() : null);

            if (null == returnParam) return;

            if (isRestful) {
                response.json(returnParam);
                return;
            }
            if (returnType == String.class) {
                response.render(returnParam.toString());
                return;
            }
            if (returnType == ModelAndView.class) {
                ModelAndView modelAndView = (ModelAndView) returnParam;
                response.render(modelAndView);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private MethodHandles.Lookup lookup = MethodHandles.lookup();


    private void invokeMethod(Method actionMethod, Object[] args) {
        CallSite site = null;
        try {
            MethodHandle handle = lookup.unreflect(actionMethod);

            site = LambdaMetafactory.metafactory(lookup,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    handle, handle.type());
            Function function = (Function) site.getTarget().invokeExact(args);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    /**
     * Invoke WebHook
     *
     * @param routeSignature current execute route handler signature
     * @param hookRoute      current webhook route handler
     * @return Return true then next handler, and else interrupt request
     * @throws Exception throw like parse param exception
     */
    public boolean invokeHook(Signature routeSignature, Route hookRoute) throws Exception {
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
                returnParam = ReflectKit.invokeMethod(target, hookMethod, routeSignature);
            } else if (len == 2) {
                returnParam = ReflectKit.invokeMethod(target, hookMethod, routeSignature.request(), routeSignature.response());
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

    public boolean invokeMiddleware(List<Route> middleware, Signature signature) throws BladeException {
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
    public boolean invokeHook(List<Route> hooks, Signature signature) throws Exception {
        for (Route hook : hooks) {
            if (hook.getTargetType() == RouteHandler.class) {
                RouteHandler routeHandler = (RouteHandler) hook.getTarget();
                routeHandler.handle(signature.request(), signature.response());
            } else {
                boolean flag = this.invokeHook(signature, hook);
                if (!flag) return false;
            }
        }
        return true;
    }

    private boolean isStaticFile(String uri) {
        Optional<String> result = STATICS.stream().filter(s -> s.equals(uri) || uri.startsWith(s)).findFirst();
        return result.isPresent();
    }

    private void sendFinish(Response response) {
        if (!response.isCommit()) {
            response.body(Unpooled.EMPTY_BUFFER);
        }
    }

}
