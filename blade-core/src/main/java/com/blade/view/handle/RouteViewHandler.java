package com.blade.view.handle;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.exception.BladeException;
import com.blade.ioc.Ioc;
import com.blade.kit.reflect.ReflectKit;
import com.blade.route.Route;
import com.blade.view.ModelAndView;
import com.blade.web.DispatchKit;
import com.blade.web.http.Request;
import com.blade.web.http.Response;

public class RouteViewHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RouteViewHandler.class);

	private Ioc ioc;

	public RouteViewHandler(Ioc ioc) {
		this.ioc = ioc;
	}

	public void handle(Request request, Response response, Route route) throws BladeException {
		Method actionMethod = route.getAction();
		Object target = route.getTarget();
		// execute

		int len = actionMethod.getParameterTypes().length;
		try {
			Object returnParam = null;
			if (len > 0) {
				Object[] args = MethodArgument.getArgs(request, response, actionMethod);
				returnParam = ReflectKit.invokeMehod(target, actionMethod, args);
			} else {
				returnParam = ReflectKit.invokeMehod(target, actionMethod);
			}

			if (null != returnParam) {
				Class<?> returnType = returnParam.getClass();
				if (returnType == String.class) {
					response.render(returnParam.toString());
				} else if (returnType == ModelAndView.class) {
					ModelAndView modelAndView = (ModelAndView) returnParam;
					response.render(modelAndView);
				}
			}
		} catch (BladeException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("route view error", e);
			request.abort();
			DispatchKit.printError(e, 500, response);
		}

	}

	public boolean intercept(Request request, Response response, Route route) {
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
			Object returnParam = null;
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
			request.abort();
			DispatchKit.printError(e, 500, response);
		}
		return false;
	}

}
