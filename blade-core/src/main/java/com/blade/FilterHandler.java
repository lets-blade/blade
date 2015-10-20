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

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blade.ioc.Container;
import com.blade.ioc.Scope;
import com.blade.ioc.impl.DefaultContainer;
import com.blade.render.ModelAndView;
import com.blade.route.HttpMethod;
import com.blade.route.RouteHandler;
import com.blade.route.RouteMatcher;
import com.blade.route.impl.DefaultRouteMatcher;
import com.blade.servlet.Request;
import com.blade.servlet.Response;
import com.blade.wrapper.RequestResponseBuilder;
import com.blade.wrapper.RequestWrapper;
import com.blade.wrapper.ResponseWrapper;

import blade.exception.BladeException;
import blade.kit.PathKit;
import blade.kit.ReflectKit;
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
public class FilterHandler {
	
	private static final Logger LOGGER = Logger.getLogger(FilterHandler.class);
	
	private Blade blade;
	
    /**
     * 服务器500错误时返回的HTML
     */
	
	private static final String INTERNAL_ERROR = "<html><head><title>500 Internal Error</title></head><body bgcolor=\"white\"><center><h1>500 Internal Error</h1></center><hr><center>blade "
			+ Blade.VERSION +"</center></body></html>";
    
	/**
	 * IOC容器
	 */
    private final static Container container = DefaultContainer.single();
    
    /**
     * 路由处理器，查找请求过来的URL
     */
    private static final DefaultRouteMatcher DEFAULT_ROUTE_MATCHER = DefaultRouteMatcher.instance();
    
	public FilterHandler(Blade blade){
		this.blade = blade;
	}
	
	/**
	 * handler执行方法
	 * 
	 * @param httpRequest	HttpServletRequest请求对象
	 * @param httpResponse	HttpServletResponse响应对象
	 * @return 是否拦截到请求
	 */
	boolean handler(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
		
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
        RequestWrapper requestWrapper = new RequestWrapper();
        ResponseWrapper responseWrapper = new ResponseWrapper();
        
        // 构建一个包装后的response
        Response response = RequestResponseBuilder.build(httpResponse);
        
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        Object result = null;
        try {
        	responseWrapper.setDelegate(response);
        	
        	// 查找用户请求的uri
			RouteMatcher match = DEFAULT_ROUTE_MATCHER.findRoute(httpMethod, uri);
			// 如果找到
			if (match != null) {
				// 执行before拦截
				result = intercept(httpRequest, requestWrapper, responseWrapper, uri, HttpMethod.BEFORE);
				if(result instanceof Boolean){
					boolean isHandler = (Boolean) result;
					if(!isHandler){
						return false;
					}
				}
				
				if(result instanceof String){
					String res = result.toString();
					if(res.startsWith("redirect.")){
						response.go(res.substring(9));
					} else {
						render(responseWrapper, res);
					}
					return true;
				}
				
				if(result instanceof ModelAndView){
					render(responseWrapper, result);
					return true;
				}
				
				// 实际执行方法
				responseWrapper.setDelegate(response);
				result = realHandler(httpRequest, requestWrapper, responseWrapper, match);
				
				// 执行after拦截
				responseWrapper.setDelegate(response);
				intercept(httpRequest, requestWrapper, responseWrapper, uri, HttpMethod.AFTER);
				
				if (null != result)
					render(responseWrapper, result);
				return true;
			}
			
			// 没有找到
			response.render404(uri);
			return true;
		} catch (BladeException bex) {
			
			String error = ThrowableKit.getStackTraceAsString(bex);
            LOGGER.error(error);
            ThrowableKit.propagate(bex);
			
            httpResponse.setStatus(500);
            // 写入内容到浏览器
            if (!httpResponse.isCommitted()) {
                response.render500(INTERNAL_ERROR);
                return true;
            }
        } catch (Exception e) {
        	String error = ThrowableKit.getStackTraceAsString(e);
            LOGGER.error(error);
            ThrowableKit.propagate(e);
            
        	httpResponse.setStatus(500);
        	// 写入内容到浏览器
            if (!httpResponse.isCommitted()) {
                response.render500(INTERNAL_ERROR);
                return true;
            }
        }
        return false;
        
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
	private Object realHandler(HttpServletRequest httpRequest, RequestWrapper requestWrapper, ResponseWrapper responseWrapper, RouteMatcher match){
		Object result = null;
		RouteHandler router = match.getRouterHandler();
		
		if (requestWrapper.getDelegate() == null) {
            Request request = RequestResponseBuilder.build(match, httpRequest);
            requestWrapper.setDelegate(request);
        } else {
            requestWrapper.initRequest(match);
        }
		
		// 初始化context
		BladeWebContext.setContext(CoreFilter.servletContext, httpRequest, responseWrapper.servletResponse(), requestWrapper, responseWrapper);
					
		if(null != router){
			
			result = router.handler(requestWrapper, responseWrapper);
			
		} else {
			Class<?> target = match.getTarget();
			
			Object targetObject = container.getBean(target, Scope.SINGLE);
			
			// 要执行的路由方法
			Method execMethod = match.getExecMethod();
			
			// 执行route方法
			result = executeMethod(targetObject, execMethod, requestWrapper, responseWrapper);
		}
		
		return result;
	}
	
	/**
	 * 拦截器事件
	 * 
	 * @param httpRequest			HttpServletRequest请求对象，用于构建Request
	 * @param requestWrapper		RequestWrapper对象，包装了Request对象
	 * @param responseWrapper		ResponseWrapper对象，包装了Response对象
	 * @param uri					请求的URI
	 */
	private Object intercept(HttpServletRequest httpRequest, RequestWrapper requestWrapper, 
			ResponseWrapper responseWrapper, String uri, HttpMethod httpMethod){
        List<RouteMatcher> matchSet = DEFAULT_ROUTE_MATCHER.findInterceptor(httpMethod, uri);
        
        for (RouteMatcher filterMatch : matchSet) {
			Object object = realHandler(httpRequest, requestWrapper, responseWrapper, filterMatch);
			if(null != object){
				return object;
			}
        }
		
        return true;
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
	private Object executeMethod(Object object, Method method, RequestWrapper requestWrapper, ResponseWrapper responseWrapper){
		int len = method.getParameterTypes().length;
		method.setAccessible(true);
		if(len > 0){
			Object[] args = getArgs(requestWrapper, responseWrapper, method.getParameterTypes());
			return ReflectKit.invokeMehod(object, method, args);
		} else {
			return ReflectKit.invokeMehod(object, method);
		}
	}
	
	/**
	 * 渲染视图
	 * 
	 * @param response
	 * @param result
	 * @return
	 */
	private void render(Response response, Object result){
		if(result instanceof String){
			response.render(result.toString());
		} else if(result instanceof ModelAndView){
			response.render( (ModelAndView) result );
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
