package com.blade.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import blade.kit.TaskKit;
import blade.kit.log.Logger;

import com.blade.Blade;

@WebListener
public class BladeContextListener implements ServletContextListener {

	private static final Logger LOGGER = Logger.getLogger(BladeContextListener.class);
	
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