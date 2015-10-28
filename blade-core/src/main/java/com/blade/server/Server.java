package com.blade.server;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.servlet.ServletContextHandler;

import com.blade.CoreFilter;

import blade.kit.log.Logger;

/**
 * Jetty服务
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Server {
	
	private static final Logger LOGGER = Logger.getLogger(Server.class);
	
	private int port = 9000;
	
	public Server(int port) {
		this.port = port;
	}
	
	public void run(String contextPath) throws Exception{
		
		org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(port);
		
	    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	    context.setContextPath(contextPath);
	    context.setResourceBase(System.getProperty("java.io.tmpdir"));
	    context.addFilter(CoreFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        server.setHandler(context);
        
	    server.start();
	    LOGGER.info("Blade Server Listen on 0.0.0.0:" + port);
	    server.join();
	}
}
