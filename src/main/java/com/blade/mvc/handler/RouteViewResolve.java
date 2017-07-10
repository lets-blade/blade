package com.blade.mvc.handler;

import com.blade.Blade;
import com.blade.exception.BladeException;
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

public class RouteViewResolve {

    private Ioc ioc;

    public RouteViewResolve(Blade blade) {
        this.ioc = blade.ioc();
    }

    public boolean handle(Signature signature) throws Exception {
        try {
            Method actionMethod = signature.getAction();
            Object target = signature.getRoute().getTarget();
            Class<?> returnType = actionMethod.getReturnType();

            Response response = signature.response();

            Path path = target.getClass().getAnnotation(Path.class);
            JSON JSON = actionMethod.getAnnotation(JSON.class);
            boolean isRestful = (null != JSON) || (null != path && path.restful());
            if (isRestful && !signature.request().userAgent().contains("MSIE")) {
                signature.response().contentType("application/json; charset=UTF-8");
            }

            int len = actionMethod.getParameterTypes().length;
            Object returnParam;
            if (len > 0) {
                returnParam = ReflectKit.invokeMehod(target, actionMethod, signature.getParameters());
            } else {
                returnParam = ReflectKit.invokeMehod(target, actionMethod);
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
            Throwable t = e;
            if (e instanceof InvocationTargetException) {
                t = e.getCause();
            }
            if (t instanceof BladeException) {
                throw (BladeException) t;
            }
            throw new BladeException(t);
        }
        return false;
    }

    public boolean invokeHook(Signature routeSignature, Route route) throws BladeException {
        Method actionMethod = route.getAction();
        Object target = route.getTarget();
        if (null == target) {
            Class<?> clazz = route.getAction().getDeclaringClass();
            target = ioc.getBean(clazz);
            route.setTarget(target);
        }

        // execute
        int len = actionMethod.getParameterTypes().length;
        actionMethod.setAccessible(true);
        try {
            Object returnParam;
            if (len > 0) {
                Signature signature = Signature.builder()
                        .route(route)
                        .request(routeSignature.request())
                        .response(routeSignature.response())
                        .parameters(routeSignature.getParameters())
                        .action(actionMethod)
                        .build();

                Object[] args = MethodArgument.getArgs(signature);
                returnParam = ReflectKit.invokeMehod(target, actionMethod, args);
            } else {
                returnParam = ReflectKit.invokeMehod(target, actionMethod);
            }

            if (null != returnParam) {
                Class<?> returnType = returnParam.getClass();
                if (returnType == Boolean.class || returnType == boolean.class) {
                    return (Boolean) returnParam;
                }
            }
            return true;
        } catch (Exception e) {
            throw new BladeException(e);
        }
    }

}