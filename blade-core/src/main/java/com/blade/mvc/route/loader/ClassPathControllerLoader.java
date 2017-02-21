/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.mvc.route.loader;

import com.blade.Blade;
import com.blade.exception.RouteException;
import com.blade.ioc.Ioc;
import com.blade.kit.StringKit;

/**
 * ClassPath controller of loader 
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class ClassPathControllerLoader implements ControllerLoader {

	private String basePackage;

	private ClassLoader classLoader = ClassPathControllerLoader.class.getClassLoader();

	private Ioc ioc = Blade.$().ioc();

	public ClassPathControllerLoader() {
		this("");
	}

	public ClassPathControllerLoader(String basePackage) {
		this.basePackage = basePackage;

		if (StringKit.isNotBlank(basePackage)) {
			if (!this.basePackage.endsWith(".")) {
				this.basePackage += '.';
			}
		}
	}

	@Override
	public Object load(String controllerName) throws RouteException {
		String className = basePackage + controllerName;

		try {
			// Load controller instance
			Class<?> controllerClass = classLoader.loadClass(className);

			Object controller = ioc.getBean(controllerClass);
			if (null == controller) {
				ioc.addBean(controllerClass);
				controller = ioc.getBean(controllerClass);
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
