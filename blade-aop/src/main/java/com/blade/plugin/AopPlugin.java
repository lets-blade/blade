package com.blade.plugin;

import com.blade.Blade;
import com.blade.aop.ProxyIocImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AopPlugin implements Plugin {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AopPlugin.class);
	
	@Override
	public void startup() {
		LOGGER.info("Set Ioc container is {}", ProxyIocImpl.class.getName());
		Blade.$().container(new ProxyIocImpl());
	}
	
	@Override
	public void shutdown() {
	}

}