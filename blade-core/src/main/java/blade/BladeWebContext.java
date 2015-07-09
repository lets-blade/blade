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
    private static ThreadLocal<Request> currentRequest = new ThreadLocal<Request>();
    
    /**
     * 当前线程的Response对象
     */
    private static ThreadLocal<Response> currentResponse = new ThreadLocal<Response>();
    
    /**
     * ServletContext对象，在应用初始化时创建
     */
    private static ServletContext servletContext;
    
    private BladeWebContext(){}
    
    /**
     * @return 返回当前线程的Request对象
     */
    public static Request request() {
        return currentRequest.get();
    }
    
    /**
     * @return 返回当前线程的HttpServletRequest对象
     */
    public static HttpServletRequest servletRequest() {
        return request().servletRequest();
    }

    /**
     * @return 返回当前线程的Response对象
     */
    public static Response response() {
        return currentResponse.get();
    }
    
    /**
     * @return 返回当前线程的HttpServletResponse对象
     */
    public static HttpServletResponse servletResponse() {
        return response().servletResponse();
    }

    /**
     * @return 返回当前线程的Session对象
     */
    public static Session session() {
        return request().session();
    }

    /**
     * 设置ServletContext
     * 
     * @param servletContext	ServletContext对象
     */
    public static void servletContext(ServletContext servletContext) {
		BladeWebContext.servletContext = servletContext;
	}
    
    /**
     * @return 返回当前线程的ServletContext对象
     */
    public static ServletContext servletContext() {
    	return servletContext;
    }
    
    /**
     * 设置context对象到ActionContext中
     * 
     * @param request 		HttpServletRequest对象
     * @param response 		HttpServletResponse对象
     */
    public static void put(Request request, Response response) {
    	currentRequest.set(request);
    	currentResponse.set(response);
    }
    
    /**
     * 移除当前线程的Request、Response对象
     */
    public static void remove(){
    	currentRequest.remove();
    	currentResponse.remove();
    }

}