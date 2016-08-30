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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Blade;
import com.blade.Bootstrap;
import com.blade.context.ApplicationContext;
import com.blade.context.ApplicationWebContext;
import com.blade.kit.StringKit;
import com.blade.kit.SystemKit;
import com.blade.kit.resource.DynamicClassReader;

/**
 * Blade Core DispatcherServlet
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class DispatcherServlet extends HttpServlet {
	
	private static final long serialVersionUID = -2607425162473178733L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherServlet.class);
	
	private Blade blade = Blade.$();
	
	private Bootstrap bootstrap; 
	
	private ServletContext servletContext;
	
	private DispatcherHandler dispatcherHandler;
	
	public DispatcherServlet() {
	}
	
	public DispatcherServlet(Bootstrap bootstrap) {
		this.bootstrap = bootstrap;
		blade.init();
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		servletContext = config.getServletContext();
		if(!blade.isInit()){
			
			LOGGER.info("jdk.version = {}", SystemKit.getJavaInfo().getVersion());
			LOGGER.info("user.dir = {}", System.getProperty("user.dir"));
			LOGGER.info("java.io.tmpdir = {}", System.getProperty("java.io.tmpdir"));
			LOGGER.info("user.timezone = {}", System.getProperty("user.timezone"));
			LOGGER.info("file.encoding = {}", System.getProperty("file.encoding"));
			
			DynamicClassReader.init();
			
			long initStart = System.currentTimeMillis();
			
		    blade.webRoot(DispatchKit.getWebRoot(servletContext).getPath());
		    
		    ApplicationWebContext.init(servletContext);
		    
		    LOGGER.info("blade.webroot = {}", blade.webRoot());
		    
		    this.bootstrap = blade.bootstrap();
		    
			if(null == bootstrap){
				String bootStrapClassName = config.getInitParameter("bootstrap");
				if(StringKit.isNotBlank(bootStrapClassName)){
					this.bootstrap = getBootstrap(bootStrapClassName);
				} else {
					this.bootstrap = new Bootstrap() {
						@Override
						public void init(Blade blade) {
						}
					}; 
				}
				blade.app(this.bootstrap);
			}
			
			ApplicationContext.init(blade);
			
			LOGGER.info("blade.isDev = {}", blade.isDev());
			
		    dispatcherHandler = new DispatcherHandler(servletContext, blade.routers());
		    
		    new BladeBanner().print();
		    
		    String appName = blade.environment().getString("app.name", "Blade");
		    
		    LOGGER.info(appName + " initialize successfully, Time elapsed: {} ms.", System.currentTimeMillis() - initStart);
		}
	}
	
	@Override
	protected void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		httpRequest.setCharacterEncoding(blade.encoding());
		httpResponse.setCharacterEncoding(blade.encoding());
		dispatcherHandler.handle(httpRequest, httpResponse);
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	/**
     * Get global initialization object, the application of the initialization
     * 
     * @param botstrapClassName 	botstrap class name
     * @return 						return bootstrap object
     * @throws ServletException
     */
    @SuppressWarnings("unchecked")
	private Bootstrap getBootstrap(String botstrapClassName) throws ServletException {
    	Bootstrap bootstrapClass = null;
        try {
        	if(null != botstrapClassName){
            	Class<Bootstrap> applicationClass = (Class<Bootstrap>) Class.forName(botstrapClassName);
                if(null != applicationClass){
                	bootstrapClass = applicationClass.newInstance();
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
