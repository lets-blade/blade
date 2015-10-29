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
		giveMatch(path, befores);
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
		giveMatch(path, afters);
		return afters;
	}
	
	public boolean matches(Route route, HttpMethod httpMethod, String path) {
		
    	// 如果是拦截器的全部匹配模式则跳过，返回true
        if ((httpMethod == HttpMethod.BEFORE || httpMethod == HttpMethod.AFTER)
                && (route.getHttpMethod() == httpMethod)
                && route.getPath().equals(Path.ALL_PATHS)) {
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
        List<String> thisPathList = Path.convertRouteToList(path);
        List<String> uriList = Path.convertRouteToList(uri);

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

}
