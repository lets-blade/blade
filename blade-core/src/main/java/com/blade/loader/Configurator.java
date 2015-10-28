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

import blade.kit.StringKit;

/**
 * Blade配置文件注入
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Configurator {

	// 配置文件对象
	private Config bladeConfig;
	
	// 配置map
	private Map<String, String> configMap;
	
	public Configurator(Config bladeConfig,
			Map<String, String> configMap) {
		this.bladeConfig = bladeConfig;
		this.configMap = configMap;
	}
	
	private static final String BLADE_ROUTE = "blade.route";
	private static final String BLADE_INTERCEPTOR = "blade.interceptor";
	private static final String BLADE_IOC = "blade.ioc";
	private static final String BLADE_PREFIX = "blade.prefix";
	private static final String BLADE_SUFFIX = "blade.suffix";
	private static final String BLADE_FILTER_FOLDER = "blade.filter_folder";
	private static final String BLADE_ENCODING = "blade.encoding";
	private static final String BLADE_VIEW_404 = "blade.view404";
	private static final String BLADE_VIEW_500 = "blade.view500";
	private static final String BLADE_DEBUG = "blade.debug";
	private static final String BLADE_ENABLEXSS = "blade.enableXSS";
	
	public void run() {
		
		if (null != configMap && configMap.size() > 0) {
			
			bladeConfig.setConfigMap(configMap);
			
			String route = configMap.get(BLADE_ROUTE);
			String interceptor = configMap.get(BLADE_INTERCEPTOR);
			String ioc = configMap.get(BLADE_IOC);
			String prefix = configMap.get(BLADE_PREFIX);
			String suffix = configMap.get(BLADE_SUFFIX);
			String filter_folder = configMap.get(BLADE_FILTER_FOLDER);
			String encoding = configMap.get(BLADE_ENCODING);
			String view404 = configMap.get(BLADE_VIEW_404);
			String view500 = configMap.get(BLADE_VIEW_500);
			String debug = configMap.get(BLADE_DEBUG);
			String xss = configMap.get(BLADE_ENABLEXSS);
			
			if (StringKit.isNotBlank(route)) {
				String[] blade_routes = StringKit.split(route, ",");
				bladeConfig.setRoutePackages(blade_routes);
			}
			
			if (StringKit.isNotBlank(filter_folder)) {
				String[] blade_filter_folders = StringKit.split(filter_folder, ",");
				bladeConfig.setStaticFolders(blade_filter_folders);
			}
			
			if (StringKit.isNotBlank(interceptor)) {
				bladeConfig.setInterceptorPackage(interceptor);
			}
			
			if (StringKit.isNotBlank(ioc)) {
				String[] blade_iocs = StringKit.split(ioc, ",");
				bladeConfig.setIocPackages(blade_iocs);
			}
			
			if (StringKit.isNotBlank(prefix)) {
				bladeConfig.setViewPrefix(prefix);
			}
			
			if (StringKit.isNotBlank(suffix)) {
				bladeConfig.setViewSuffix(suffix);
			}
			
			if (StringKit.isNotBlank(encoding)) {
				bladeConfig.setEncoding(encoding);
			}
			
			if (StringKit.isNotBlank(view404)) {
				bladeConfig.setView404(view404);
			}
			
			if (StringKit.isNotBlank(view500)) {
				bladeConfig.setView500(view500);
			}
			
			if (StringKit.isNotBlank(debug)) {
				Boolean debugBool = Boolean.valueOf(debug);
				bladeConfig.setDebug(debugBool);
			}
			
			if (StringKit.isNotBlank(xss)) {
				Boolean enableXssBool = Boolean.valueOf(xss);
				bladeConfig.setEnableXSS(enableXssBool);
			}
		}
	}
}