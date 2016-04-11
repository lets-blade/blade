package com.blade.view.handle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.blade.ioc.Ioc;
import com.blade.route.Route;
import com.blade.route.annotation.PathVariable;
import com.blade.route.annotation.RequestParam;
import com.blade.view.ModelAndView;
import com.blade.view.ModelMap;
import com.blade.web.DispatchKit;
import com.blade.web.http.Request;
import com.blade.web.http.Response;
import com.blade.web.http.wrapper.Session;
import com.blade.web.multipart.FileItem;

import blade.kit.reflect.ReflectKit;

public class RouteViewHandler {
	
	private Ioc ioc;
	
	public RouteViewHandler(Ioc ioc) {
		this.ioc = ioc;
	}
	
	private Object getPathParam(Class<?> parameterType, String val, Request request){
		Object result = null;
		if(parameterType.equals(String.class)){
			result = request.param(val);
		} else if(parameterType.equals(Integer.class) || parameterType.equals(int.class)){
			result = request.paramAsInt(val);
		} else if(parameterType.equals(Boolean.class) || parameterType.equals(boolean.class)){
			result = request.paramAsBool(val);
		} else if(parameterType.equals(Long.class) || parameterType.equals(long.class)){
			result = request.paramAsLong(val);
		}
		return result;
	}
	
	private Object getRequestParam(Class<?> parameterType, String val, String defaultValue, Request request){
		Object result = null;
		if(parameterType.equals(String.class)){
			String value = request.query(val);
			if(null == value){
				result = defaultValue;
			} else {
				result = value;
			}
		} else if(parameterType.equals(Integer.class) || parameterType.equals(int.class)){
			Integer value = request.queryAsInt(val);
			if(null == value){
				result = Integer.valueOf(defaultValue);
			} else {
				result = value;
			}
		} else if(parameterType.equals(Boolean.class) || parameterType.equals(boolean.class)){
			Boolean value = request.queryAsBool(val);
			if(null == value){
				result = Boolean.valueOf(defaultValue);
			} else {
				result = value;
			}
		} else if(parameterType.equals(Long.class) || parameterType.equals(long.class)){
			Long value = request.queryAsLong(val);
			if(null == value){
				result = Long.valueOf(defaultValue);
			} else {
				result = value;
			}
		}
		return result;
	}
	
	/**
	 * Parameters in the method
	 * 
	 * @param request		Request object for injection to the method parameter list
	 * @param response		Response object for injection to the method parameter list
	 * @param actionMethod	Execute method
	 * @return				Return the generated array of parameters
	 */
	public Object[] getArgs(Request request, Response response, Method actionMethod){
		Annotation[][] parameterAnnotations = actionMethod.getParameterAnnotations();
		Class<?>[] parameterTypes = actionMethod.getParameterTypes();
		int len = parameterTypes.length;
		Object[] args = new Object[len];
		actionMethod.setAccessible(true);
		
		int i = 0;
		if(parameterAnnotations.length > 0){
			for (Annotation[] annotations : parameterAnnotations) {
				Class<?> parameterType = parameterTypes[i];
				for (Annotation annotation : annotations) {
					Class<?> anType = annotation.annotationType();
					if(anType.equals(PathVariable.class)){
						PathVariable pathVariable = (PathVariable) annotation;
						String val = pathVariable.value();
						Object value = getPathParam(parameterType, val, request);
						args[i] = value;
					} else if(anType.equals(RequestParam.class)) {
						RequestParam requestParam = (RequestParam) annotation;
						String val = requestParam.value();
						Object value = getRequestParam(parameterType, val, requestParam.defaultValue(), request);
						args[i] = value;
					}
				}
				i++;
			}
		}
		
		for(i=0; i<len; i++){
			Class<?> paramTypeClazz = parameterTypes[i];
			if(paramTypeClazz.equals(Request.class)){
				args[i] = request;
			} else if(paramTypeClazz.equals(Response.class)){
				args[i] = response;
			} else if(paramTypeClazz.equals(Session.class)){
				args[i] = request.session();
			} else if(paramTypeClazz.equals(ModelMap.class)){
				args[i] = new ModelMap(request);
			} else if(paramTypeClazz.equals(HttpServletRequest.class)){
				args[i] = request.raw();
			} else if(paramTypeClazz.equals(HttpServletResponse.class)){
				args[i] = response.raw();
			} else if(paramTypeClazz.equals(HttpSession.class)){
				args[i] = request.raw().getSession();
			} else if(paramTypeClazz.equals(FileItem.class)){
				FileItem[] fileItems = request.files();
				if(null != fileItems && fileItems.length > 0){
					args[i] = fileItems[0];
				}
			}
		}
		return args;
	}

	public void handle(Request request, Response response, Route route){
		Method actionMethod = route.getAction();
		Object target = route.getTarget();
		// execute
		
		int len = actionMethod.getParameterTypes().length;
		try {
			Object returnParam = null;
			if(len > 0){
				Object[] args = getArgs(request, response, actionMethod);
				returnParam = ReflectKit.invokeMehod(target, actionMethod, args);
			} else {
				returnParam = ReflectKit.invokeMehod(target, actionMethod);
			}
			
			if(null != returnParam){
				Class<?> returnType = returnParam.getClass();
				if(returnType == String.class){
					response.render( returnParam.toString() );
				} else if(returnType == ModelAndView.class){
					ModelAndView modelAndView = (ModelAndView) returnParam;
					response.render( modelAndView );
				}
			}
		} catch (Exception e) {
			request.abort();
			DispatchKit.printError(e, 500, response);
		}
		
	}
	
	public boolean intercept(Request request, Response response, Route route){
		Method actionMethod = route.getAction();
		Object target = route.getTarget();
		
		if(null == target){
			Class<?> clazz = route.getAction().getDeclaringClass();
			target = ioc.getBean(clazz);
			route.setTarget(target);
		}
		
		// execute
		int len = actionMethod.getParameterTypes().length;
		actionMethod.setAccessible(true);
		try {
			Object returnParam = null;
			if(len > 0){
				Object[] args = getArgs(request, response, actionMethod);
				returnParam = ReflectKit.invokeMehod(target, actionMethod, args);
			} else {
				returnParam = ReflectKit.invokeMehod(target, actionMethod);
			}
			
			if(null != returnParam){
				Class<?> returnType = returnParam.getClass();
				if(returnType == Boolean.class || returnType == boolean.class){
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
