package com.blade.mvc.handler;

import com.blade.Blade;
import com.blade.exception.BladeException;
import com.blade.exception.InternalErrorException;
import com.blade.ioc.Ioc;
import com.blade.kit.BladeKit;
import com.blade.kit.ReflectKit;
import com.blade.mvc.Const;
import com.blade.mvc.annotation.JSON;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import com.blade.mvc.ui.ModelAndView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Route handler invoke
 *
 * @author biezhi
 * 2017/9/20
 */
public class RequestInvoker {

    private final Blade blade;
    private final Ioc   ioc;

    public RequestInvoker(Blade blade) {
        this.blade = blade;
        this.ioc = blade.ioc();
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
            target = blade.getBean(clazz);
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
            if (isRestful && !signature.request().userAgent().contains("MSIE")) {
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
            target = ioc.getBean(clazz);
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