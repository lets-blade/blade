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
package com.blade.mvc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Blade;
import com.blade.Const;
import com.blade.context.WebContextHolder;
import com.blade.exception.BladeException;
import com.blade.ioc.Ioc;
import com.blade.kit.DispatchKit;
import com.blade.kit.StringKit;
import com.blade.mvc.http.HttpStatus;
import com.blade.mvc.http.Path;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.http.wrapper.ServletRequest;
import com.blade.mvc.http.wrapper.ServletResponse;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.RouteHandler;
import com.blade.mvc.route.RouteMatcher;
import com.blade.mvc.route.Routers;
import com.blade.mvc.view.ModelAndView;
import com.blade.mvc.view.ViewSettings;
import com.blade.mvc.view.resolve.RouteViewResolve;
import com.blade.mvc.view.template.TemplateException;

/**
 * Synchronous request processor
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.5
 */
public class DispatcherHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherHandler.class);
	
	private Ioc ioc;
	
	private Blade blade;
	
	private ServletContext servletContext;
	
	private RouteMatcher routeMatcher;
	
	private StaticFileFilter staticFileFilter;
	
	private RouteViewResolve routeViewHandler;
	
	public DispatcherHandler(ServletContext servletContext, Routers routers) {
		this.servletContext = servletContext;
		this.blade = Blade.$();
		this.ioc = blade.ioc();
		this.routeMatcher = new RouteMatcher(routers);
		this.staticFileFilter = new StaticFileFilter(blade.applicationConfig().getResources());
		this.routeViewHandler = new RouteViewResolve(this.ioc);
	}
	
	public void handle(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		
		// http method, GET/POST ...
        String method = httpRequest.getMethod();
        
        // reuqest uri
        String uri = Path.getRelativePath(httpRequest.getRequestURI(), servletContext.getContextPath());
        
 		// Create Response
 		Response response = new ServletResponse(httpResponse);;
 		
        // If it is static, the resource is handed over to the filter
    	if(staticFileFilter.isStatic(uri)){
            LOGGER.debug("Request : {}\t{}", method, uri);
    		DispatchKit.printStatic(uri, httpRequest, response);
			return;
    	}
        
        try {
        	
        	LOGGER.info("Request : {}\t{}", method, uri);
        	
        	Request request = new ServletRequest(httpRequest);
         	WebContextHolder.init(servletContext, request, response);
			Route route = routeMatcher.getRoute(method, uri);
			if (null != route) {
				request.setRoute(route);
				
				// before inteceptor
				List<Route> befores = routeMatcher.getBefore(uri);
				boolean result = invokeInterceptor(request, response, befores);
				if(result){
					// execute
					this.routeHandle(request, response, route);
					if(!request.isAbort()){
						// after inteceptor
						List<Route> afters = routeMatcher.getAfter(uri);
						invokeInterceptor(request, response, afters);
					}
				}
			} else {
				// Not found
				render404(response, uri);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			DispatchKit.printError(e, 500, response);
		}
	}
	
	/**
	 * 404 view render
	 * 
	 * @param response	response object
	 * @param uri		404 uri
	 * @throws IOException 
	 * @throws TemplateException 
	 */
	private void render404(Response response, String uri) throws Exception {
		String view404 = ViewSettings.$().getView404();
    	if(StringKit.isNotBlank(view404)){
    		ModelAndView modelAndView = new ModelAndView(view404);
    		modelAndView.add("viewName", uri);
    		response.render( modelAndView );
    	} else {
    		response.status(HttpStatus.NOT_FOUND);
    		response.html(String.format(Const.VIEW_NOTFOUND, uri));
		}
	}
	
	/**
	 * Methods to perform the interceptor
	 * 
	 * @param request		request object
	 * @param response		response object
	 * @param interceptors	execute the interceptor list
	 * @return				Return execute is ok
	 */
	private boolean invokeInterceptor(Request request, Response response, List<Route> interceptors) throws Exception {
		for (int i = 0, len = interceptors.size(); i < len; i++) {
			Route route = interceptors.get(i);
			boolean flag = routeViewHandler.intercept(request, response, route);
			if (!flag) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Actual routing method execution
	 * 
	 * @param request	request object
	 * @param response	response object
	 * @param route		route object
	 */
	private void routeHandle(Request request, Response response, Route route) throws Exception{
		Object target = route.getTarget();
		if(null == target){
			Class<?> clazz = route.getAction().getDeclaringClass();
			target = ioc.getBean(clazz);
			route.setTarget(target);
		}
		request.initPathParams(route.getPath());
		
		// Init context
		WebContextHolder.init(servletContext, request, response);
		if(route.getTargetType() == RouteHandler.class){
			RouteHandler routeHandler = (RouteHandler) target;
			routeHandler.handle(request, response);
		} else {
			routeViewHandler.handle(request, response, route);
		}
	}
    
}