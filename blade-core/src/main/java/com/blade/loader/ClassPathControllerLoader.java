package com.blade.loader;

import com.blade.route.RoutesException;

public class ClassPathControllerLoader implements ControllerLoader {

	private String basePackage;

	private ClassLoader classLoader = ClassPathControllerLoader.class.getClassLoader();

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
	public Object load(String controllerName) throws RoutesException {
		String className = basePackage + controllerName;

		try {
			// load the controller class and instantiate it
			Class<?> controllerClass = classLoader.loadClass(className);
			return controllerClass.newInstance();
		} catch (Exception e) {
			throw new RoutesException(e);
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
