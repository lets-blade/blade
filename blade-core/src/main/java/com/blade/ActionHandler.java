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
package com.blade;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blade.context.BladeWebContext;
import com.blade.http.HttpStatus;
import com.blade.http.Request;
import com.blade.http.Response;
import com.blade.render.ModelAndView;
import com.blade.route.Route;
import com.blade.route.RouteHandler;
import com.blade.route.Router;
import com.blade.route.SampleRouteMatcher;
import com.blade.servlet.ServletRequest;
import com.blade.servlet.ServletResponse;

import blade.exception.BladeException;
import blade.kit.PathKit;
import blade.kit.ReflectKit;
import blade.kit.StringKit;
import blade.kit.base.ThrowableKit;
import blade.kit.log.Logger;

/**
 * 请求执行的Handler
 * <p>
 * 拦截器所有blade的请求，处理route and interceptor
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class ActionHandler {
	
	private static final Logger LOGGER = Logger.getLogger(ActionHandler.class);
	
	private Blade blade;
	
	private Router router;
	
	private SampleRouteMatcher sampleRouteMatcher;
	
    /**
     * 服务器500错误时返回的HTML
     */
	
	private static final String INTERNAL_ERROR = "<html><head><title>500 Internal Error</title></head><body bgcolor=\"white\"><center><h1>500 Internal Error</h1></center><hr><center>blade "
			+ Blade.VERSION +"</center></body></html>";
    
	private static final String VIEW_NOTFOUND = "<html><head><title>404 Not Found</title></head><body bgcolor=\"white\"><center><h1>[ %s ] Not Found</h1></center><hr><center>blade "
			+ Blade.VERSION +"</center></body></html>";
	
	public ActionHandler(Blade blade){
		this.blade = blade;
		this.router = blade.router();
		this.sampleRouteMatcher = new SampleRouteMatcher(router);
	}
	
	/**
	 * handler执行方法
	 * 
	 * @param httpRequest	HttpServletRequest请求对象
	 * @param httpResponse	HttpServletResponse响应对象
	 * @return 是否拦截到请求
	 */
	public boolean handle(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
		
		Response response = null;
        try {
        	
        	// http方法, GET/POST ...
            String method = httpRequest.getMethod();
            
            // 请求的uri
            String uri = PathKit.getRelativePath(httpRequest);
            
            // 如果是静态资源则交给filter处理
            if(null != blade.staticFolder() && blade.staticFolder().length > 0){
            	if(!filterStaticFolder(uri)){
            		return false;
            	}
            }
            
            if(blade.debug()){
            	LOGGER.debug("Request : " + method + "\t" + uri);
            }
        	
        	// 创建RequestWrapper And RequestWrapper
    		Request request = new ServletRequest(httpRequest);
            
            // 构建一个包装后的response
            response = new ServletResponse(httpResponse, blade.render());
            
            // 初始化context
         	BladeWebContext.setContext(request, response);
         	
			Route route = sampleRouteMatcher.getRoute(method, uri);
			
			// 如果找到
			if (route != null) {
				// 执行before拦截
				List<Route> befores = sampleRouteMatcher.getBefore(uri);
				invokeInterceptor(request, response, befores);
				
				// 实际执行方法
				handle(request, response, route);
				
				// 执行after拦截
				List<Route> afters = sampleRouteMatcher.getAfter(uri);
				invokeInterceptor(request, response, afters);
				return true;
			}
			
			// 没有找到
			render404(response, uri);
			return true;
		} catch (BladeException bex) {
			
			String error = ThrowableKit.getStackTraceAsString(bex);
            LOGGER.error(error);
            ThrowableKit.propagate(bex);
			
            httpResponse.setStatus(500);
            // 写入内容到浏览器
            if (!httpResponse.isCommitted()) {
                response.html(INTERNAL_ERROR);
                return true;
            }
        } catch (Exception e) {
        	String error = ThrowableKit.getStackTraceAsString(e);
            LOGGER.error(error);
            ThrowableKit.propagate(e);
            
        	httpResponse.setStatus(500);
        	// 写入内容到浏览器
            if (!httpResponse.isCommitted()) {
                response.html(INTERNAL_ERROR);
                return true;
            }
        }
        return false;
        
	}
	
	private void render404(Response response, String uri) throws IOException{
		String view404 = blade.view404();
    	if(StringKit.isNotBlank(view404)){
    		ModelAndView modelAndView = new ModelAndView(view404);
    		modelAndView.add("viewName", uri);
    		response.render( modelAndView );
    	} else {
    		response.status(HttpStatus.NOT_FOUND);
    		response.html(String.format(VIEW_NOTFOUND, uri));
		}
	}
	
	private void invokeInterceptor(Request request, Response response, List<Route> interceptors) {
		for(Route route : interceptors){
			handle(request, response, route);
		}
	}

	/**
	 * 
	 * 实际的路由方法执行
	 * @param httpRequest		http请求对象
	 * @param requestWrapper	request包装对象
	 * @param responseWrapper	response包装对象
	 * @param match				路由匹配对象
	 * @return	object
	 */
	private void handle(Request request, Response response, Route route){
		RouteHandler router = (RouteHandler) route.getTarget();
		request.initPathParams(request.path());
		// 初始化context
		BladeWebContext.setContext(request, response);
		if(null != router){
			router.handle(request, response);
		} else {
			Object target = route.getTarget();
			// 要执行的路由方法
			Method actionMethod = route.getAction();
			// 执行route方法
			executeMethod(target, actionMethod, request, response);
		}
	}
	
	/**
	 * 获取方法内的参数
	 * 
	 * @param request		Request对象，用于注入到method参数列表中
	 * @param response		Response对象，用于注入到method参数列表中
	 * @param params		params参数列表
	 * @return				返回生成后的参数数组
	 */
	private Object[] getArgs(Request request, Response response, Class<?>[] params){
		
		int len = params.length;
		Object[] args = new Object[len];
		
		for(int i=0; i<len; i++){
			Class<?> paramTypeClazz = params[i];
			if(paramTypeClazz.getName().equals(Request.class.getName())){
				args[i] = request;
			}
			if(paramTypeClazz.getName().equals(Response.class.getName())){
				args[i] = response;
			}
		}
		
		return args;
	}
	
	/**
	 * 执行路由方法
	 * @param object		方法的实例，即该方法所在类的对象
	 * @param method		要执行的method
	 * @param request		Request对象，作为参数注入
	 * @param response		Response对象，作为参数注入
	 * @return				返回方法执行后的返回值
	 */
	private Object executeMethod(Object object, Method method, Request request, Response response){
		int len = method.getParameterTypes().length;
		method.setAccessible(true);
		if(len > 0){
			Object[] args = getArgs(request, response, method.getParameterTypes());
			return ReflectKit.invokeMehod(object, method, args);
		} else {
			return ReflectKit.invokeMehod(object, method);
		}
	}
	
	/**
	 * 要过滤掉的目录
	 * @param uri
	 * @return
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
