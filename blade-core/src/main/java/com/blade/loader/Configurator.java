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
package com.blade.loader;

import java.util.Map;

import blade.kit.Assert;
import blade.kit.CollectionKit;
import blade.kit.StringKit;
import blade.kit.config.Config;

/**
 * Blade configuration
 * 
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
public final class Configurator {
	
	public static void init(BladeConfig bladeConfig, Config config) {
		
		Assert.notNull(bladeConfig);
		Assert.notNull(config);
		
		Boolean dev = config.getBoolean("blade.dev");
		Boolean httpCache = config.getBoolean("blade.http.cache");
		Boolean httpXss = config.getBoolean("blade.http.xss");
		
		String httpEncoding = config.getString("blade.http.encoding");
		String httpFilters = config.getString("blade.http.filters");
		String basePackage = config.getString("blade.basepackage");
		String iocPackage = config.getString("blade.ioc");
		String view500 = config.getString("blade.view.500");
		String view404 = config.getString("blade.view.404");
		
		if(null != dev){
			bladeConfig.setDev(dev);
		}
		
		if(null != httpCache){
			bladeConfig.setHttpCache(httpCache);
		}
		
		if(StringKit.isNotBlank(httpEncoding)){
			bladeConfig.setEncoding(httpEncoding);
		}
		
		if(StringKit.isNotBlank(iocPackage)){
			bladeConfig.addIocPackages(iocPackage);
		}
		
		if(null != httpXss){
			bladeConfig.setHttpXss(httpXss);
		}
		
		if(StringKit.isNotBlank(httpFilters)){
			bladeConfig.setStaticFolders(httpFilters.split(","));
		}
		
		if(StringKit.isNotBlank(view404)){
			bladeConfig.setView404(view404);
		}
		
		if(StringKit.isNotBlank(view500)){
			bladeConfig.setView500(view500);
		}
		
		if(StringKit.isNotBlank(basePackage)){
			bladeConfig.setBasePackage(basePackage);
	    	bladeConfig.addIocPackages(basePackage + ".service.*");
	    	bladeConfig.addRoutePackages(basePackage + ".controller");
	    	bladeConfig.setInterceptorPackage(basePackage + ".interceptor");
		}
		
		Map<String, String> configMap = config.getConfigMap();
		if(CollectionKit.isNotEmpty(configMap)){
			bladeConfig.setConfigMap(configMap);
		}
	}
	
}