package com.blade.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Blade;

import blade.kit.TaskKit;

@WebListener
public class BladeContextListener implements ServletContextListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(BladeContextListener.class);
	
	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		LOGGER.info("blade context Initialized!");
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
		LOGGER.info("blade destroy!");
    	BladeWebContext.remove();
    	Blade.me().iocApplication().destroy();
    	TaskKit.depose();
	}
}