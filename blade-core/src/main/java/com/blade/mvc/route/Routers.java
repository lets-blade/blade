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
package com.blade.mvc.route;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.kit.Assert;
import com.blade.kit.reflect.ReflectKit;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;

/**
 * Registration, management route
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.5
 */
public class Routers {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Routers.class);
	
	private Map<String, Route> routes = null;
	
	private Map<String, Route> interceptors = null;
	
	private static final String METHOD_NAME = "handle";
	
	public Routers() {
		this.routes = new HashMap<String, Route>();
		this.interceptors = new HashMap<String, Route>();
	}
	
	public Map<String, Route> getRoutes() {
		return routes;
	}
	
	public Map<String, Route> getInterceptors() {
		return interceptors;
	}
	
	public void addRoute(Route route) {
		String path = route.getPath();
		HttpMethod httpMethod = route.getHttpMethod();
		String key = path + "#" + httpMethod.toString();
		
		// existent
		if (null != this.routes.get(key)) {
			LOGGER.warn("\tRoute {} -> {} has exist", path, httpMethod.toString());
		}
		
		if(httpMethod == HttpMethod.BEFORE || httpMethod == HttpMethod.AFTER){
			if (null != this.interceptors.get(key)) {
				LOGGER.warn("\tInterceptor {} -> {} has exist", path, httpMethod.toString());
			}
			this.interceptors.put(key, route);
			LOGGER.debug("Add Interceptor => {}", route);
		} else {
			this.routes.put(key, route);
			LOGGER.debug("Add Route => {}", route);
		}
	}
	
	public void addRoutes(List<Route> routes) {
		Assert.notNull(routes);
		for(Route route : routes){
			this.addRoute(route);
		}
	}
	
	public void addRoute(HttpMethod httpMethod, String path, RouteHandler handler, String methodName) throws NoSuchMethodException {
		Class<?> handleType = handler.getClass();
		Method method = handleType.getMethod(methodName, Request.class, Response.class);
		addRoute(httpMethod, path, handler, RouteHandler.class, method);
	}
	
	public void addRoute(HttpMethod httpMethod, String path, Object controller, Class<?> controllerType, Method method) {
		
		Assert.notNull(httpMethod);
		Assert.notBlank(path);
		Assert.notNull(method);
		
		String key = path + "#" + httpMethod.toString();
		// existent
		if (null != this.routes.get(key)) {
			LOGGER.warn("\tRoute {} -> {} has exist", path, httpMethod.toString());
		}
		
		Route route = new Route(httpMethod, path, controller, controllerType, method);
		if(httpMethod == HttpMethod.BEFORE || httpMethod == HttpMethod.AFTER){
			if (null != this.interceptors.get(key)) {
				LOGGER.warn("\tInterceptor {} -> {} has exist", path, httpMethod.toString());
			}
			this.interceptors.put(key, route);
			LOGGER.info("Add Interceptor: {}", route);
		} else {
			this.routes.put(key, route);
			LOGGER.info("Add Route => {}", route);
		}
		
	}
	
	public void route(String path, RouteHandler handler, HttpMethod httpMethod) {
		try {
			addRoute(httpMethod, path, handler, METHOD_NAME);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void route(String[] paths, RouteHandler handler, HttpMethod httpMethod) {
		for(String path : paths){
			route(path, handler, httpMethod);
		}
	}
	
	private Map<String, Method[]> classMethosPool = new HashMap<String, Method[]>(8);
	
	public void route(String path, Class<?> clazz, String methodName) {
		
		Assert.notNull(path, "Route path not is null!");
		Assert.notNull(clazz, "Class Type not is null!");
		Assert.notNull(methodName, "Method name not is null");
		
		HttpMethod httpMethod = HttpMethod.ALL;
		if(methodName.indexOf(":") != -1){
			String[] methodArr = methodName.split(":");
			httpMethod = HttpMethod.valueOf(methodArr[0].toUpperCase());
			methodName = methodArr[1];
		}
		try {
			
			Method[] methods = classMethosPool.get(clazz.getName());
			if(null == methods){
				methods = clazz.getMethods();
				classMethosPool.put(clazz.getName(), methods);
			}
			if(null != methods){
				for(Method method : methods){
					if(method.getName().equals(methodName)){
						addRoute(httpMethod, path, ReflectKit.newInstance(clazz), clazz, method);
					}
				}
			}
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InstantiationException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public void route(String path, Class<?> clazz, String methodName, HttpMethod httpMethod) {
		try {
			Assert.notNull(path, "Route path not is null!");
			Assert.notNull(clazz, "Class Type not is null!");
			Assert.notNull(methodName, "Method name not is null");
			Assert.notNull(httpMethod, "Request Method not is null");
			Method method = clazz.getMethod(methodName, Request.class, Response.class);
			addRoute(httpMethod, path, null, clazz, method);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void buildRoute(String path, Class<?> clazz, Method method, HttpMethod httpMethod) {
		addRoute(httpMethod, path, null, clazz, method);
	}
	
}
