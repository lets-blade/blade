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
package blade.route;

import java.lang.reflect.Method;
import java.util.Set;

import blade.Blade;
import blade.annotation.After;
import blade.annotation.Before;
import blade.annotation.Interceptor;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.ioc.Container;
import blade.ioc.DefaultContainer;
import blade.kit.log.Logger;
import blade.kit.resource.ClassPathClassReader;
import blade.kit.resource.ClassReader;

/**
 * 路由构造器
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class RouteMatcherBuilder {
    
    private static final Logger LOGGER = Logger.getLogger(RouteMatcherBuilder.class);

    private static DefaultRouteMatcher routeMatcher = null;
    
    /**
     * 类读取器,用于在指定规则中扫描类
     */
    private final static ClassReader classReader = new ClassPathClassReader();
    
    /**
     * IOC容器，存储路由到ioc中
     */
    private final static Container container = DefaultContainer.single();
    
    private RouteMatcherBuilder() {
    }
    
    /**
     * 开始构建路由
     * 
     * @return	返回构建路由后的构造器
     */
    public static synchronized DefaultRouteMatcher building() {
    	
        if (routeMatcher != null) {
        	routeMatcher.clearRoutes();
        	routeMatcher = null;
        }
        
        routeMatcher = new DefaultRouteMatcher();
        
        if(Blade.debug()){
        	LOGGER.debug("creates RouteMatcher");
        }
        
        String[] basePackages = Blade.defaultRoutes();
        
        if(null != basePackages && basePackages.length > 0){
        	
        	String basePackage = basePackages[0];
        	
        	// 处理如：com.xxx.* 表示递归扫描包
        	String suffix = basePackage.endsWith(".*") ? ".*" : "";
        	basePackage = basePackage.endsWith(".*") ? basePackage.substring(0, basePackage.length() - 2) : basePackage;
        	
			String routePackage = basePackage + "." + Blade.PACKAGE_ROUTE + suffix;
			String interceptorPackage = basePackage + "." + Blade.PACKAGE_INTERCEPTOR + suffix;
			
        	buildRoute(routePackage);
        	
        	buildInterceptor(interceptorPackage);
        	
        } else {
        	// 路由
	    	String[] routePackages = Blade.routes();
	    	if(null != routePackages && routePackages.length > 0){
	    		buildRoute(routePackages);
	    	}
	    	
			// 拦截器
	    	String[] interceptorPackages = Blade.interceptor();
	    	if(null != interceptorPackages && interceptorPackages.length > 0){
	    		buildInterceptor(interceptorPackages);
	    	}
		}
        return routeMatcher;
    }
    
    /**
     * 构建拦截器
     * 
     * @param interceptorPackages	要添加的拦截器包
     */
    private static void buildInterceptor(String... interceptorPackages){
    	// 拦截器
		for(String packageName : interceptorPackages){
			
			boolean recursive = false;
			
			if (packageName.endsWith(".*")) {
				packageName = packageName.substring(0, packageName.length() - 2);
				recursive = true;
			}
			
    		// 扫描所有的Interceptor
    		Set<Class<?>> classes = classReader.getClassByAnnotation(packageName, Interceptor.class, recursive);
    		
    		if(null != classes && classes.size() > 0){
    			for(Class<?> interceptorClazz : classes){
    				parseInterceptor(interceptorClazz);
    			}
    		}
    	}
    }
    
    /**
     * 构建路由
     * 
     * @param routePackages		要添加的路由包
     */
    private static void buildRoute(String... routePackages){
    	// 路由
		for(String packageName : routePackages){
			
			boolean recursive = false;
			
			if (packageName.endsWith(".*")) {
				packageName = packageName.substring(0, packageName.length() - 2);
				recursive = true;
			}
			
    		// 扫描所有的Controoler
    		Set<Class<?>> classes = classReader.getClassByAnnotation(packageName, Path.class, recursive);
    		
    		if(null != classes && classes.size() > 0){
    			for(Class<?> pathClazz : classes){
    				parseRouter(pathClazz);
    			}
    		}
    	}
    	
    }
    
    /**
     * 解析拦截器
     * 
     * @param interceptor		要解析的拦截器class
     */
    private static void parseInterceptor(final Class<?> interceptor){
    	
    	Method[] methods = interceptor.getMethods();
    	if(null == methods || methods.length == 0){
    		return;
    	}
    	
    	container.registBean(interceptor);
    	
    	for (Method method : methods) {
			
			Before before = method.getAnnotation(Before.class);
			After after = method.getAnnotation(After.class);
			
			if (null != before) {
				
				String path = before.value().startsWith("/") ? before.value() : "/" + before.value();
				
				path = path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
				
				String acceptType = before.acceptType();
				buildInterceptor(interceptor, method, path, HttpMethod.BEFORE, acceptType);
			}
			
			if (null != after) {
				String path = after.value().startsWith("/") ? after.value() : "/" + after.value();
				
				path = path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
				
				String acceptType = after.acceptType();
				buildInterceptor(interceptor, method, path, HttpMethod.AFTER, acceptType);
			}
		}
    }
    
    /**
     * 解析一个控制器中的所有路由
     * 
     * @param controller		要解析的路由class
     */
    private static void parseRouter(final Class<?> router){
    	
    	Method[] methods = router.getMethods();
    	if(null == methods || methods.length == 0){
    		return;
    	}
    	
		final String nameSpace = router.getAnnotation(Path.class).value();
		
		container.registBean(router);
		
		for (Method method : methods) {
			
			Route mapping = method.getAnnotation(Route.class);
			
			//route方法
			if (null != mapping) {
				
				////构建路由
				
				String path = mapping.value().startsWith("/") ? mapping.value() : "/" + mapping.value();
				path = nameSpace + path;
				path = path.replaceAll("[/]+", "/");
				
				path = path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
				
				HttpMethod methodType = mapping.method();
				
				String acceptType = mapping.acceptType();
				
				buildRoute(router, method, path, methodType, acceptType);
			}
		}
    }
    
    /**
     * 构建一个路由
     * 
     * @param target		路由目标执行的class
     * @param execMethod	路由执行方法
     * @param path			路由url
     * @param method		路由http方法
     * @param acceptType	路由acceptType
     */
    private static void buildRoute(Class<?> target, Method execMethod, String path, HttpMethod method, String acceptType){
		routeMatcher.addRoute(target, execMethod, path, method, acceptType);
    }
    
    /**
     * 构建一个路由
     * 
     * @param target		路由目标执行的class
     * @param execMethod	路由执行方法
     * @param path			路由url
     * @param method		路由http方法
     * @param acceptType	路由acceptType
     */
    private static void buildInterceptor(Class<?> target, Method execMethod, String path, HttpMethod method, String acceptType){
		routeMatcher.addInterceptor(target, execMethod, path, method, acceptType);
    }
    
}
