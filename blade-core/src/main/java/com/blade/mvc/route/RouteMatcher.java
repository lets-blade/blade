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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Path;

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
	private List<Route> interceptorRoutes = new ArrayList<Route>();
	
    public RouteMatcher(Routers routers) {
		this.routes = routers.getRoutes();
		this.interceptors = routers.getInterceptors();
		this.routeKeys = routes.keySet();
		Collection<Route> inters = interceptors.values();
		if (null != inters && inters.size() > 0) {
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
		
		String routeKey = path + "#" + httpMethod.toUpperCase();
		Route route = routes.get(routeKey);
		if(null != route){
			return route;
		}
		route = routes.get(path + "#ALL");
		if(null != route){
			return route;
		}
		
		List<Route> matchRoutes = new ArrayList<Route>();
		for(String key : routeKeys){
			String[] keyArr = key.split("#");
			HttpMethod routeMethod = HttpMethod.valueOf(keyArr[1]);
			if (matchesPath(keyArr[0], cleanPath)) {
				if (routeMethod == HttpMethod.ALL || HttpMethod.valueOf(httpMethod) == routeMethod) {
					route = routes.get(key);
					matchRoutes.add(route);
				}
			}
		}
		
		// Priority matching principle 
        giveMatch(path, matchRoutes);
        
        return matchRoutes.size() > 0 ? matchRoutes.get(0) : null;
	}
    
    /**
     * Find all in before of the interceptor 
     * @param path	request path
     * @return		return interceptor list
     */
    public List<Route> getBefore(String path) {
    	
		List<Route> befores = new ArrayList<Route>();
		String cleanPath = parsePath(path);
		for (Route route : interceptorRoutes) {
			if(matchesPath(route.getPath(), cleanPath) && route.getHttpMethod() == HttpMethod.BEFORE){
				befores.add(route);
			}
        }
		giveMatch(path, befores);
		return befores;
	}
	
    /**
     * Find all in after of the interceptor 
     * @param path	request path
     * @return		return interceptor list
     */
	public List<Route> getAfter(String path) {
		List<Route> afters = new ArrayList<Route>();
		String cleanPath = parsePath(path);
		for (Route route : interceptorRoutes) {
			if(matchesPath(route.getPath(), cleanPath) && route.getHttpMethod() == HttpMethod.AFTER){
				afters.add(route);
			}
        }
		giveMatch(path, afters);
		return afters;
	}
    
	/**
	 * Sort of path 
	 * @param uri		request uri	
	 * @param routes	route list
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
