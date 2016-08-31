package com.blade.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Blade;
import com.blade.aop.ProxyIocImpl;

public class AopPlugin implements Plugin {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AopPlugin.class);
	
	@Override
	public void start() {
		LOGGER.info("Set Ioc container is {}", ProxyIocImpl.class.getName());
		Blade.$().container(new ProxyIocImpl());
	}

	@Override
	public void destroy() {
		
	}

}
