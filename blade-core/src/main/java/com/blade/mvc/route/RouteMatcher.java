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

import com.blade.kit.CollectionKit;
import com.blade.kit.StringKit;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Path;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Default Route Matcher
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.5
 */
public class RouteMatcher {

//	private static final Logger LOGGER = Logger.getLogger(SampleRouteMatcher.class);
    
    // Storage URL and route
	private Map<String, Route> routes = null;
	private Map<String, Route> interceptors = null;
	
	// Storage Map Key
	private Set<String> routeKeys = null;

	private List<Route> interceptorRoutes = CollectionKit.newArrayList(8);
	
    public RouteMatcher(Routers routers) {
		this.routes = routers.getRoutes();
		this.interceptors = routers.getInterceptors();
		this.routeKeys = routes.keySet();
		Collection<Route> inters = interceptors.values();
		if (!inters.isEmpty()) {
			this.interceptorRoutes.addAll(inters);
		}
    }
    
    /**
     * Find a route 
     * @param httpMethod	httpMethod
     * @param path			request path
     * @return				return route object
     */
    public Route getRoute(String httpMethod, String path) {
		String cleanPath = parsePath(path);
		
		String routeKey = path + '#' + httpMethod.toUpperCase();
		final Route[] route = {routes.get(routeKey)};
		if(null != route[0]){
			return route[0];
		}
		route[0] = routes.get(path + "#ALL");
		if(null != route[0]){
			return route[0];
		}
		
		List<Route> matchRoutes = CollectionKit.newArrayList();

		routeKeys.forEach(key -> {
			String[] keyArr =  StringKit.split(key, '#');
			HttpMethod routeMethod = HttpMethod.valueOf(keyArr[1]);
			if (matchesPath(keyArr[0], cleanPath)) {
				if (routeMethod == HttpMethod.ALL || HttpMethod.valueOf(httpMethod) == routeMethod) {
					route[0] = routes.get(key);
					matchRoutes.add(route[0]);
				}
			}
		});

		// Priority matching principle 
        this.giveMatch(path, matchRoutes);
        
        return matchRoutes.isEmpty() ? null : matchRoutes.get(0);
	}
    
    /**
     * Find all in before of the interceptor 
     * @param path	request path
     * @return		return interceptor list
     */
    public List<Route> getBefore(String path) {
		List<Route> befores = CollectionKit.newArrayList();
		String cleanPath = parsePath(path);
		interceptorRoutes.forEach(route -> {
			if(matchesPath(route.getPath(), cleanPath) && route.getHttpMethod() == HttpMethod.BEFORE){
				befores.add(route);
			}
		});
		this.giveMatch(path, befores);
		return befores;
	}
	
    /**
     * Find all in after of the interceptor 
     * @param path	request path
     * @return		return interceptor list
     */
	public List<Route> getAfter(String path) {
		List<Route> afters = CollectionKit.newArrayList();
		String cleanPath = parsePath(path);
		interceptorRoutes.forEach(route -> {
			if(matchesPath(route.getPath(), cleanPath) && route.getHttpMethod() == HttpMethod.AFTER){
				afters.add(route);
			}
		});
		this.giveMatch(path, afters);
		return afters;
	}
    
	/**
	 * Sort of path 
	 * @param uri		request uri	
	 * @param routes	route list
	 */
    private void giveMatch(final String uri, List<Route> routes) {
		Collections.sort(routes, (o1, o2) -> {
			if(o2.getPath().equals(uri)){
				return o2.getPath().indexOf(uri);
			}
			return -1;
		});
	}
    
    /**
     * Matching path
     * 
     * @param routePath		route path
     * @param pathToMatch	match path
     * @return				return match is success
     */
    private boolean matchesPath(String routePath, String pathToMatch) {
		routePath = routePath.replaceAll(Path.VAR_REGEXP, Path.VAR_REPLACE);
		return pathToMatch.matches("(?i)" + routePath);
	}
    
    /**
     * Parse Path
     * 
     * @param path		route path
     * @return			return parsed path
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
