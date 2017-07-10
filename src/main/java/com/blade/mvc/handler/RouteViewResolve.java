package com.blade.mvc.handler;

import com.blade.Blade;
import com.blade.exception.BladeException;
import com.blade.ioc.Ioc;
import com.blade.kit.ReflectKit;
import com.blade.mvc.annotation.JSON;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.hook.Invoker;
import com.blade.mvc.http.Response;
import com.blade.mvc.ui.ModelAndView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RouteViewResolve {

    private Ioc ioc;

    public RouteViewResolve(Blade blade) {
        this.ioc = blade.ioc();
    }

    public boolean handle(Invoker invoker) throws Exception {
        try {
            Method actionMethod = invoker.getAction();
            Object target = invoker.getRoute().getTarget();
            Class<?> returnType = actionMethod.getReturnType();

            Response response = invoker.response();

            Path path = target.getClass().getAnnotation(Path.class);
            JSON JSON = actionMethod.getAnnotation(JSON.class);
            boolean isRestful = (null != JSON) || (null != path && path.restful());
            if (isRestful && !invoker.request().userAgent().contains("MSIE")) {
                invoker.response().contentType("application/json; charset=UTF-8");
            }

            int len = actionMethod.getParameterTypes().length;
            Object returnParam;
            if (len > 0) {
                returnParam = ReflectKit.invokeMehod(target, actionMethod, invoker.getParameters());
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

    public boolean invokeHook(Invoker invoker) throws BladeException {
        Method actionMethod = invoker.getAction();
        Object target = invoker.getRoute().getTarget();
        if (null == target) {
            Class<?> clazz = invoker.getAction().getDeclaringClass();
            target = ioc.getBean(clazz);
            invoker.getRoute().setTarget(target);
        }

        // execute
        int len = actionMethod.getParameterTypes().length;
        actionMethod.setAccessible(true);
        try {
            Object returnParam;
            if (len > 0) {
                returnParam = ReflectKit.invokeMehod(target, actionMethod, invoker.getParameters());
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