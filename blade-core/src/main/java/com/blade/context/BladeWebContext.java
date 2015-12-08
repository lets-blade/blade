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
package com.blade.context;

import javax.servlet.ServletContext;

import com.blade.http.Request;
import com.blade.http.Response;
import com.blade.servlet.wrapper.Session;

/**
 * 全局的WeContext
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class BladeWebContext {
	
	/**
	 * 当前线程的Request对象
	 */
    private static ThreadLocal<BladeWebContext> ctx = new ThreadLocal<BladeWebContext>();
    
    /**
     * ServletContext对象，在应用初始化时创建
     */
    private ServletContext context; 
    
    /**
     * Request对象
     */
    private Request request;
    
    /**
     * Response对象
     */
    private Response response;
    
    private BladeWebContext(){}
    
    public static BladeWebContext me(){
    	return ctx.get();
    }
    
    public static void setContext(ServletContext context, Request request, Response response) {
    	BladeWebContext bladeWebContext = new BladeWebContext();
    	bladeWebContext.context = context;
    	bladeWebContext.request = request;
    	bladeWebContext.response = response;
    	ctx.set(bladeWebContext);
    }
    
    /**
     * 移除当前线程的Request、Response对象
     */
    public static void remove(){
    	ctx.remove();
    }
    
    public static Request request() {
        return me().request;
    }
    
    public static Response response() {
        return me().response;
    }
    
    public static Session session() {
        return request().session();
    }
    
	public static ServletContext servletContext() {
		return me().context;
	}
	
	public ServletContext getContext() {
		return context;
	}
	
	public Request getRequest() {
		return request;
	}

	public Response getResponse() {
		return response;
	}
	
}