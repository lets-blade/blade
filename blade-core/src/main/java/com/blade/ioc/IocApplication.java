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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Blade;
import com.blade.Bootstrap;
import com.blade.ioc.annotation.Component;
import com.blade.plugin.Plugin;
import com.blade.route.Route;
import com.blade.route.RouteHandler;
import com.blade.route.Routers;

import blade.kit.CollectionKit;
import blade.kit.resource.ClassPathClassReader;
import blade.kit.resource.ClassReader;

/**
 * IOC container, used to initialize the IOC object
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class IocApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(IocApplication.class);
	
	/**
	 * Ioc Container
	 */
	private Ioc ioc = null;
	
	/**
	 * Class to read object, load class
	 */
	private ClassReader classReader = null;
	
	private String[] iocs;
	private Bootstrap bootstrap;
	
	/**
	 * Plugin List
	 */
	private List<Plugin> plugins = null;
	
	private Set<Class<? extends Plugin>> pluginTypes;
	
	private Blade blade;
	
	public IocApplication(Blade blade) {
		this.blade = blade;
		this.classReader = new ClassPathClassReader();
		this.plugins = CollectionKit.newArrayList();
		this.pluginTypes = blade.plugins();
		this.ioc = blade.ioc();
		this.iocs = blade.iocs();
		this.bootstrap = blade.bootstrap();
	}
	
	/**
	 * IOC initialize
	 * @param iocs		ioc packages
	 * @param bootstrap	bootstrap object
	 */
	public void init(){
		
		// Initialize the global configuration class
		if(null == ioc.getBean(Bootstrap.class)){
			ioc.addBean(bootstrap);
		}
		
		// The object to initialize the IOC container loads the IOC package to configure the class that conforms to the IOC
		if(null != iocs && iocs.length > 0){
			for(String packageName : iocs){
				registerBean(packageName);
			}
		}
		
		for(Class<? extends Plugin> type : pluginTypes){
			ioc.addBean(type);
			Plugin plugin = ioc.getBean(type);
			plugins.add(plugin);
		}
		
		// init controllers
		Routers routers = blade.routers();
		Map<String, Route> routes = routers.getRoutes();
		if(CollectionKit.isNotEmpty(routes)){
			Collection<Route> routesList = routes.values();
			if(CollectionKit.isNotEmpty(routesList)){
				for(Route route : routesList){
					Class<?> type = route.getTargetType();
					if(null != type && type != RouteHandler.class && null == ioc.getBean(type)){
						ioc.addBean(type);
					}
				}
			}
		}
		
		Map<String, Route> interceptors = routers.getInterceptors();
		if(CollectionKit.isNotEmpty(interceptors)){
			Collection<Route> routesList = interceptors.values();
			if(CollectionKit.isNotEmpty(routesList)){
				for(Route route : routesList){
					Class<?> type = route.getTargetType();
					if(null != type && type != RouteHandler.class && null == ioc.getBean(type)){
						ioc.addBean(type);
					}
				}
			}
		}
		
		LOGGER.info("Add Object: {}", ioc.getBeans());
	}
	
	/**
	 * Register all objects in a package
	 * 
	 * @param packageName package name
	 */
	private void registerBean(String packageName) {
		
		// Recursive scan
		boolean recursive = false; 
		if (packageName.endsWith(".*")) {
			packageName = packageName.substring(0, packageName.length() - 2);
			recursive = true;
		}
		
		// Scan package all class
		Set<Class<?>> classes = classReader.getClass(packageName, recursive);
		for (Class<?> clazz : classes) {
			Component component = clazz.getAnnotation(Component.class);
			if(null != component){
				// Register classes
				ioc.addBean(clazz);
			}
		}
	}
	
	public List<Plugin> getPlugins() {
		return plugins;
	}
	
	/**
	 * destroy
	 */
	public void destroy() {
		// Clean IOC container
		ioc.clearAll();
		for(Plugin plugin : plugins){
			plugin.destroy();
		}
	}
}