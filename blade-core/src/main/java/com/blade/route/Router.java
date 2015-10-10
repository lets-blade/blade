package com.blade.route;

import com.blade.IocApplication;

public class Router {
	
	public void load(Class<? extends RouteBase> route){
		IocApplication.addRouteClass(route);
	}
	
	/**
	 * 注册一个函数式的路由</br>
	 * <p>
	 * 方法上指定请求类型，如：post:signin
	 * </p>
	 * @param path			路由url	
	 * @param clazz			路由处理类
	 * @param methodName	路由处理方法名称
	 */
	public void register(String path, Class<?> clazz, String methodName){
		RouteMatcherBuilder.buildFunctional(path, clazz, methodName, null);
	}
	
	/**
	 * 注册一个函数式的路由
	 * @param path			路由url	
	 * @param clazz			路由处理类
	 * @param methodName	路由处理方法名称
	 * @param httpMethod	请求类型,GET/POST
	 */
	public static synchronized void register(String path, Class<?> clazz, String methodName, HttpMethod httpMethod){
		RouteMatcherBuilder.buildFunctional(path, clazz, methodName, httpMethod);
	}
	
	/**
	 * get请求
	 * @param path
	 * @param routeHandler
	 */
	public void get(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.GET);
	}
	
	/**
	 * get请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor get(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.GET);
		}
		return null;
	}
	
	/**
	 * post请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void post(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.POST);
	}
	
	/**
	 * post请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor post(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.POST);
		}
		return null;
	}
	
	/**
	 * delete请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void delete(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.DELETE);
	}
	
	/**
	 * delete请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor delete(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.DELETE);
		}
		return null;
	}
	
	/**
	 * put请求
	 * @param paths
	 */
	public static synchronized void put(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.PUT);
	}
	
	/**
	 * put请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor put(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.PUT);
		}
		return null;
	}
	
	/**
	 * patch请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void patch(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.PATCH);
	}

	/**
	 * patch请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor patch(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.PATCH);
		}
		return null;
	}
	
	/**
	 * head请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void head(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.HEAD);
	}
	
	/**
	 * head请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor head(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.HEAD);
		}
		return null;
	}
	
	/**
	 * trace请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void trace(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.TRACE);
	}
	
	/**
	 * trace请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor trace(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.TRACE);
		}
		return null;
	}
	
	/**
	 * options请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void options(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.OPTIONS);
	}
	
	/**
	 * options请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor options(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.OPTIONS);
		}
		return null;
	}
	
	/**
	 * connect请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void connect(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.CONNECT);
	}
	
	/**
	 * connect请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor connect(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.CONNECT);
		}
		return null;
	}
	
	/**
	 * 任意请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void all(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.ALL);
	}
	
	/**
	 * all请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor all(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.ALL);
		}
		return null;
	}
	
	/**
	 * 拦截器before请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void before(String path, RouteHandler routeHandler){
		RouteMatcherBuilder.buildInterceptor(path, routeHandler, HttpMethod.BEFORE);
	}

	/**
	 * before请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor before(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.BEFORE);
		}
		return null;
	}
	
	/**
	 * 拦截器after请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void after(String path, RouteHandler routeHandler){
		RouteMatcherBuilder.buildInterceptor(path, routeHandler, HttpMethod.AFTER);
	}
	
	/**
	 * after请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor after(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.AFTER);
		}
		return null;
	}
	
}
