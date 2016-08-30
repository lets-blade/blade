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
package com.blade.route;

import java.lang.reflect.Method;
import java.util.Set;

import com.blade.Blade;
import com.blade.annotation.Controller;
import com.blade.annotation.Intercept;
import com.blade.annotation.Route;
import com.blade.interceptor.Interceptor;
import com.blade.kit.CollectionKit;
import com.blade.kit.StringKit;
import com.blade.kit.reflect.ReflectKit;
import com.blade.kit.resource.ClassReader;
import com.blade.kit.resource.DynamicClassReader;
import com.blade.web.http.HttpMethod;
import com.blade.web.http.Request;
import com.blade.web.http.Response;

/**
 * Route builder
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class RouteBuilder {
    
    /**
     * Class reader, used to scan the class specified in the rules
     */
    private ClassReader classReader;
    
    private Routers routers;
    
    private String[] routePackages;
    
    private String interceptorPackage;
    
    public RouteBuilder(Routers routers) {
    	this.routers = routers;
    	this.classReader = DynamicClassReader.getClassReader();
    }
    
    /**
     * Start building route
     */
    public void building() {
    	
    	this.routePackages = Blade.$().config().getRoutePackages();
    	this.interceptorPackage = Blade.$().config().getInterceptorPackage();
    	
    	// Route
    	if(null != routePackages && routePackages.length > 0){
    		this.buildRoute(routePackages);
    	}
    	
		// Inteceptor
    	if(StringKit.isNotBlank(interceptorPackage)){
    		this.buildInterceptor(interceptorPackage);
    	}
    	
    }
    
    /**
     * Build interceptor
     * 
     * @param interceptorPackages	add the interceptor package
     */
    private void buildInterceptor(String... interceptorPackages){
    	
    	// Scan all Interceptor
		Set<Class<?>> classes = null;
		
    	// Traversal Interceptor
		for(String packageName : interceptorPackages){
			
    		// Scan all Interceptor
			classes = classReader.getClass(packageName, Interceptor.class, false);
    		if(CollectionKit.isNotEmpty(classes)){
    			for(Class<?> interceptorClazz : classes){
    				addInterceptor(interceptorClazz);
    			}
    		}
    	}
    }
    
    /**
     * Build Route
     * 
     * @param routePackages		route packets to add
     */
    private void buildRoute(String... routePackages){
    	Set<Class<?>> classes = null;
    	// Traverse route
		for(String packageName : routePackages){
			// Scan all Controoler
    		classes = classReader.getClassByAnnotation(packageName, Controller.class, true);
    		if(CollectionKit.isNotEmpty(classes)){
    			for(Class<?> pathClazz : classes){
    				addRouter(pathClazz);
    			}
    		}
    	}
    	
    }
    
    /**
     * Parse Interceptor
     * 
     * @param interceptor	resolve the interceptor class
     */
    public void addInterceptor(final Class<?> interceptor){
    	
    	boolean hasInterface = ReflectKit.hasInterface(interceptor, Interceptor.class);
    	
    	if(null == interceptor || !hasInterface){
    		return;
    	}
    	
//    	ioc.addBean(interceptor);
    	
    	Intercept intercept = interceptor.getAnnotation(Intercept.class);
    	String partten = "/.*";
    	if(null != intercept){
    		partten = intercept.value();
    	}
    	
    	try {
			Method before = interceptor.getMethod("before", Request.class, Response.class);
			Method after = interceptor.getMethod("after", Request.class, Response.class);
			buildInterceptor(partten, interceptor, before, HttpMethod.BEFORE);
			buildInterceptor(partten, interceptor, after, HttpMethod.AFTER);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
    	
    }
    
    /**
     * Parse all routing in a controller
     * 
     * @param controller	resolve the routing class
     */
    public void addRouter(final Class<?> router){
    	
    	Method[] methods = router.getMethods();
    	if(null == methods || methods.length == 0){
    		return;
    	}
    	
		final String nameSpace = router.getAnnotation(Controller.class).value();
		
		final String suffix = router.getAnnotation(Controller.class).suffix();
		
		for (Method method : methods) {
			Route mapping = method.getAnnotation(Route.class);
			//route method
			if (null != mapping) {
				// build multiple route
				HttpMethod methodType = mapping.method();
				String[] paths = mapping.value();
				if(null != paths && paths.length > 0){
					for(String value : paths){
						String pathV = getRoutePath(value, nameSpace, suffix);
						buildRoute(router, method, pathV, methodType);
					}
				}
			}
		}
    }
    
    private String getRoutePath(String value, String nameSpace, String suffix){
    	String path = value.startsWith("/") ? value : "/" + value;
    	
    	nameSpace = nameSpace.startsWith("/") ? nameSpace : "/" + nameSpace;
		path = nameSpace + path;
		
		path = path.replaceAll("[/]+", "/");
		
		path = path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
		
		path = path + suffix;
		
		return path;
    }
    
    /**
     * Build a route
     * 
     * @param target		route target execution class 
     * @param execMethod	route execution method 
     * @param path			route path
     * @param method		route httpmethod
     */
    private void buildRoute(Class<?> clazz, Method execMethod, String path, HttpMethod method){
    	routers.buildRoute(path, clazz, execMethod, method);
    }
    
    /**
     * Build a route
     * 
     * @param path			route path
     * @param target		route target execution class 
     * @param execMethod	route execution method 
     * @param method		route httpmethod
     */
    private void buildInterceptor(String path, Class<?> clazz, Method execMethod, HttpMethod method){
    	routers.buildRoute(path, clazz, execMethod, method);
    }
    
}