/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.ioc;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Blade;
import com.blade.annotation.Controller;
import com.blade.annotation.Intercept;
import com.blade.annotation.RestController;
import com.blade.config.BaseConfig;
import com.blade.context.DynamicClassReader;
import com.blade.interceptor.Interceptor;
import com.blade.ioc.annotation.Component;
import com.blade.ioc.annotation.Service;
import com.blade.kit.StringKit;
import com.blade.kit.resource.ClassInfo;
import com.blade.kit.resource.ClassReader;
import com.blade.route.RouteBuilder;

/**
 * IOC container, used to initialize the IOC object
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
public class IocApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(IocApplication.class);

	private static List<Object> aopInterceptors = new ArrayList<Object>();

	/**
	 * Class to read object, load class
	 */
	private ClassReader classReader = null;
	private Blade blade;

	public IocApplication() {
		this.blade = Blade.$();
		this.classReader = DynamicClassReader.getClassReader();
	}

	private List<ClassInfo> loadCondigs() throws Exception {
		List<ClassInfo> configs = null;
		String[] configPackages = blade.config().getConfigPackages();
		if (null != configPackages && configPackages.length > 0) {
			configs = new ArrayList<ClassInfo>(10);
			for (String packageName : configPackages) {
				Set<ClassInfo> configClasses = classReader.getClassByAnnotation(packageName, Component.class, false);
				if (null != configClasses) {
					for (ClassInfo classInfo : configClasses) {
						if (classInfo.getClazz().getSuperclass().getName()
								.equals("com.blade.aop.AbstractMethodInterceptor")) {
							aopInterceptors.add(classInfo.newInstance());
						}
						Class<?>[] interfaces = classInfo.getClazz().getInterfaces();
						for (Class<?> in : interfaces) {
							if (in.equals(BaseConfig.class)) {
								configs.add(classInfo);
							}
						}
					}
				}
			}
		}
		return configs;
	}

	private List<ClassInfo> loadServices() throws Exception {
		List<ClassInfo> services = null;
		String[] configPackages = blade.config().getIocPackages();
		if (null != configPackages && configPackages.length > 0) {
			services = new ArrayList<ClassInfo>(20);
			for (String packageName : configPackages) {
				if (StringKit.isBlank(packageName)) {
					continue;
				}
				// Recursive scan
				boolean recursive = false;
				if (packageName.endsWith(".*")) {
					packageName = packageName.substring(0, packageName.length() - 2);
					recursive = true;
				}

				// Scan package all class
				Set<ClassInfo> iocClasses = classReader.getClass(packageName, recursive);
				for (ClassInfo classInfo : iocClasses) {
					Class<?> clazz = classInfo.getClazz();
					if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
						Component component = clazz.getAnnotation(Component.class);
						Service service = clazz.getAnnotation(Service.class);
						if (null != service || null != component) {
							services.add(classInfo);
						}
					}
				}
			}
		}
		return services;
	}

	private List<ClassInfo> loadControllers() {
		List<ClassInfo> controllers = null;
		String[] routePackages = blade.config().getRoutePackages();
		if (null != routePackages && routePackages.length > 0) {
			controllers = new ArrayList<ClassInfo>();
			for (String packageName : routePackages) {
				// Scan all Controoler
				controllers.addAll(classReader.getClassByAnnotation(packageName, Controller.class, true));
				controllers.addAll(classReader.getClassByAnnotation(packageName, RestController.class, true));
			}
		}
		return controllers;
	}

	private List<ClassInfo> loadInterceptors() {
		List<ClassInfo> interceptors = null;
		String interceptorPackage = blade.config().getInterceptorPackage();
		if (StringKit.isNotBlank(interceptorPackage)) {
			interceptors = new ArrayList<ClassInfo>(10);
			Set<ClassInfo> intes = classReader.getClassByAnnotation(interceptorPackage, Intercept.class, true);
			if(null != intes){
				for(ClassInfo classInfo : intes){
					if(null != classInfo.getClazz().getInterfaces() && classInfo.getClazz().getInterfaces()[0].equals(Interceptor.class)){
						interceptors.add(classInfo);
					}
				}
			}
		}
		return interceptors;
	}

	public void initBeans() throws Exception {
		List<ClassInfo> services = this.loadServices();
		List<ClassInfo> configs = this.loadCondigs();
		List<ClassInfo> controllers = this.loadControllers();
		// web
		List<ClassInfo> inteceptors = this.loadInterceptors();

		// 先获取所有被容器托管的Class, 再依次注入

		Ioc ioc = blade.ioc();

		RouteBuilder routeBuilder = blade.routeBuilder();

		// 1. 初始化service
		if (null != services) {
			for (ClassInfo classInfo : services) {
				ioc.addBean(classInfo.getClazz());
			}
		}

		// 2. 初始化配置文件
		if (null != configs) {
			for (ClassInfo classInfo : configs) {
				Object bean = ioc.addBean(classInfo.getClazz());
				BaseConfig baseConfig = (BaseConfig) bean;
				baseConfig.config(blade.applicationConfig());
			}
		}

		// 3. 初始化controller
		if (null != controllers) {
			for (ClassInfo classInfo : controllers) {
				ioc.addBean(classInfo.getClazz());
				routeBuilder.addRouter(classInfo.getClazz());
			}
		}

		// 4. 初始化interceptor
		if (null != inteceptors) {
			for (ClassInfo classInfo : inteceptors) {
				ioc.addBean(classInfo.getClazz());
				routeBuilder.addInterceptor(classInfo.getClazz());
			}
		}

		LOGGER.info("Add Object: {}", ioc.getBeans());

		// injection
		List<BeanDefine> beanDefines = ioc.getBeanDefines();
		if (null != beanDefines) {
			for (BeanDefine beanDefine : beanDefines) {
				IocKit.injection(ioc, beanDefine);
			}
		}

	}

	public static List<Object> getAopInterceptors() {
		return aopInterceptors;
	}

}