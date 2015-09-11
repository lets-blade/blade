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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import blade.Blade;
import blade.kit.CollectionKit;
import blade.kit.log.Logger;

/**
 * 默认的路由匹配器
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class DefaultRouteMatcher {

    private static final Logger LOGGER = Logger.getLogger(DefaultRouteMatcher.class);
    
    private static final DefaultRouteMatcher DEFAULT_ROUTE_MATCHER = new DefaultRouteMatcher();
    
    // 存储所有路由
    private List<RouteMatcher> routes;
    
    // 存储所有拦截器
    private List<RouteMatcher> interceptors;
    
    private DefaultRouteMatcher() {
        routes = CollectionKit.newArrayList();
        interceptors = CollectionKit.newArrayList();
    }
    
    public static DefaultRouteMatcher instance(){
    	return DEFAULT_ROUTE_MATCHER;
    }
    
    /**
     * 查询是否有路由
     * 
     * @param httpMethod	http请求方法，GET/POST
     * @param uri			请求路径
     * @param acceptType	请求的acceptType
     * @return				返回一个路由匹配对象
     */
    public RouteMatcher findRoute(HttpMethod httpMethod, String uri) {
    	
    	uri = (uri.length() > 1 && uri.endsWith("/")) ? uri.substring(0, uri.length() - 1) : uri;
    	
        List<RouteMatcher> routeEntries = this.findRouteMatcher(httpMethod, uri);
        
        // 优先匹配原则
        giveMatch(uri, routeEntries);
        
        RouteMatcher entry =  routeEntries.size() > 0 ? routeEntries.get(0) : null;
        
        return entry != null ? new RouteMatcher(entry.router, entry.target, entry.execMethod, entry.httpMethod, entry.path, uri) : null;
    }
    
    private void giveMatch(final String uri, List<RouteMatcher> routeEntries) {
		Collections.sort(routeEntries, new Comparator<RouteMatcher>() {
		    @Override
		    public int compare(RouteMatcher o1, RouteMatcher o2) {
				if(o2.path.equals(uri)){
					return o2.path.indexOf(uri);
				}
				return -1;
			}
		});
	}

	/**
     * 查询一个路由集合
     * 
     * @param httpMethod	http请求方法，GET/POST
     * @param path			请求路径
     * @param acceptType	请求的acceptType
     * @return				返回一个路由匹配对象集合
     */
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

    /**
     * 清空路由集合
     */
    public void clearRoutes() {
        routes.clear();
    }
    
    /**
     * 添加一个路由对象
     * 
     * @param target		路由目标执行的class
     * @param execMethod	路由执行方法
     * @param url			路由url
     * @param method		路由http方法
     * @param acceptType	路由acceptType
     */
    public void addRoute(Class<?> target, Method execMethod, String url, HttpMethod method) {
    	RouteMatcher entry = new RouteMatcher();
        entry.target = target;
        entry.execMethod = execMethod;
        entry.httpMethod = method;
        entry.path = url;
        
        if(Blade.debug()){
        	LOGGER.debug("Add Route：" + entry);
        }
        
        // 添加到路由集合
        routes.add(entry);
    }
    
    /**
     * 添加一个拦截器对象
     * 
     * @param target		路由目标执行的class
     * @param execMethod	路由执行方法
     * @param url			路由url
     * @param method		路由http方法
     * @param acceptType	路由acceptType
     */
    public void addInterceptor(Class<?> target, Method execMethod, String url, HttpMethod method) {
    	RouteMatcher entry = new RouteMatcher();
        entry.target = target;
        entry.execMethod = execMethod;
        entry.httpMethod = method;
        entry.path = url;
        
        if(Blade.debug()){
        	LOGGER.debug("Add Interceptor：" + entry);
        }
        
        // 添加到路由集合
        interceptors.add(entry);
    }
    
    /**
     * 添加一个路由对象
     * 
     * @param router		执行的匿名类
     * @param url			路由url
     * @param method		路由http方法
     * @param acceptType	路由acceptType
     */
    public void addRoute(Router router, String url, HttpMethod method, String acceptType) {
    	RouteMatcher entry = new RouteMatcher();
        entry.router = router;
        entry.httpMethod = method;
        entry.path = url;
        
        if(Blade.debug()){
        	LOGGER.debug("Add Route：" + entry);
        }
        
        // 添加到路由集合
        routes.add(entry);
    }
    
    /**
     * 添加一个拦截器对象
     * 
     * @param router		执行的匿名类
     * @param url			路由url
     * @param method		路由http方法
     * @param acceptType	路由acceptType
     */
    public void addInterceptor(Router router, String url, HttpMethod method, String acceptType) {
    	RouteMatcher entry = new RouteMatcher();
        entry.router = router;
        entry.httpMethod = method;
        entry.path = url;
        
        if(Blade.debug()){
        	LOGGER.debug("Add Interceptor：" + entry);
        }
        
        // 添加到路由集合
        interceptors.add(entry);
    }
    
    /**
     * 查找所有匹配HttpMethod和path的路由
     * 
     * @param httpMethod		http方法
     * @param path				路由路径
     * @return					返回匹配的所有路由集合
     */
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

    /**
     * 查找所有匹配HttpMethod和path的路由
     * 
     * @param httpMethod		http方法
     * @param path				路由路径
     * @return					返回匹配的所有路由集合
     */
    private List<RouteMatcher> searchInterceptor(HttpMethod httpMethod, String path) {
        List<RouteMatcher> matchSet = CollectionKit.newArrayList();
        for (RouteMatcher entry : interceptors) {
            if (entry.matches(httpMethod, path)) {
                matchSet.add(entry);
            }
        }
        return matchSet;
    }
    
    
}
