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
import com.blade.ioc.Ioc;
import com.blade.route.annotation.After;
import com.blade.route.annotation.Before;
import com.blade.route.annotation.Interceptor;
import com.blade.route.annotation.Path;
import com.blade.route.annotation.Route;
import com.blade.web.http.HttpMethod;

import blade.kit.StringKit;
import blade.kit.resource.ClassPathClassReader;
import blade.kit.resource.ClassReader;

/**
 * Route builder
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class RouteBuilder {
    
    /**
	 * Default route suffix package, the user scan route location, the default is route, users can customize
	 */
    private String pkgRoute = "route";
	
	/**
	 * Default interceptor suffix package, the user scans the interceptor location, the default is interceptor, users can customize
	 */
    private String pkgInterceptor = "interceptor";
	
    /**
     * Class reader, used to scan the class specified in the rules
     */
    private ClassReader classReader = new ClassPathClassReader();
    
    /**
     * IOC container, storage route to IOC
     */
    private Ioc ioc = null;
    
    private Blade blade;
    
    private Routers routers;
    
    public RouteBuilder(Blade blade) {
    	this.blade = blade;
    	this.routers = blade.routers();
    	this.ioc = blade.ioc();
    }
    
    /**
     * Start building route
     */
    public void building() {
        String basePackage = blade.basePackage();
        
        if(StringKit.isNotBlank(basePackage)){
        	
        	// Processing e.g: com.xxx.* representation of recursive scanning package
        	String suffix = basePackage.endsWith(".*") ? ".*" : "";
        	basePackage = basePackage.endsWith(".*") ? basePackage.substring(0, basePackage.length() - 2) : basePackage;
        	
			String routePackage = basePackage + "." + pkgRoute + suffix;
			String interceptorPackage = basePackage + "." + pkgInterceptor + suffix;
			
        	buildRoute(routePackage);
        	buildInterceptor(interceptorPackage);
        	
        } else {
        	// Route
        	String[] routePackages = blade.routePackages();
        	if(null != routePackages && routePackages.length > 0){
        		buildRoute(routePackages);
        	}
        	
    		// Inteceptor
        	String interceptorPackage = blade.interceptorPackage();
        	if(StringKit.isNotBlank(interceptorPackage)){
        		buildInterceptor(interceptorPackage);
        	}
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
			
			boolean recursive = false;
			
			if (packageName.endsWith(".*")) {
				packageName = packageName.substring(0, packageName.length() - 2);
				recursive = true;
			}
			
    		// Scan all Interceptor
    		classes = classReader.getClassByAnnotation(packageName, Interceptor.class, recursive);
    		
    		if(null != classes && classes.size() > 0){
    			for(Class<?> interceptorClazz : classes){
    				parseInterceptor(interceptorClazz);
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
			
			boolean recursive = false;
			
			if (packageName.endsWith(".*")) {
				packageName = packageName.substring(0, packageName.length() - 2);
				recursive = true;
			}
			
    		// Scan all Controoler
    		classes = classReader.getClassByAnnotation(packageName, Path.class, recursive);
    		
    		if(null != classes && classes.size() > 0){
    			for(Class<?> pathClazz : classes){
    				parseRouter(pathClazz);
    			}
    		}
    	}
    	
    }
    
    /**
     * Parse Interceptor
     * 
     * @param interceptor	resolve the interceptor class
     */
    private void parseInterceptor(final Class<?> interceptor){
    	
    	Method[] methods = interceptor.getMethods();
    	if(null == methods || methods.length == 0){
    		return;
    	}
    	
    	ioc.addBean(interceptor);
    	
    	for (Method method : methods) {
			
			Before before = method.getAnnotation(Before.class);
			After after = method.getAnnotation(After.class);
			
			if (null != before) {
				
				String suffix = before.suffix();
				
				String path = getRoutePath(before.value(), "", suffix);
				
				buildInterceptor(path, interceptor, method, HttpMethod.BEFORE);
				
				String[] paths = before.values();
				if(null != paths && paths.length > 0){
					for(String value : paths){
						String pathV = getRoutePath(value, "", suffix);
						buildInterceptor(pathV, interceptor, method, HttpMethod.BEFORE);
					}
				}
			}
			
			if (null != after) {
				
				String suffix = after.suffix();
				
				String path = getRoutePath(after.value(), "", suffix);
				
				buildInterceptor(path, interceptor, method, HttpMethod.AFTER);
				
				String[] paths = after.values();
				if(null != paths && paths.length > 0){
					for(String value : paths){
						String pathV = getRoutePath(value, "", suffix);
						buildInterceptor(pathV, interceptor, method, HttpMethod.AFTER);
					}
				}
			}
		}
    }
    
    /**
     * Parse all routing in a controller
     * 
     * @param controller	resolve the routing class
     */
    private void parseRouter(final Class<?> router){
    	
    	Method[] methods = router.getMethods();
    	if(null == methods || methods.length == 0){
    		return;
    	}
    	
    	ioc.addBean(router);
    	
		final String nameSpace = router.getAnnotation(Path.class).value();
		
		final String suffix = router.getAnnotation(Path.class).suffix();
		
		for (Method method : methods) {
			
			Route mapping = method.getAnnotation(Route.class);
			
			//route method
			if (null != mapping) {
				
				// build 
				String path = getRoutePath(mapping.value(), nameSpace, suffix);
				
				HttpMethod methodType = mapping.method();
				
				buildRoute(router, method, path, methodType);
				
				// build multiple route
				String[] paths = mapping.values();
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