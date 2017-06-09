package com.blade.mvc.handler;

import com.blade.Blade;
import com.blade.BladeException;
import com.blade.ioc.Ioc;
import com.blade.kit.ReflectKit;
import com.blade.mvc.annotation.JSON;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import com.blade.mvc.ui.ModelAndView;
import com.blade.mvc.ui.template.TemplateEngine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RouteViewResolve {

    private Ioc ioc;
    private TemplateEngine templateEngine;

    public RouteViewResolve(Blade blade) {
        this.ioc = blade.ioc();
        this.templateEngine = blade.templateEngine();
    }

    public boolean handle(Request request, Response response, Route route) {
        try {
            Method actionMethod = route.getAction();
            Object target = route.getTarget();
            Class<?> returnType = actionMethod.getReturnType();

            Path path = target.getClass().getAnnotation(Path.class);
            JSON JSON = actionMethod.getAnnotation(JSON.class);
            boolean isRestful = (null != JSON) || (null != path && path.restful());
            if (isRestful && !request.userAgent().contains("MSIE")) {
                response.contentType("application/json; charset=UTF-8");
            }

            int len = actionMethod.getParameterTypes().length;
            Object returnParam;
            if (len > 0) {
                Object[] args = MethodArgument.getArgs(request, response, actionMethod);
                returnParam = ReflectKit.invokeMehod(target, actionMethod, args);
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

    public boolean invokeHook(Request request, Response response, Route route) throws BladeException {
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
                Object[] args = MethodArgument.getArgs(request, response, actionMethod);
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