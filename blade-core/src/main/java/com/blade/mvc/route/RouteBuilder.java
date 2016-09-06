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

import static com.blade.Blade.$;

import java.lang.reflect.Method;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.context.DynamicContext;
import com.blade.kit.CollectionKit;
import com.blade.kit.StringKit;
import com.blade.kit.reflect.ReflectKit;
import com.blade.kit.resource.ClassInfo;
import com.blade.kit.resource.ClassReader;
import com.blade.mvc.annotation.Controller;
import com.blade.mvc.annotation.Intercept;
import com.blade.mvc.annotation.RestController;
import com.blade.mvc.annotation.Route;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.interceptor.Interceptor;
/**
 * Route builder
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.5
 */
public class RouteBuilder {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(RouteBuilder.class);
	
    /**
     * Class reader, used to scan the class specified in the rules
     */
    private ClassReader classReader;
    
    private Routers routers;
    
    private String[] routePackages;
    
    private String interceptorPackage;
    
    public RouteBuilder(Routers routers) {
    	this.routers = routers;
    	this.classReader = DynamicContext.getClassReader();
    }
    
    /**
     * Start building route
     */
    public void building() {
    	
    	this.routePackages = $().applicationConfig().getRoutePkgs();
    	this.interceptorPackage = $().applicationConfig().getInterceptorPkg();
    	
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
		Set<ClassInfo> classes = null;
		
    	// Traversal Interceptor
		for(int i=0, len=interceptorPackages.length; i<len; i++){
			// Scan all Interceptor
			classes = classReader.getClass(interceptorPackages[i], Interceptor.class, false);
    		if(CollectionKit.isNotEmpty(classes)){
    			for(ClassInfo classInfo : classes){
    				Class<?> interceptorClazz = classInfo.getClazz();
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
    	Set<ClassInfo> classes = null;
    	// Traverse route
    	for(int i=0,len=routePackages.length; i<len; i++){
    		// Scan all Controoler
    		classes = classReader.getClassByAnnotation(routePackages[i], Controller.class, true);
    		if(CollectionKit.isNotEmpty(classes)){
    			for(ClassInfo classInfo : classes){
    				Class<?> pathClazz = classInfo.getClazz(); 
    				this.addRouter(pathClazz);
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
    	String nameSpace = null, suffix = null;
    	
    	if(null != router.getAnnotation(Controller.class)){
    		nameSpace = router.getAnnotation(Controller.class).value();
    		suffix = router.getAnnotation(Controller.class).suffix();
    	}
    	
    	if(null != router.getAnnotation(RestController.class)){
    		nameSpace = router.getAnnotation(RestController.class).value();
    		suffix = router.getAnnotation(RestController.class).suffix();
    	}
    	
    	if(null == nameSpace && null == suffix){
    		LOGGER.warn("Route [{}] not controller annotation", router.getName());
    		return;
    	}
		for (int i = 0, len = methods.length; i < len; i++) {
			Method method = methods[i];
			Route mapping = method.getAnnotation(Route.class);
			//route method
			if (null != mapping) {
				// build multiple route
				HttpMethod methodType = mapping.method();
				String[] paths = mapping.value();
				if(null != paths && paths.length > 0){
					for(int j=0, plen = paths.length; j<plen; j++){
						String pathV = getRoutePath(paths[i], nameSpace, suffix);
						this.buildRoute(router, method, pathV, methodType);
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