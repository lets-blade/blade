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
package com.blade.web;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import blade.kit.StringKit;
import blade.kit.base.ThrowableKit;
import blade.kit.log.Logger;

import com.blade.Blade;
import com.blade.Const;
import com.blade.context.BladeWebContext;
import com.blade.render.ModelAndView;
import com.blade.route.Route;
import com.blade.route.RouteHandler;
import com.blade.route.RouteMatcher;
import com.blade.web.http.HttpStatus;
import com.blade.web.http.Path;
import com.blade.web.http.Request;
import com.blade.web.http.Response;
import com.blade.web.http.wrapper.ServletRequest;
import com.blade.web.http.wrapper.ServletResponse;

/**
 * Asynchronous request processor
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class AsynRequestHandler implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(AsynRequestHandler.class);

	private Blade blade = Blade.me();
	
	private ServletContext servletContext;
	
	static RouteMatcher routeMatcher;
	
	private HttpServletRequest httpRequest;
	
	private HttpServletResponse httpResponse;
	
	private AsyncContext asyncContext;
	
	public AsynRequestHandler(ServletContext servletContext, AsyncContext asyncCtx) {
		this.asyncContext = asyncCtx;
		this.servletContext = servletContext;
		this.httpRequest = (HttpServletRequest) asyncCtx.getRequest();
		this.httpResponse = (HttpServletResponse) asyncCtx.getResponse();
	}
	
	@Override
	public void run(){
		
		Response response = null;
        try {
        	// http method, GET/POST ...
            String method = httpRequest.getMethod();
            
            // reuqest uri
            String uri = Path.getRelativePath(httpRequest.getRequestURI(), servletContext.getContextPath());
            
            // If it is static, the resource is handed over to the filter
            if(null != blade.staticFolder() && blade.staticFolder().length > 0){
            	if(!filterStaticFolder(uri)){
            		asyncContext.complete();
            		return;
            	}
            }
            
            if(blade.debug()){
            	LOGGER.debug("Request : " + method + "\t" + uri);
            }
            
            // Create Request
    		Request request = new ServletRequest(httpRequest);
            
    		// Create Response
            response = new ServletResponse(httpResponse, blade.render());
            
            // Init Context
         	BladeWebContext.setContext(servletContext, request, response);
         	
			Route route = routeMatcher.getRoute(method, uri);
			
			// If find it
			if (route != null) {
				request.setRoute(route);
				// before inteceptor
				List<Route> befores = routeMatcher.getBefore(uri);
				invokeInterceptor(request, response, befores);
				
				// execute
				handle(request, response, route);
				
				// after inteceptor
				List<Route> afters = routeMatcher.getAfter(uri);
				invokeInterceptor(request, response, afters);
				asyncContext.complete();
				return;
			}
			
			// Not found
			render404(response, uri);
			asyncContext.complete();
			return;
		} catch (Exception e) {
        	String error = ThrowableKit.getStackTraceAsString(e);
            LOGGER.error(error);
            ThrowableKit.propagate(e);
            
        	httpResponse.setStatus(500);
        	// Write content to the browser
            if (!httpResponse.isCommitted()) {
                response.html(Const.INTERNAL_ERROR);
                asyncContext.complete();
                return;
            }
        }
        asyncContext.complete();
        return;
	}
	
	/**
	 * 404 view render
	 * 
	 * @param response	response object
	 * @param uri		404 uri
	 */
	private void render404(Response response, String uri) {
		String view404 = blade.view404();
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
	 */
	private void invokeInterceptor(Request request, Response response, List<Route> interceptors) {
		for(Route route : interceptors){
			handle(request, response, route);
		}
	}

	/**
	 * Actual routing method execution
	 * 
	 * @param request	request object
	 * @param response	response object
	 * @param route		route object
	 */
	private void handle(Request request, Response response, Route route){
		
		Object target = route.getTarget();
		if(null == target){
			Class<?> clazz = route.getAction().getDeclaringClass();
			target = Blade.me().container().getBean(clazz, null);
		}
		request.initPathParams(route.getPath());
		
		// Init context
		BladeWebContext.setContext(servletContext, request, response);
		if(target instanceof RouteHandler){
			RouteHandler routeHandler = (RouteHandler)target;
			routeHandler.handle(request, response);
		} else {
			Method actionMethod = route.getAction();
			// execute
			RouteArgument.executeMethod(target, actionMethod, request, response);
		}
	}

	/**
	 * Filter out the directory
	 * 
	 * @param uri	URI represents the current path, filtering in a static directory
	 * @return		Return false, filter the success; return true, do not filter
	 */
	private boolean filterStaticFolder(String uri){
		int len = blade.staticFolder().length;
    	for(int i=0; i<len; i++){
    		if(uri.startsWith(blade.staticFolder()[i])){
    			return false;
    		}
    	}
    	return true;
	}
	
}
