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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import blade.servlet.Request;
import blade.servlet.Response;
import blade.servlet.Session;
import blade.wrapper.RequestWrapper;
import blade.wrapper.ResponseWrapper;

/**
 * 全局的WeContext
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class BladeWebContext {
	
	/**
	 * 当前线程的Request对象
	 */
    private static final ThreadLocal<BladeWebContext> BLADE_WEB_CONTEXT = new ThreadLocal<BladeWebContext>();
    
    /**
     * ServletContext对象，在应用初始化时创建
     */
    private ServletContext context; 
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private Request request;
    private Response response;
    
    private BladeWebContext(){}
    
    public static BladeWebContext me(){
    	return BLADE_WEB_CONTEXT.get();
    }
    
    static void setContext(ServletContext servletContext, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, RequestWrapper request, ResponseWrapper response) {
    	BladeWebContext bladeWebContext = new BladeWebContext();
    	bladeWebContext.context = servletContext;
    	bladeWebContext.httpServletRequest = httpServletRequest;
    	bladeWebContext.httpServletResponse = httpServletResponse;
    	bladeWebContext.request = request.getDelegate();
    	bladeWebContext.response = response.getDelegate();
    	BLADE_WEB_CONTEXT.set(bladeWebContext);
    }
    
    /**
     * 移除当前线程的Request、Response对象
     */
    public static void remove(){
    	BLADE_WEB_CONTEXT.remove();
    }
    
    public static Request request() {
        return BladeWebContext.me().request;
    }
    
    public static Response response() {
        return BladeWebContext.me().response;
    }
    
    public static Session session() {
        return request().session();
    }
    
	public static ServletContext servletContext() {
		return BladeWebContext.me().context;
	}
	
	public static HttpServletResponse servletResponse() {
		return BladeWebContext.me().httpServletResponse;
	}
	
	public static HttpServletRequest servletRequest() {
		return BladeWebContext.me().httpServletRequest;
	}

	public ServletContext getContext() {
		return context;
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}

	public Request getRequest() {
		return request;
	}

	public Response getResponse() {
		return response;
	}
	
}