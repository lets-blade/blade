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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.blade.http.HttpMethod;
import com.blade.http.Path;

/**
 * 
 * <p>
 * 路由匹配器默认实现
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class RouteMatcher {

//	private static final Logger LOGGER = Logger.getLogger(SampleRouteMatcher.class);
    
    // 存储所有路由
    private List<Route> routes;
    
    private List<Route> interceptors;
    
    public RouteMatcher(Routers router) {
    	this.routes = router.getRoutes();
    	this.interceptors = router.getInterceptors();
    }
    
    /**
     * 查找一个路由
     * @param httpMethod	http请求方法
     * @param path			请求路径
     * @return				返回路由对象
     */
    public Route getRoute(String httpMethod, String path) {
		String cleanPath = parsePath(path);
		List<Route> matchRoutes = new ArrayList<Route>();
		for (Route route : this.routes) {
			if (matchesPath(route.getPath(), cleanPath)) {
				if (route.getHttpMethod() == HttpMethod.ALL
						|| HttpMethod.valueOf(httpMethod) == route.getHttpMethod()) {
					matchRoutes.add(route);
				}
			}
		}
		// 优先匹配原则
        giveMatch(path, matchRoutes);
        
        return matchRoutes.size() > 0 ? matchRoutes.get(0) : null;
	}
    
    /**
     * 查找所有前置拦截器
     * @param path	请求路径
     * @return		返回前置拦截器列表
     */
    public List<Route> getBefore(String path) {
		List<Route> befores = new ArrayList<Route>();
		String cleanPath = parsePath(path);
		for (Route route : this.interceptors) {
			if(matchesPath(route.getPath(), cleanPath) && route.getHttpMethod() == HttpMethod.BEFORE){
				befores.add(route);
			}
        }
		giveMatch(path, befores);
		return befores;
	}
	
    /**
     * 查找所有后置拦截器
     * @param path	请求路径
     * @return		返回后置拦截器列表
     */
	public List<Route> getAfter(String path) {
		List<Route> afters = new ArrayList<Route>();
		String cleanPath = parsePath(path);
		for (Route route : interceptors) {
			if(matchesPath(route.getPath(), cleanPath) && route.getHttpMethod() == HttpMethod.AFTER){
				afters.add(route);
			}
        }
		giveMatch(path, afters);
		return afters;
	}
    
	/**
	 * 对path进行排序
	 * @param uri		请求uri	
	 * @param routes	路由列表
	 */
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
    
    /**
     * 匹配路径
     * @param routePath		路由路径
     * @param pathToMatch	要匹配的路径
     * @return				返回是否匹配成功
     */
    private boolean matchesPath(String routePath, String pathToMatch) {
		routePath = routePath.replaceAll(Path.VAR_REGEXP, Path.VAR_REPLACE);
		return pathToMatch.matches("(?i)" + routePath);
	}
    
    /**
     * 解析路径
     * @param path		路径地址
     * @return			返回解析后的路径
     */
    private String parsePath(String path) {
		path = Path.fixPath(path);
		try {
			URI uri = new URI(path);
			return uri.getPath();
		} catch (URISyntaxException e) {
			return null;
		}
	}

}
