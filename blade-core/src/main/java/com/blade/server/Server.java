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
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.web.DispatcherServlet;

/**
 * Jetty Server
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Server {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
	
	private int port = 9000;
	
	private boolean async = true;
	
	private org.eclipse.jetty.server.Server server;
	
	private ServletContextHandler context;
	
	public Server(int port, boolean async) {
		this.port = port;
		this.async = async;
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	public void setAsync(boolean async) {
		this.async = async;
	}

	public void start(String contextPath) throws Exception{
		
		server = new org.eclipse.jetty.server.Server(this.port);
		
	    context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	    context.setContextPath(contextPath);
	    context.setResourceBase(System.getProperty("java.io.tmpdir"));
	    
	    ServletHolder servletHolder = new ServletHolder(DispatcherServlet.class);
	    servletHolder.setAsyncSupported(async);
	    
	    context.addServlet(servletHolder, "/");
        server.setHandler(this.context);
	    server.start();
//	    server.dump(System.err);
	    LOGGER.info("Blade Server Listen on 0.0.0.0:{}", this.port);
	}
	
	public void join() throws InterruptedException {
		server.join();
	}
	
	public void stop() throws Exception{
		context.stop();
		server.stop();
	}
}
