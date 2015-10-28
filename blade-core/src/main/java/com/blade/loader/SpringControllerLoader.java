package com.blade.loader;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.blade.route.RoutesException;

public class SpringControllerLoader implements ControllerLoader, ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	@Override
	public Object load(String controllerName) throws RoutesException {
		Object bean = applicationContext.getBean(controllerName);
		if (bean == null) {
			throw new RoutesException("Bean '" + controllerName + "' was not found.");
		}
		return bean;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
