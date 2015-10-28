package com.blade.route;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.blade.http.HttpMethod;
import com.blade.http.Path;

import blade.kit.PathKit;

public class SampleRouteMatcher {

//	private static final Logger LOGGER = Logger.getLogger(SampleRouteMatcher.class);
    
    // 存储所有路由
    private List<Route> routes;
    
    private List<Route> interceptors;
    
    public SampleRouteMatcher(Router router) {
    	this.routes = router.getRoutes();
    	this.interceptors = router.getInterceptors();
    }
    
    public Route getRoute(String httpMethod, String path) {
		String cleanPath = parsePath(path);
		List<Route> matchRoutes = new ArrayList<Route>();
		for (Route route : this.routes) {
			if (matchesPath(route.getPath(), cleanPath) && route.getHttpMethod().toString().equalsIgnoreCase(httpMethod)) {
				matchRoutes.add(route);
			}
		}
		// 优先匹配原则
        giveMatch(path, matchRoutes);
        
        return matchRoutes.size() > 0 ? matchRoutes.get(0) : null;
	}
    
    private void giveMatch(final String uri, List<Route> routes) {
		Collections.sort(routes, new Comparator<Route>() {
		    @Override
		    public int compare(Route o1, Route o2) {
				if(o2.getPath().equals(uri)){
					return o2.getPath().indexOf(uri);
				}
				return -1;
			}
		});
	}
    
    private boolean matchesPath(String routePath, String pathToMatch) {
		routePath = routePath.replaceAll(Path.VAR_REGEXP, Path.VAR_REPLACE);
		return pathToMatch.matches("(?i)" + routePath);
	}
    
    private String parsePath(String path) {
		path = Path.fixPath(path);

		try {
			URI uri = new URI(path);
			return uri.getPath();
		} catch (URISyntaxException e) {
			return null;
		}
	}

	public List<Route> getBefore(String path) {
		List<Route> befores = new ArrayList<Route>();
		String cleanPath = parsePath(path);
		for (Route route : this.interceptors) {
            if (matches(route, HttpMethod.BEFORE, cleanPath)) {
                befores.add(route);
            }
        }
		return befores;
	}
	
	public List<Route> getAfter(String path) {
		List<Route> afters = new ArrayList<Route>();
		String cleanPath = parsePath(path);
		for (Route route : interceptors) {
            if (matches(route, HttpMethod.AFTER, cleanPath)) {
            	afters.add(route);
            }
        }
		return afters;
	}
	
	public boolean matches(Route route, HttpMethod httpMethod, String path) {
		
    	// 如果是拦截器的全部匹配模式则跳过，返回true
        if ((httpMethod == HttpMethod.BEFORE || httpMethod == HttpMethod.AFTER)
                && (route.getHttpMethod() == httpMethod)
                && route.getPath().equals(PathKit.ALL_PATHS)) {
            return true;
        }
        
        boolean match = false;
        if (route.getHttpMethod() == HttpMethod.ALL || route.getHttpMethod() == httpMethod) {
            match = matchPath(route.getPath(), path);
        }
        
        return match;
    }
	
