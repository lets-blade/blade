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
package com.blade.server;

import org.eclipse.jetty.servlet.ServletContextHandler;

import blade.kit.log.Logger;

import com.blade.DispatcherServlet;

/**
 * 
 * <p>
 * Jetty服务
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Server {
	
	private static final Logger LOGGER = Logger.getLogger(Server.class);
	
	private int port = 9000;
	
	private org.eclipse.jetty.server.Server server;
	
	private ServletContextHandler context;
	
	public Server(int port) {
		this.port = port;
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	public void start(String contextPath) throws Exception{
		
		server = new org.eclipse.jetty.server.Server(this.port);
		
	    context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	    context.setContextPath(contextPath);
	    context.setResourceBase(System.getProperty("java.io.tmpdir"));
	    context.addServlet(DispatcherServlet.class, "/");
	    
//	    context.addFilter(CoreFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
	    
        server.setHandler(this.context);
	    server.start();
//	    server.dump(System.err);
	    LOGGER.info("Blade Server Listen on 0.0.0.0:" + this.port);
	}
	
	public void join() throws InterruptedException {
		server.join();
	}
	
	public void stop() throws Exception{
		context.stop();
		server.stop();
	}
}
