package com.blade.mvc.handler;

import com.blade.exception.BladeException;
import com.blade.exception.InternalErrorException;
import com.blade.exception.NotFoundException;
import com.blade.kit.BladeKit;
import com.blade.kit.ReflectKit;
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
import com.blade.mvc.ui.ModelAndView;
import com.blade.server.netty.HttpConst;
import com.blade.server.netty.HttpServerHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * Request route execution thrad
 *
 * @author biezhi
 * @date 2017/12/21
 */
@Slf4j
public class RequestExecution implements Runnable {

    @Getter
    private final ChannelHandlerContext ctx;
    private final FullHttpRequest       fullHttpRequest;
    private final HttpServerHandler     httpServerHandler;

    public RequestExecution(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest, HttpServerHandler httpServerHandler) {
        this.ctx = ctx;
        this.fullHttpRequest = fullHttpRequest;
        this.httpServerHandler = httpServerHandler;
    }

    @Override
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("{}", fullHttpRequest);
        }
        if(null == this.ctx || this.ctx.isRemoved()){
            return;
        }
        Request  request  = HttpRequest.build(ctx, fullHttpRequest);
        Response response = HttpResponse.build(ctx);
        // route signature
        Signature signature = Signature.builder().request(request).response(response).build();
        boolean   isStatic  = false;
        try {

            // request uri
            String uri = request.uri();

            // write session
            WebContext.set(new WebContext(request, response));

            if (this.isStaticFile(uri)) {
                //staticFileHandler.handle(ctx, request, response);
                isStatic = true;
                return;
            }

            Route route = httpServerHandler.routeMatcher.lookupRoute(request.method(), uri);
            if (null == route) {
                log.warn("Not Found\t{}", uri);
                throw new NotFoundException(uri);
            }

            log.info("{}\t{}\t{}", request.protocol(), request.method(), uri);

            request.initPathParams(route);

            // get method parameters
            signature.setRoute(route);

            // middleware
            if (httpServerHandler.hasMiddleware && !this.invokeMiddleware(httpServerHandler.routeMatcher.getMiddleware(), signature)) {
                this.sendFinish(response);
                return;
            }

            // web hook before
            if (httpServerHandler.hasBeforeHook && !this.invokeHook(httpServerHandler.routeMatcher.getBefore(uri), signature)) {
                this.sendFinish(response);
                return;
            }

            // execute
            signature.setRoute(route);
            this.routeHandle(signature);

            // webHook
            if (httpServerHandler.hasAfterHook) {
                this.invokeHook(httpServerHandler.routeMatcher.getAfter(uri), signature);
            }
        } catch (Exception e) {
            if (null != httpServerHandler.exceptionHandler) {
                httpServerHandler.exceptionHandler.handle(e);
            } else {
                log.error("Blade Invoke Error", e);
            }
        } finally {
            if (!isStatic) this.sendFinish(response);
            WebContext.remove();
        }
    }

    boolean isStaticFile(String uri) {
        if (null == uri) return false;
        Optional<String> result = httpServerHandler.statics.stream().filter(s -> s.equals(uri) || uri.startsWith(s)).findFirst();
        return result.isPresent();
    }

    void sendFinish(Response response) {
        if (!response.isCommit()) {
            response.body(Unpooled.EMPTY_BUFFER);
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

            int    len         = actionMethod.getParameterTypes().length;
            Object returnParam = ReflectKit.invokeMethod(target, actionMethod, len > 0 ? signature.getParameters() : null);
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
            if (e instanceof InvocationTargetException) e = (Exception) e.getCause();
            throw e;
        }
    }

    /**
     * invoke webhook
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

}
