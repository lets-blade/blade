package com.blade.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.blade.Blade;
import com.blade.context.WebContextHolder;

public class WebContextListener implements ServletContextListener, HttpSessionListener {
	
	private int timeout = Blade.$().config().getInt("server.timeout", 15 * 60);
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		event.getSession().setMaxInactiveInterval(timeout);
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		WebContextHolder.init(sce.getServletContext());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
	}


}
