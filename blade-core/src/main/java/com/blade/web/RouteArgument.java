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

/**
 * 路由参数注入器
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class RouteArgument {

	/**
	 * 获取方法内的参数
	 * 
	 * @param request		Request对象，用于注入到method参数列表中
	 * @param response		Response对象，用于注入到method参数列表中
	 * @param route			当前操作的路由对象
	 * @param params		params参数列表
	 * @return				返回生成后的参数数组
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
	 * 执行路由方法
	 * 
	 * @param object		方法的实例，即该方法所在类的对象
	 * @param method		要执行的method
	 * @param request		Request对象，作为参数注入
	 * @param response		Response对象，作为参数注入
	 * @param route			当前操作的路由对象
	 * @return				返回方法执行后的返回值
	 */
	public static Object executeMethod(Object object, Method method, Request request, Response response){
		int len = method.getParameterTypes().length;
		method.setAccessible(true);
		if(len > 0){
			Object[] args = getArgs(request, response, method.getParameterTypes());
			return ReflectKit.invokeMehod(object, method, args);
		} else {
			return ReflectKit.invokeMehod(object, method);
		}
	}
}
