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

import com.blade.Blade;
import com.blade.comparator.OrderComparator;
import com.blade.config.BaseConfig;
import com.blade.context.DynamicContext;
import com.blade.ioc.annotation.Component;
import com.blade.ioc.annotation.Service;
import com.blade.kit.CollectionKit;
import com.blade.kit.StringKit;
import com.blade.kit.resource.ClassInfo;
import com.blade.kit.resource.ClassReader;
import com.blade.mvc.annotation.Controller;
import com.blade.mvc.annotation.Intercept;
import com.blade.mvc.annotation.RestController;
import com.blade.mvc.interceptor.Interceptor;
import com.blade.mvc.route.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * IOC container, used to initialize the IOC object
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
public final class IocApplication {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IocApplication.class);

	/**
	 * aop interceptor
	 */
	private static List<Object> aopInterceptors = CollectionKit.newArrayList(8);
	
	/**
	 * Class to read object, load class
	 */
	private ClassReader classReader = null;
	private Blade blade;
	private OrderComparator orderComparator;
	
	public IocApplication() {
		this.blade = Blade.$();
		this.classReader = DynamicContext.getClassReader();
		this.orderComparator = new OrderComparator();
	}
	
	/**
	 * load config beans
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<ClassInfo> loadCondigs() throws Exception {
		String[] configPkgs = blade.configuration().getConfigPkgs();
		if (null != configPkgs && configPkgs.length > 0) {
			List<ClassInfo> configs = CollectionKit.newArrayList(10);
			for (int i = 0, len = configPkgs.length; i < len; i++) {
				Set<ClassInfo> configClasses = classReader.getClassByAnnotation(configPkgs[i], Component.class, false);
				if (null != configClasses) {
					for (ClassInfo classInfo : configClasses) {
						Class<?>[] interfaces = classInfo.getClazz().getInterfaces();
						for (Class<?> in : interfaces) {
							if (in.equals(BaseConfig.class)) {
								configs.add(classInfo);
							}
						}
						if (classInfo.getClazz().getSuperclass().getName().equals("com.blade.aop.AbstractMethodInterceptor")) {
							aopInterceptors.add(classInfo.newInstance());
						}
					}
				}
			}
			Collections.sort(configs, orderComparator);
			return configs;
		}
		return null;
	}

	private List<ClassInfo> loadServices() throws Exception {
		String[] iocPkgs = blade.configuration().getIocPkgs();
		if (null != iocPkgs && iocPkgs.length > 0) {
			List<ClassInfo> services = CollectionKit.newArrayList(16);
			for (int i = 0, len = iocPkgs.length; i < len; i++) {
				String pkgName = iocPkgs[i];
				if (StringKit.isBlank(pkgName)) {
					continue;
				}
				// Recursive scan
				boolean recursive = false;
				if (pkgName.endsWith(".*")) {
					pkgName = pkgName.substring(0, pkgName.length() - 2);
					recursive = true;
				}

				// Scan package all class
				Set<ClassInfo> iocClasses = classReader.getClass(pkgName, recursive);
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
			return services;
		}
		return null;
	}

	private List<ClassInfo> loadControllers() {
		String[] routePkgs = blade.configuration().getRoutePkgs();
		if (null != routePkgs && routePkgs.length > 0) {
			List<ClassInfo> controllers = CollectionKit.newArrayList(8);
			for(int i=0, len=routePkgs.length; i<len; i++){
				// Scan all Controoler
				controllers.addAll(classReader.getClassByAnnotation(routePkgs[i], Controller.class, true));
				controllers.addAll(classReader.getClassByAnnotation(routePkgs[i], RestController.class, true));
			}
			return controllers;
		}
		return null;
	}

	private List<ClassInfo> loadInterceptors() {
		String interceptorPackage = blade.configuration().getInterceptorPkg();
		if (StringKit.isNotBlank(interceptorPackage)) {
			List<ClassInfo> interceptors = CollectionKit.newArrayList(8);
			Set<ClassInfo> intes = classReader.getClassByAnnotation(interceptorPackage, Intercept.class, true);
			if (null != intes) {
				for (ClassInfo classInfo : intes) {
					if (null != classInfo.getClazz().getInterfaces()
							&& classInfo.getClazz().getInterfaces()[0].equals(Interceptor.class)) {
						interceptors.add(classInfo);
					}
				}
			}
			return interceptors;
		}
		return null;
	}

	public void initBeans() throws Exception {
		List<ClassInfo> services = this.loadServices();
		List<ClassInfo> configs = this.loadCondigs();
		List<ClassInfo> controllers = this.loadControllers();
		// web
		List<ClassInfo> inteceptors = this.loadInterceptors();

		Ioc ioc = blade.ioc();

		RouteBuilder routeBuilder = blade.routeBuilder();

		// 1. init service
		if (null != services) {
			for(int i=0, len=services.size(); i<len; i++){
				ioc.addBean(services.get(i).getClazz());
			}
		}
		
		List<BaseConfig> baseConfigs = CollectionKit.newArrayList();

		// 2. init configs
		if (null != configs) {
			for(int i=0, len=configs.size(); i<len; i++){
				Object bean = ioc.addBean(configs.get(i).getClazz());
				baseConfigs.add( (BaseConfig) bean);
			}
		}

		// 3. init controller
		if (null != controllers) {
			for(int i=0, len=controllers.size(); i<len; i++){
				ioc.addBean(controllers.get(i).getClazz());
				routeBuilder.addRouter(controllers.get(i).getClazz());
			}
		}

		// 4. init interceptor
		if (null != inteceptors) {
			for(int i=0, len=inteceptors.size(); i<len; i++){
				ioc.addBean(inteceptors.get(i).getClazz());
				routeBuilder.addInterceptor(inteceptors.get(i).getClazz());
			}
		}

		if(null != ioc.getBeans() && !ioc.getBeans().isEmpty()){
			LOGGER.info("Add Object: {}", ioc.getBeans());
		}

		// init configs
		for(BaseConfig baseConfig : baseConfigs){
			baseConfig.config(blade.configuration());
		}
		
		// injection
		List<BeanDefine> beanDefines = ioc.getBeanDefines();
		if (null != beanDefines) {
			for(int i=0, len=beanDefines.size(); i<len; i++){
				IocKit.injection(ioc, beanDefines.get(i));
			}
		}

	}
	
	public static List<Object> getAopInterceptors() {
		return aopInterceptors;
	}
	
}