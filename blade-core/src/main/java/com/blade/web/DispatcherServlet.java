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

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import blade.kit.StringKit;
import blade.kit.log.Logger;

import com.blade.Aop;
import com.blade.Blade;
import com.blade.Bootstrap;
import com.blade.route.RouteBuilder;
import com.blade.route.RouteMatcher;

/**
 * Blade核心调度器
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class DispatcherServlet extends HttpServlet {
	
	private static final long serialVersionUID = -2607425162473178733L;
	
	private static final Logger LOGGER = Logger.getLogger(DispatcherServlet.class);
	
	private Blade blade = Blade.me();
	
	private Bootstrap bootstrap; 
	
	private ServletContext servletContext;
	
	private SyncRequestHandler syncRequestHandler;
	
	public DispatcherServlet() {
	}
	
	public DispatcherServlet(Bootstrap bootstrap) {
		this.bootstrap = bootstrap;
		blade.setInit(true);
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		if(!blade.isInit()){
			this.bootstrap = blade.bootstrap();
			if(null == bootstrap){
				String bootStrapClassName = config.getInitParameter("bootstrap");
				if(StringKit.isNotBlank(bootStrapClassName)){
					bootstrap = getBootstrap(bootStrapClassName);
				} else {
					bootstrap = new Bootstrap() {
						@Override
						public void init(Blade blade) {
						}
					}; 
				}
				blade.app(bootstrap);
			}
			bootstrap.init(blade);
			
		    // 构建路由
			new RouteBuilder(blade).building();
			
			// 初始化IOC
			blade.iocInit();
			
		    blade.bootstrap().contextInitialized(blade);
		    
		    servletContext = config.getServletContext();
		    
		    syncRequestHandler = new SyncRequestHandler(servletContext, blade.routers());
		    AsynRequestHandler.routeMatcher = new RouteMatcher(blade.routers());
		    
		    blade.setInit(true);
		    LOGGER.info("blade init complete!");
		}
	}

	@Override
	protected void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		httpRequest.setCharacterEncoding(blade.encoding());
		httpResponse.setCharacterEncoding(blade.encoding());
		
		boolean isAsync = httpRequest.isAsyncSupported();
		if (isAsync) {
			AsyncContext asyncCtx = httpRequest.startAsync();
			asyncCtx.addListener(new AppAsyncListener());
			asyncCtx.setTimeout(10000L);
			asyncCtx.start(new AsynRequestHandler(servletContext, asyncCtx));
		} else {
			syncRequestHandler.handle(httpRequest, httpResponse);
		}
	}
	
	/**
     * 获取全局初始化对象，初始化应用
     * 
     * @param botstrapClassName 		全局初始化类名
     * @return 							一个全局初始化对象
     * @throws ServletException
     */
    @SuppressWarnings("unchecked")
	private Bootstrap getBootstrap(String botstrapClassName) throws ServletException {
    	Bootstrap bootstrapClass = null;
        try {
        	if(null != botstrapClassName){
            	Class<Bootstrap> applicationClass = (Class<Bootstrap>) Class.forName(botstrapClassName);
                if(null != applicationClass){
                	bootstrapClass = Aop.create(applicationClass);
                }
        	} else {
        		throw new ServletException("bootstrapClass is null !");
			}
        } catch (Exception e) {
            throw new ServletException(e);
        }
		return bootstrapClass;
    }
}
