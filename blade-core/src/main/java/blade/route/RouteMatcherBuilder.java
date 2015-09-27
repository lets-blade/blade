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
import blade.exception.BladeException;
import blade.ioc.Container;
import blade.ioc.DefaultContainer;
import blade.kit.ReflectKit;
import blade.kit.StringKit;
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

    private static DefaultRouteMatcher defaultRouteMatcher = DefaultRouteMatcher.instance();
    
    /**
	 * 默认路由后缀包，用户扫描路由所在位置，默认为route，用户可自定义
	 */
    private final static String PACKAGE_ROUTE = "route";
	
	/**
	 * 默认拦截器后缀包，用户扫描拦截器所在位置，默认为interceptor，用户可自定义
	 */
    private final static String PACKAGE_INTERCEPTOR = "interceptor";
	
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
    public static synchronized void building() {
    	
        String basePackage = Blade.basePackage();
        
        if(StringKit.isNotBlank(basePackage)){
        	
        	// 处理如：com.xxx.* 表示递归扫描包
        	String suffix = basePackage.endsWith(".*") ? ".*" : "";
        	basePackage = basePackage.endsWith(".*") ? basePackage.substring(0, basePackage.length() - 2) : basePackage;
        	
			String routePackage = basePackage + "." + PACKAGE_ROUTE + suffix;
			String interceptorPackage = basePackage + "." + PACKAGE_INTERCEPTOR + suffix;
			
        	buildRoute(routePackage);
        	
        	buildInterceptor(interceptorPackage);
        	
        } else {
        	
        	// 路由
	    	String[] routePackages = Blade.routes();
	    	if(null != routePackages && routePackages.length > 0){
	    		buildRoute(routePackages);
	    	}
	    	
			// 拦截器
	    	String interceptorPackage = Blade.interceptor();
	    	if(StringKit.isNotBlank(interceptorPackage)){
	    		buildInterceptor(interceptorPackage);
	    	}
		}
    }
    
    /**
     * 函数式路由构建
     */
    public static void buildFunctional(String path, Class<?> clazz, String methodName, HttpMethod httpMethod){
    	if(StringKit.isNotBlank(path) && null != clazz && StringKit.isNotBlank(methodName)){
    		
    		// 字符串上写请求   get:hello
    		if(methodName.indexOf(":") != -1){
    			String[] methodArr = StringKit.split(methodName, ":");
    			httpMethod = getHttpMethod(methodArr[0]);
    			methodName = methodArr[1];
    		}
    		
    		if(null == httpMethod){
    			httpMethod = HttpMethod.ALL;
    		}
    		
    		// 查找
    		Object target = container.getBean(clazz, null);
    		if(null == target){
    			container.registBean(clazz);
    		}
    		
    		Method execMethod = ReflectKit.getMethodByName(clazz, methodName);
    		
    		// 拦截器
    		if(httpMethod == HttpMethod.AFTER || httpMethod == HttpMethod.BEFORE){
    			defaultRouteMatcher.addInterceptor(clazz, execMethod, path, httpMethod);
    		} else {
    			defaultRouteMatcher.addRoute(clazz, execMethod, path, httpMethod);
			}
    	} else {
			 throw new BladeException("an unqualified configuration");
		}
    }
    
    /**
     * Handler路由构建
     */
    public static void buildHandler(String path, Router router, HttpMethod httpMethod){
    	if(StringKit.isNotBlank(path) && null != router){
    		defaultRouteMatcher.addRoute(router, path, httpMethod);
    	} else {
			 throw new BladeException("an unqualified configuration");
		}
    }
    
    /**
     * 函数式构建拦截器
     */
    public static void buildInterceptor(String path, Router router, HttpMethod httpMethod){
    	if(StringKit.isNotBlank(path) && null != router){
    		defaultRouteMatcher.addInterceptor(router, path, httpMethod);
    	} else {
			 throw new BladeException("an unqualified configuration");
		}
    }
    
    /**
     * 函数式拦截器构建
     */
    public static void buildInterceptor(String path, Class<?> clazz, String methodName){
    	if(StringKit.isNotBlank(path) && null != clazz && StringKit.isNotBlank(methodName)){

    		// 字符串上写请求   hello
    		if(methodName.indexOf(":") != -1){
    			String[] methodArr = StringKit.split(methodName, ":");
    			methodName = methodArr[1];
    		}
    		
    		// 查找
    		Object target = container.getBean(clazz, null);
    		if(null == target){
    			container.registBean(clazz);
    		}
    		
    		Method execMethod = ReflectKit.getMethodByName(clazz, methodName);
    		
    		defaultRouteMatcher.addInterceptor(clazz, execMethod, path, HttpMethod.BEFORE);
    	} else {
			 throw new BladeException("an unqualified configuration");
		}
    }
    
    /**
     * 构建拦截器
     * 
     * @param interceptorPackages	要添加的拦截器包
     */
    private static void buildInterceptor(String... interceptorPackages){
    	// 扫描所有的Interceptor
		Set<Class<?>> classes = null;
    	// 拦截器
		for(String packageName : interceptorPackages){
			
			boolean recursive = false;
			
			if (packageName.endsWith(".*")) {
				packageName = packageName.substring(0, packageName.length() - 2);
				recursive = true;
			}
			
    		// 扫描所有的Interceptor
    		classes = classReader.getClassByAnnotation(packageName, Interceptor.class, recursive);
    		
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
    	Set<Class<?>> classes = null;
    	// 路由
		for(String packageName : routePackages){
			
			boolean recursive = false;
			
			if (packageName.endsWith(".*")) {
				packageName = packageName.substring(0, packageName.length() - 2);
				recursive = true;
			}
			
    		// 扫描所有的Controoler
    		classes = classReader.getClassByAnnotation(packageName, Path.class, recursive);
    		
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
     * 解析一个控制器中的所有路由
     * 
     * @param controller		要解析的路由class
     */
    private static void parseRouter(final Class<?> router){
    	
    	Method[] methods = router.getMethods();
    	if(null == methods || methods.length == 0){
    		return;
    	}
    	
    	container.registBean(router);
    	
		final String nameSpace = router.getAnnotation(Path.class).value();
		
		final String suffix = router.getAnnotation(Path.class).suffix();
		
		for (Method method : methods) {
			
			Route mapping = method.getAnnotation(Route.class);
			
			//route方法
			if (null != mapping) {
				
				////构建路由
				String path = getRoutePath(mapping.value(), nameSpace, suffix);
				
				HttpMethod methodType = mapping.method();
				
				buildRoute(router, method, path, methodType);
				
				// 构建多个路由
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
    
    private static String getRoutePath(String value, String nameSpace, String suffix){
    	String path = value.startsWith("/") ? value : "/" + value;
		path = nameSpace + path;
		path = path.replaceAll("[/]+", "/");
		
		path = path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
		
		path = path + suffix;
		
		return path;
    }
    
    /**
     * 构建一个路由
     * 
     * @param target		路由目标执行的class
     * @param execMethod	路由执行方法
     * @param path			路由url
     * @param method		路由http方法
     */
    private static void buildRoute(Class<?> target, Method execMethod, String path, HttpMethod method){
    	defaultRouteMatcher.addRoute(target, execMethod, path, method);
    }
    
    /**
     * 构建一个路由
     * 
     * @param path			路由url
     * @param target		路由目标执行的class
     * @param execMethod	路由执行方法
     * @param method		路由http方法
     */
    private static void buildInterceptor(String path, Class<?> target, Method execMethod, HttpMethod method){
    	defaultRouteMatcher.addInterceptor(target, execMethod, path, method);
    }
    
    private static HttpMethod getHttpMethod(String name){
    	try {
			return Enum.valueOf(HttpMethod.class, name.toUpperCase());
		} catch (Exception e) {
			LOGGER.error("HttpMethod conversion failure", e);
		}
    	return null;
    }
}
