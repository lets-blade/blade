/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.web;

import java.lang.reflect.Method;

import blade.kit.ReflectKit;

import com.blade.web.http.Request;
import com.blade.web.http.Response;
import com.blade.web.http.ResponsePrint;

/**
 * Route parameters of injector
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class RouteArgument {

	/**
	 * Parameters in the method
	 * 
	 * @param request		Request object for injection to the method parameter list
	 * @param response		Response object for injection to the method parameter list
	 * @param params		Params parameter list
	 * @return				Return the generated array of parameters
	 */
	public static Object[] getArgs(Request request, Response response, Class<?>[] params){
		
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
	
	/**
	 * Implementation route
	 * 
	 * @param object		The instance of the method, that is, the object of the method's class.
	 * @param method		Method to execute
	 * @param request		Request object, as parameter injection
	 * @param response		Response object, as parameter injection
	 * @return				Return value after the method returns
	 */
	public static Object executeMethod(Object object, Method method, Request request, Response response){
		int len = method.getParameterTypes().length;
		method.setAccessible(true);
		try {
			if(len > 0){
				Object[] args = getArgs(request, response, method.getParameterTypes());
				return ReflectKit.invokeMehod(object, method, args);
			} else {
				return ReflectKit.invokeMehod(object, method);
			}
		} catch (Exception e) {
			request.abort();
			ResponsePrint.printError(e, 500, response.raw());
		}
		return null;
	}
}
