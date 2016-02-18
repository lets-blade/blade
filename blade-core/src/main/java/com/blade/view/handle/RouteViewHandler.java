package com.blade.view.handle;

import java.lang.reflect.Method;

import com.blade.route.Route;
import com.blade.view.template.ModelAndView;
import com.blade.web.DispatchKit;
import com.blade.web.http.Request;
import com.blade.web.http.Response;

import blade.kit.json.JSONHelper;
import blade.kit.json.JSONValue;
import blade.kit.reflect.ConvertKit;
import blade.kit.reflect.ReflectKit;

public class RouteViewHandler {

	/**
	 * Parameters in the method
	 * 
	 * @param request		Request object for injection to the method parameter list
	 * @param response		Response object for injection to the method parameter list
	 * @param params		Params parameter list
	 * @return				Return the generated array of parameters
	 */
	public Object[] getArgs(Request request, Response response, Class<?>[] params){
		
		int len = params.length;
		Object[] args = new Object[len];
		
		for(int i=0; i<len; i++){
			Class<?> paramTypeClazz = params[i];
			if(paramTypeClazz.getName().equals(Request.class.getName())){
				args[i] = request;
			}
			if(paramTypeClazz.getName().equals(Response.class.getName())){
				args[i] = response;
			}
		}
		return args;
	}

	public void handle(Request request, Response response, Route route){
		Method actionMethod = route.getAction();
		Object target = route.getTarget();
		// execute
		
		int len = actionMethod.getParameterTypes().length;
		actionMethod.setAccessible(true);
		try {
			Object returnParam = null;
			if(len > 0){
				Object[] args = getArgs(request, response, actionMethod.getParameterTypes());
				returnParam = ReflectKit.invokeMehod(target, actionMethod, args);
			} else {
				returnParam = ReflectKit.invokeMehod(target, actionMethod);
			}
			
			if(null != returnParam){
				Class<?> returnType = returnParam.getClass();
				if(returnType == String.class){
					response.text(returnParam.toString());
				} else if(returnType == ModelAndView.class){
					ModelAndView modelAndView = (ModelAndView) returnParam;
					response.render( modelAndView );
				} else if(ConvertKit.isBasicType(returnType)){
					response.text(returnParam.toString());
				} else {
					JSONValue jsonValue = JSONHelper.objectAsJsonValue(returnParam);
					String json = jsonValue.toString();
					response.json(json);
				}
			}
		} catch (Exception e) {
			request.abort();
			DispatchKit.printError(e, 500, response.raw());
		}
		
	}
	
	public boolean intercept(Request request, Response response, Route route){
		Method actionMethod = route.getAction();
		Object target = route.getTarget();
		
		// execute
		int len = actionMethod.getParameterTypes().length;
		actionMethod.setAccessible(true);
		try {
			Object returnParam = null;
			if(len > 0){
				Object[] args = getArgs(request, response, actionMethod.getParameterTypes());
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
			DispatchKit.printError(e, 500, response.raw());
		}
		return false;
	}
	
}
