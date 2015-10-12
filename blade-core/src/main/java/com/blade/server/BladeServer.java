package com.blade.server;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.blade.CoreFilter;

import blade.kit.log.Logger;

public class BladeServer {
	
	private static final Logger LOGGER = Logger.getLogger(BladeServer.class);
	
	private int port = 9000;
	
	public BladeServer(int port) {
		this.port = port;
	}
	
	public void run(String contextPath) throws Exception{
		
		Server server = new Server(port);
		
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