	/**
	 * 继续匹配
	 * 
	 * @param uri
	 * @return
	 */
    private boolean matchPath(String path, String uri) {
    	
    	// /hello
        if (!path.endsWith("*") && ((uri.endsWith("/") && !path.endsWith("/"))
                || (path.endsWith("/") && !uri.endsWith("/")))) {
            return false;
        }
        
        if (path.equals(uri)) {
            return true;
        }

        // 检查参数
        List<String> thisPathList = PathKit.convertRouteToList(path);
        List<String> uriList = PathKit.convertRouteToList(uri);

        int thisPathSize = thisPathList.size();
        int uriSize = uriList.size();

        if (thisPathSize == uriSize) {
            for (int i = 0; i < thisPathSize; i++) {
                String thisPathPart = thisPathList.get(i);
                String pathPart = uriList.get(i);

                if ((i == thisPathSize - 1) && (thisPathPart.equals("*") && path.endsWith("*"))) {
                    // 通配符匹配
                    return true;
                }

                if ((!thisPathPart.startsWith(":"))
                        && !thisPathPart.equals(pathPart)
                        && !thisPathPart.equals("*")) {
                    return false;
                }
            }
            // 全部匹配
            return true;
        } else {
            if (path.endsWith("*")) {
                if (uriSize == (thisPathSize - 1) && (path.endsWith("/"))) {
                	uriList.add("");
                	uriList.add("");
                	uriSize += 2;
                }

                if (thisPathSize < uriSize) {
                    for (int i = 0; i < thisPathSize; i++) {
                        String thisPathPart = thisPathList.get(i);
                        String pathPart = uriList.get(i);
                        if (thisPathPart.equals("*") && (i == thisPathSize - 1) && path.endsWith("*")) {
                            return true;
                        }
                        if (!thisPathPart.startsWith(":")
                                && !thisPathPart.equals(pathPart)
                                && !thisPathPart.equals("*")) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 查询是否有路由
     * 
     * @param httpMethod	http请求方法，GET/POST
     * @param uri			请求路径
     * @return				返回一个路由匹配对象
     *//*
    public Route findRoute(HttpMethod httpMethod, String uri) {
    	
    	uri = (uri.length() > 1 && uri.endsWith("/")) ? uri.substring(0, uri.length() - 1) : uri;
    	
        List<RouteMatcher> routeEntries = this.findRouteMatcher(httpMethod, uri);
        
        // 优先匹配原则
        giveMatch(uri, routeEntries);
        
        RouteMatcher entry =  routeEntries.size() > 0 ? routeEntries.get(0) : null;
        
        return entry != null ? new RouteMatcher(entry.getRouterHandler(), entry.getTarget(), entry.getExecMethod(), entry.getHttpMethod(), entry.getPath(), uri) : null;
    }
    
    private void giveMatch(final String uri, List<RouteMatcher> routeEntries) {
		Collections.sort(routeEntries, new Comparator<RouteMatcher>() {
		    @Override
		    public int compare(RouteMatcher o1, RouteMatcher o2) {
				if(o2.getPath().equals(uri)){
					return o2.getPath().indexOf(uri);
				}
				return -1;
			}
		});
	}

	*//**
     * 查询一个路由集合
     * 
     * @param httpMethod	http请求方法，GET/POST
     * @param path			请求路径
     * @return				返回一个路由匹配对象集合
     *//*
    public List<RouteMatcher> findInterceptor(HttpMethod httpMethod, String uri) {
    	if(uri.length() > 1){
    		uri = uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
    	}
        List<RouteMatcher> matchSet = CollectionKit.newArrayList();
        List<RouteMatcher> routeEntries = this.searchInterceptor(httpMethod, uri);

        for (RouteMatcher routeEntry : routeEntries) {
        	matchSet.add(routeEntry);
        }

        return matchSet;
    }

    *//**
     * 清空路由集合
     *//*
    public void clearRoutes() {
        routes.clear();
    }
    
    *//**
     * 添加一个路由对象
     * 
     * @param target		路由目标执行的class
     * @param execMethod	路由执行方法
     * @param url			路由url
     * @param method		路由http方法
     *//*
    public void addRoute(Class<?> target, Method execMethod, String url, HttpMethod httpMethod) {
    	RouteMatcher routeMatcher = new RouteMatcher();
    	routeMatcher.setTarget(target);
    	routeMatcher.setExecMethod(execMethod);
    	routeMatcher.setHttpMethod(httpMethod);
    	routeMatcher.setPath(url);
        
    	LOGGER.debug("Add Route：" + routeMatcher);
        
        // 添加到路由集合
        routes.add(routeMatcher);
    }
    
    *//**
     * 添加一个拦截器对象
     * 
     * @param target		路由目标执行的class
     * @param execMethod	路由执行方法
     * @param url			路由url
     * @param method		路由http方法
     *//*
    public void addInterceptor(Class<?> target, Method execMethod, String url, HttpMethod httpMethod) {
        RouteMatcher routeMatcher = new RouteMatcher();
    	routeMatcher.setTarget(target);
    	routeMatcher.setExecMethod(execMethod);
    	routeMatcher.setHttpMethod(httpMethod);
    	routeMatcher.setPath(url);
        
    	LOGGER.debug("Add Interceptor：" + routeMatcher);
        
        // 添加到路由集合
        interceptors.add(routeMatcher);
    }
    
    *//**
     * 添加一个路由对象
     * 
     * @param router		执行的匿名类
     * @param url			路由url
     * @param method		路由http方法
     *//*
    public void addRoute(RouteHandler routerHandler, String url, HttpMethod httpMethod) {
        
        RouteMatcher routeMatcher = new RouteMatcher();
    	routeMatcher.setRouterHandler(routerHandler);
    	routeMatcher.setHttpMethod(httpMethod);
    	routeMatcher.setPath(url);
        
    	LOGGER.debug("Add Route：" + routeMatcher);
        
        // 添加到路由集合
        routes.add(routeMatcher);
    }
    
    *//**
     * 添加一个拦截器对象
     * 
     * @param router		执行的匿名类
     * @param url			路由url
     * @param method		路由http方法
     *//*
    public void addInterceptor(RouteHandler routerHandler, String url, HttpMethod httpMethod) {
        
        RouteMatcher routeMatcher = new RouteMatcher();
    	routeMatcher.setRouterHandler(routerHandler);
    	routeMatcher.setHttpMethod(httpMethod);
    	routeMatcher.setPath(url);
        
    	LOGGER.debug("Add Interceptor：" + routeMatcher);
        
        // 添加到路由集合
        interceptors.add(routeMatcher);
    }
    
    *//**
     * 查找所有匹配HttpMethod和path的路由
     * 
     * @param httpMethod		http方法
     * @param path				路由路径
     * @return					返回匹配的所有路由集合
     *//*
    private List<RouteMatcher> findRouteMatcher(HttpMethod httpMethod, String path) {
    	path = path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    	
        List<RouteMatcher> matchSet = CollectionKit.newArrayList();
        for (RouteMatcher entry : routes) {
            if (entry.matches(httpMethod, path)) {
                matchSet.add(entry);
            }
        }
        return matchSet;
    }

    *//**
     * 查找所有匹配HttpMethod和path的路由
     * 
     * @param httpMethod		http方法
     * @param path				路由路径
     * @return					返回匹配的所有路由集合
     *//*
    private List<RouteMatcher> searchInterceptor(HttpMethod httpMethod, String path) {
        List<RouteMatcher> matchSet = CollectionKit.newArrayList();
        for (RouteMatcher entry : interceptors) {
            if (entry.matches(httpMethod, path)) {
                matchSet.add(entry);
            }
        }
        return matchSet;
    }
    */
}
