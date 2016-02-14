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
package com.blade;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.ioc.Ioc;
import com.blade.plugin.Plugin;

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
	
	/**
	 * Plugin List
	 */
	private List<Plugin> plugins = null;
	
	public IocApplication(Ioc ioc) {
		this.classReader = new ClassPathClassReader();
		this.plugins = new ArrayList<Plugin>();
		this.ioc = ioc;
	}
	
	/**
	 * IOC initialize
	 * @param iocs		ioc packages
	 * @param bootstrap	bootstrap object
	 */
	public void init(String[] iocs, Bootstrap bootstrap){
		
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
		
		// Initialization injection
//		container.initWired();
		
		Set<String> names = ioc.getBeanNames();
		for(String name : names){
			LOGGER.info("Add Object：{}={}", name, ioc.getBean(name));
		}
		
	}
	
	public <T extends Plugin> T registerPlugin(Class<T> plugin){
		ioc.addBean(plugin);
		T t = ioc.getBean(plugin);
		plugins.add(t);
		return t;
	}

	public <T extends Plugin> T getPlugin(Class<T> plugin){
		if(null != plugin && null != ioc){
			return ioc.getBean(plugin);
		}
		return null;
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
			// Register classes
			ioc.addBean(clazz);
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