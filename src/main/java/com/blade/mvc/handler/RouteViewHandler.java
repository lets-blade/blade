package com.blade.mvc.handler;

import com.blade.Blade;
import com.blade.ioc.Ioc;
import com.blade.kit.ReflectKit;
import com.blade.mvc.annotation.JSON;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import com.blade.mvc.ui.ModelAndView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RouteViewHandler {

    private Ioc ioc;

    public RouteViewHandler(Blade blade) {
        this.ioc = blade.ioc();
    }

    public boolean handle(Signature signature) throws Exception {
        try {
            Method   actionMethod = signature.getAction();
            Object   target       = signature.getRoute().getTarget();
            Class<?> returnType   = actionMethod.getReturnType();

            Response response = signature.response();

            Path    path      = target.getClass().getAnnotation(Path.class);
            JSON    JSON      = actionMethod.getAnnotation(JSON.class);
            boolean isRestful = (null != JSON) || (null != path && path.restful());
            if (isRestful && !signature.request().userAgent().contains("MSIE")) {
                signature.response().contentType("application/json; charset=UTF-8");
            }

            int    len = actionMethod.getParameterTypes().length;
            Object returnParam;
            if (len > 0) {
                returnParam = ReflectKit.invokeMethod(target, actionMethod, signature.getParameters());
            } else {
                returnParam = ReflectKit.invokeMethod(target, actionMethod);
            }

            if (null != returnParam) {
                if (isRestful) {
                    response.json(returnParam);
                } else {
                    if (returnType == String.class) {
                        response.render(returnParam.toString());
                    } else if (returnType == ModelAndView.class) {
                        ModelAndView modelAndView = (ModelAndView) returnParam;
                        response.render(modelAndView);
                    }
                }
                return true;
            }
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) e = (Exception) e.getCause();
            throw e;
        }
        return false;
    }

    public boolean invokeHook(Signature routeSignature, Route route) throws Exception {
        Method actionMethod = route.getAction();
        Object target       = route.getTarget();
        if (null == target) {
            Class<?> clazz = route.getAction().getDeclaringClass();
            target = ioc.getBean(clazz);
            route.setTarget(target);
        }

        // execute
        int len = actionMethod.getParameterTypes().length;
        actionMethod.setAccessible(true);

        Object returnParam;
        if (len > 0) {
            Signature signature = Signature.builder().route(route)
                    .request(routeSignature.request()).response(routeSignature.response())
                    .parameters(routeSignature.getParameters())
                    .action(actionMethod).build();

            Object[] args = MethodArgument.getArgs(signature);
            returnParam = ReflectKit.invokeMethod(target, actionMethod, args);
        } else {
            returnParam = ReflectKit.invokeMethod(target, actionMethod);
        }

        if (null != returnParam) {
            Class<?> returnType = returnParam.getClass();
            if (returnType == Boolean.class || returnType == boolean.class) {
                return (Boolean) returnParam;
            }
        }
        return true;
    }

}