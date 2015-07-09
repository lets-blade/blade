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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blade.Blade;
import blade.kit.MimeParse;
import blade.kit.StringKit;
import blade.kit.log.Logger;

/**
 * 默认的路由匹配器
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class DefaultRouteMatcher {

    private static final Logger LOGGER = Logger.getLogger(DefaultRouteMatcher.class);
    
    // 存储所有路由
    private List<RouteMatcher> routes;
    // 存储所有拦截器
    private List<RouteMatcher> interceptors;
    
    public DefaultRouteMatcher() {
        routes = new ArrayList<RouteMatcher>();
        interceptors = new ArrayList<RouteMatcher>();
    }

    /**
     * 查询是否有路由
     * 
     * @param httpMethod	http请求方法，GET/POST
     * @param uri			请求路径
     * @param acceptType	请求的acceptType
     * @return				返回一个路由匹配对象
     */
    public RouteMatcher findRouteMatcher(HttpMethod httpMethod, String uri, String acceptType) {
    	
    	uri = uri.length() > 1 && uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
    	
        List<RouteMatcher> routeEntries = this.findRouteMatcher(httpMethod, uri);
        
        RouteMatcher entry = findTargetWithGivenAcceptType(routeEntries, acceptType);
        
        return entry != null ? new RouteMatcher(entry.target, entry.execMethod, entry.httpMethod, entry.path, uri, acceptType) : null;
    }
    
    /**
     * 查询一个路由集合
     * 
     * @param httpMethod	http请求方法，GET/POST
     * @param path			请求路径
     * @param acceptType	请求的acceptType
     * @return				返回一个路由匹配对象集合
     */
    public List<RouteMatcher> findInterceptor(HttpMethod httpMethod, String uri, String acceptType) {
    	if(uri.length() > 1){
    		uri = uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
    	}
        List<RouteMatcher> matchSet = new ArrayList<RouteMatcher>();
        List<RouteMatcher> routeEntries = this.findInterceptor(httpMethod, uri);

        for (RouteMatcher routeEntry : routeEntries) {
            if (acceptType != null) {
            	
                String bestMatch = MimeParse.bestMatch(Arrays.asList(routeEntry.acceptType), acceptType);

                if (routeWithGivenAcceptType(bestMatch)) {
                	matchSet.add(routeEntry);
                }
            } else {
            	matchSet.add(routeEntry);
            }
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
     * 移除一个路由
     * 
     * @param path			移除路由的路径
     * @param httpMethod	移除路由的方法
     * @return				true:移除成功，false:移除失败
     */	
    public boolean removeRoute(String path, String httpMethod) {
        if (StringKit.isEmpty(path)) {
            throw new IllegalArgumentException("path cannot be null or blank");
        }

        if (StringKit.isEmpty(httpMethod)) {
            throw new IllegalArgumentException("httpMethod cannot be null or blank");
        }
        
        HttpMethod method = HttpMethod.valueOf(httpMethod);

        return removeRoute(method, path);
    }
    
    public boolean removeRoute(String path) {
        if (StringKit.isEmpty(path)) {
            throw new IllegalArgumentException("path cannot be null or blank");
        }

        return removeRoute((HttpMethod)null, path);
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
    public void addRoute(Class<?> target, Method execMethod, String url, HttpMethod method, String acceptType) {
    	RouteMatcher entry = new RouteMatcher();
        entry.target = target;
        entry.execMethod = execMethod;
        entry.httpMethod = method;
        entry.path = url;
        entry.requestURI = url;
        entry.acceptType = acceptType;
        
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
    public void addInterceptor(Class<?> target, Method execMethod, String url, HttpMethod method, String acceptType) {
    	RouteMatcher entry = new RouteMatcher();
        entry.target = target;
        entry.execMethod = execMethod;
        entry.httpMethod = method;
        entry.path = url;
        entry.requestURI = url;
        entry.acceptType = acceptType;
        
        if(Blade.debug()){
        	LOGGER.debug("Add Interceptor：" + entry);
        }
        
        // 添加到路由集合
        interceptors.add(entry);
    }
    
    private Map<String, RouteMatcher> getAcceptedMimeTypes(List<RouteMatcher> routes) {
        Map<String, RouteMatcher> acceptedTypes = new HashMap<String, RouteMatcher>();

        for (RouteMatcher routeEntry : routes) {
            if (!acceptedTypes.containsKey(routeEntry.acceptType)) {
                acceptedTypes.put(routeEntry.acceptType, routeEntry);
            }
        }

        return acceptedTypes;
    }

    private boolean routeWithGivenAcceptType(String bestMatch) {
        return !MimeParse.NO_MIME_TYPE.equals(bestMatch);
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
    	
        List<RouteMatcher> matchSet = new ArrayList<RouteMatcher>();
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
    private List<RouteMatcher> findInterceptor(HttpMethod httpMethod, String path) {
        List<RouteMatcher> matchSet = new ArrayList<RouteMatcher>();
        for (RouteMatcher entry : interceptors) {
            if (entry.matches(httpMethod, path)) {
                matchSet.add(entry);
            }
        }
        return matchSet;
    }
    
    /**
     * 查找符合请求头的路由
     * @param routeMatches
     * @param acceptType
     * @return
     */
    private RouteMatcher findTargetWithGivenAcceptType(List<RouteMatcher> routeMatches, String acceptType) {
        if (acceptType != null && routeMatches.size() > 0) {
            Map<String, RouteMatcher> acceptedMimeTypes = getAcceptedMimeTypes(routeMatches);
            String bestMatch = MimeParse.bestMatch(acceptedMimeTypes.keySet(), acceptType);

            if (routeWithGivenAcceptType(bestMatch)) {
                return acceptedMimeTypes.get(bestMatch);
            } else {
                return null;
            }
        } else {
            if (routeMatches.size() > 0) {
                return routeMatches.get(0);
            }
        }

        return null;
    }

    private boolean removeRoute(HttpMethod httpMethod, String path) {
        List<RouteMatcher> forRemoval = new ArrayList<RouteMatcher>();

        for (RouteMatcher routeEntry : routes) {
            HttpMethod httpMethodToMatch = httpMethod;

            if (httpMethod == null) {
                // Use the routeEntry's HTTP method if none was given, so that only path is used to match.
                httpMethodToMatch = routeEntry.httpMethod;
            }

            if (routeEntry.matches(httpMethodToMatch, path)) {
            	
            	if(Blade.debug()){
            		LOGGER.debug("Removing path {}", path, httpMethod == null ? "" : " with HTTP method " + httpMethod);
                }
            	
                forRemoval.add(routeEntry);
            }
        }

        return routes.removeAll(forRemoval);
    }
}
