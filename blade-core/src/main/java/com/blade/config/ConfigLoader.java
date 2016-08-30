/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
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
package com.blade.config;

import java.lang.reflect.Modifier;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Blade;
import com.blade.exception.ConfigException;
import com.blade.ioc.Ioc;
import com.blade.ioc.annotation.Component;
import com.blade.kit.CollectionKit;
import com.blade.kit.reflect.ReflectKit;
import com.blade.kit.resource.ClassReader;
import com.blade.kit.resource.DynamicClassReader;

/**
 * ConfigLoader
 * 
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
public class ConfigLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);
	
	private ClassReader classReader;
	private Ioc ioc;
	private ApplicationConfig applicationConfig;

	public ConfigLoader(Ioc ioc, ApplicationConfig applicationConfig) {
		this.ioc = ioc;
		this.classReader = DynamicClassReader.getClassReader();
		this.applicationConfig = applicationConfig;
	}

	@SuppressWarnings("unchecked")
	public void loadConfig() {
		String[] configPackages = Blade.$().config().getConfigPackages();
		if (null != configPackages && configPackages.length > 0) {
			// Scan package all class
			try {
				for (String packageName : configPackages) {
					Set<Class<?>> classes = classReader.getClassByAnnotation(packageName, Component.class, false);
					if (CollectionKit.isNotEmpty(classes)) {
						for (Class<?> clazz : classes) {
							boolean hasInterface = ReflectKit.hasInterface(clazz, BaseConfig.class);
							if(hasInterface){
								addConfig((Class<? extends BaseConfig>) clazz);
							}
						}
					}
				}
			} catch (ConfigException e) {
				LOGGER.error("load config error", e);
			}
		}
	}
	
	public void addConfig(Class<? extends BaseConfig> clazz) throws ConfigException {
		if (!Modifier.isAbstract(clazz.getModifiers())) {
			Object bean = ioc.addBean(clazz);
			BaseConfig baseConfig = (BaseConfig) bean;
			baseConfig.config(applicationConfig);
		}
	}

}
