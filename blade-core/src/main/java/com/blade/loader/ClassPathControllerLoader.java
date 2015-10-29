package com.blade.loader;

import com.blade.ioc.Container;
import com.blade.ioc.SampleContainer;
import com.blade.ioc.Scope;
import com.blade.route.RouteException;

/**
 * 
 * <p>
 * ClassPath控制器加载器
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class ClassPathControllerLoader implements ControllerLoader {

	private String basePackage;

	private ClassLoader classLoader = ClassPathControllerLoader.class.getClassLoader();

	private Container container = SampleContainer.single();
	
	public ClassPathControllerLoader() {
		this("");
	}

	public ClassPathControllerLoader(String basePackage) {
		this.basePackage = basePackage;

		if (this.basePackage != null && !"".equals(this.basePackage)) {
			if (!this.basePackage.endsWith(".")) {
				this.basePackage += ".";
			}
		}
	}

	@Override
	public Object load(String controllerName) throws RouteException {
		String className = basePackage + controllerName;

		try {
			// 加载控制器实例
			Class<?> controllerClass = classLoader.loadClass(className);
			
			Object controller = container.getBean(controllerClass, Scope.SINGLE);
			if(null == controller){
				controller = controllerClass.newInstance();
				container.registBean(controller);
			}
			return controller;
		} catch (Exception e) {
			throw new RouteException(e);
		}
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
