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
package blade;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import blade.exception.BladeException;
import blade.ioc.Container;
import blade.ioc.DefaultContainer;
import blade.ioc.Scope;
import blade.kit.PathKit;
import blade.kit.ReflectKit;
import blade.kit.log.Logger;
import blade.render.ModelAndView;
import blade.route.DefaultRouteMatcher;
import blade.route.HttpMethod;
import blade.route.RouteMatcher;
import blade.servlet.Request;
import blade.servlet.Response;
import blade.wrapper.RequestResponseBuilder;
import blade.wrapper.RequestWrapper;
import blade.wrapper.ResponseWrapper;

/**
 * 请求执行的Handler
 * <p>
 * 拦截器所有blade的请求，处理route and interceptor
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class RequestHandler {
	
	private static final Logger LOGGER = Logger.getLogger(RequestHandler.class);
	
	private static final String ACCEPT_TYPE_REQUEST_MIME_HEADER = "Accept";
	
    /**
     * 服务器500错误时返回的HTML
     */
    private static final String INTERNAL_ERROR = "<html><body><h2>500 Internal Error</h2></body></html>";
    
    private final static Container container = DefaultContainer.single();
    
    /**
     * 路由处理器，查找请求过来的URL
     */
    static DefaultRouteMatcher routeMatcher;
    
	private RequestHandler(){}
	
	public static RequestHandler single() {
        return RequestHandlerHolder.single;
    }
	
	/**
	 * 单例的RequestHandler
	 * 
	 * @author biezhi
	 * @since 1.0
	 *
	 */
	private static class RequestHandlerHolder {
        private static final RequestHandler single = new RequestHandler();
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
        if(null != Blade.staticFolder() && Blade.staticFolder().length > 0){
        	if(!filterStaticFolder(uri)){
        		return false;
        	}
        }
        
        String acceptType = httpRequest.getHeader(ACCEPT_TYPE_REQUEST_MIME_HEADER);
        
        // 响应体
        String bodyContent = null;
        Request request = null;
        
        // 构建一个包装后的response
        Response response = RequestResponseBuilder.build(httpResponse);
        
        // 创建RequestWrapper And RequestWrapper
        RequestWrapper requestWrapper = new RequestWrapper();
        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        
        if(Blade.debug()){
        	LOGGER.debug("Request : " + method + "\t" + uri);
        }
        
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        
        try {
        	
        	// 执行before拦截
        	before(requestWrapper, responseWrapper, httpRequest, uri, acceptType);
        	
        	// 查找用户请求的uri
			RouteMatcher match = routeMatcher.findRouteMatcher(httpMethod, uri, acceptType);
			
			// 如果找到
			if (match != null) {
				
				Class<?> target = match.getTarget();
				
				Object targetObject = container.getBean(target, Scope.SINGLE);
				
				// 要执行的路由方法
				Method execMethod = match.getExecMethod();
				
				if(null != requestWrapper.getDelegate()){
					request  = requestWrapper.getDelegate();
					request.initRequest(match);
				} else {
					request = RequestResponseBuilder.build(match, httpRequest);
				}
				requestWrapper.setDelegate(request);
                
				BladeWebContext.put(requestWrapper, responseWrapper);
				
				// 执行route方法
				Object result = executeMethod(targetObject, execMethod, requestWrapper, responseWrapper);
				
				// 执行after拦截
	        	after(requestWrapper, responseWrapper, httpRequest, uri, acceptType);
	            
	        	if(null != result){
	        		render(responseWrapper, result);
	        	}
				return true;
			} else {
				// 没有找到
				response.render404(uri);
			}
		} catch (BladeException bex) {
			LOGGER.error(bex.getMessage());
            httpResponse.setStatus(500);
            if (bex.getMessage() != null) {
                bodyContent = bex.getMessage();
            } else {
                bodyContent = INTERNAL_ERROR;
            }
        }
        boolean consumed = bodyContent != null;
        if (consumed) {
            // 写入内容到浏览器
            if (!httpResponse.isCommitted()) {
                response.render500(bodyContent);
                return true;
            }
        }
        return false;
        
	}
	
	/**
	 * 前置事件，在route执行前执行
	 * 这里如果执行则Request和Response都会被创建好
	 * 
	 * @param requestWrapper		RequestWrapper对象，包装了Request对象
	 * @param responseWrapper		ResponseWrapper对象，包装了Response对象
	 * @param httpRequest			HttpServletRequest请求对象，用于构建Request
	 * @param uri					请求的URI
	 * @param acceptType			请求头过滤
	 */
	private void before(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, HttpServletRequest httpRequest, final String uri, final String acceptType){
		
		List<RouteMatcher> matchSet = routeMatcher.findInterceptor(HttpMethod.BEFORE, uri, acceptType);
		
		for (RouteMatcher filterMatch : matchSet) {
			
        	Class<?> target = filterMatch.getTarget();
        	
        	Object targetObject = container.getBean(target, Scope.SINGLE);
        	
			Method execMethod = filterMatch.getExecMethod();
			
			Request request = RequestResponseBuilder.build(filterMatch, httpRequest);
			requestWrapper.setDelegate(request);
			
			executeMethod(targetObject, execMethod, requestWrapper, responseWrapper);
			
        }
	}
	
	/**
	 * 后置事件，在route执行后执行
	 * 
	 * @param requestWrapper		RequestWrapper对象，包装了Request对象
	 * @param responseWrapper		ResponseWrapper对象，包装了Response对象
	 * @param httpRequest			HttpServletRequest请求对象，用于构建Request
	 * @param uri					请求的URI
	 * @param acceptType			请求头过滤
	 */
	private String after(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, HttpServletRequest httpRequest, final String uri, final String acceptType){
        List<RouteMatcher> matchSet = routeMatcher.findInterceptor(HttpMethod.AFTER, uri, acceptType);
        
        String bodyContent = null;
        for (RouteMatcher filterMatch : matchSet) {
        	Class<?> target = filterMatch.getTarget();
        	
        	Object targetObject = container.getBean(target, Scope.SINGLE);
        	
			Method execMethod = filterMatch.getExecMethod();
			
			if (requestWrapper.getDelegate() == null) {
                Request request = RequestResponseBuilder.build(filterMatch, httpRequest);
                requestWrapper.setDelegate(request);
            } else {
                requestWrapper.initRequest(filterMatch);
            }
			
			executeMethod(targetObject, execMethod, requestWrapper, responseWrapper);
			
			String bodyAfterFilter = responseWrapper.getDelegate().body();
	        if (bodyAfterFilter != null) {
	            bodyContent = bodyAfterFilter;
	        }
        }
        
        return bodyContent;
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
		if(len > 0){
			Object[] args = getArgs(request, response, method.getParameterTypes());
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
	private Object render(Response response, Object result){
		if(result instanceof String){
			response.render(result.toString());
		} else if(result instanceof ModelAndView){
			response.render( (ModelAndView) result );
		}
		return null;
	}
	
	private boolean filterStaticFolder(String uri){
		int len = Blade.staticFolder().length;
    	for(int i=0; i<len; i++){
    		if(uri.startsWith(Blade.staticFolder()[i])){
    			return false;
    		}
    	}
    	return true;
	}
}
