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
package com.blade.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Blade;
import com.blade.kit.Assert;
import com.blade.kit.Environment;
import com.blade.kit.StringKit;
import com.blade.mvc.view.ViewSettings;

/**
 * Blade Application Config Class
 * 
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.6.6
 *
 */
public class ApplicationConfig {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);
	
	// Storage of all routing packets
	private Set<String> routePackages = new HashSet<String>(8);

	// Store all IOC packages
	private Set<String> iocPackages = new HashSet<String>(8);

	// Strore all config packages
	private Set<String> configPackages = new HashSet<String>(2);

	// Store all filter directories
	private Set<String> staticFolders = new HashSet<String>(5);
	
	// Base package
	private String basePackage;
	
	// Interceptor package
	private String interceptorPackage;

	// Encoding
	private String encoding = "utf-8";

	// web root path
	private String webRoot = "";
	
	// Is dev mode
	private boolean isDev = true;
	
	private boolean isInit  = false;
	
	private Class<?> applicationClass;

	public ApplicationConfig() {
		this.addResources("/public", "/assets", "/static");
	}

	public void setEnv(Environment environment) {
		if (null != environment && !isInit) {
			this.isDev = environment.getBoolean("app.dev", true);
			
			this.addIocPackages(environment.getString("app.ioc"));
			
			ViewSettings.$().setView500(environment.getString("mvc.view.500"));
			ViewSettings.$().setView404(environment.getString("mvc.view.404"));
			this.encoding = environment.getString("mvc.http.encoding", "UTF-8");
			String statics = environment.getString("mvc.statics");
			
			String basePackage = environment.getString("app.base-package");
			Integer port = environment.getInt("server.port");

			if (null != port) {
				Blade.$().listen(port);
			}
			
			if (StringKit.isNotBlank(statics)) {
				this.addResources(statics.split(","));
			}
			
			if (StringKit.isNotBlank(basePackage) && StringKit.isBlank(basePackage)) {
				this.setBasePackage(basePackage);
			}
			isInit = true;
		}
	}

	public String[] getRoutePackages() {
		String[] routeArr = new String[routePackages.size()];
		return routePackages.toArray(routeArr);
	}

	public void addRoutePackages(String... packages) {
		if (null != packages && packages.length > 0) {
			routePackages.addAll(Arrays.asList(packages));
		}
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
		this.addConfigPackages(basePackage + ".config");
		this.addIocPackages(basePackage + ".service.*");
		this.addRoutePackages(basePackage + ".controller");
		this.setInterceptorPackage(basePackage + ".interceptor");
	}

	public String[] getIocPackages() {
		String[] iocArr = new String[iocPackages.size()];
		return iocPackages.toArray(iocArr);
	}

	public String[] getConfigPackages() {
		String[] configArr = new String[configPackages.size()];
		return configPackages.toArray(configArr);
	}

	public void addIocPackages(String... packages) {
		if (null != packages && packages.length > 0) {
			iocPackages.addAll(Arrays.asList(packages));
		}
	}

	public void addConfigPackages(String... packages) {
		if (null != packages && packages.length > 0) {
			configPackages.addAll(Arrays.asList(packages));
		}
	}

	public String getInterceptorPackage() {
		return interceptorPackage;
	}

	public void setInterceptorPackage(String interceptorPackage) {
		this.interceptorPackage = interceptorPackage;
	}

	public Set<String> getStaticFolders() {
		return staticFolders;
	}

	public void addResources(String... resources) {
		Assert.notNull(resources);
		for(String resource : resources){
			LOGGER.debug("Add Resource: {}", resource);
		}
		staticFolders.addAll(Arrays.asList(resources));
	}

	public String getWebRoot() {
		return webRoot;
	}

	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}

	public boolean isDev() {
		return isDev;
	}

	public void setDev(boolean isDev) {
		this.isDev = isDev;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isInit(){
		return this.isInit;
	}

	public Class<?> getApplicationClass() {
		return applicationClass;
	}

	public void setApplicationClass(Class<?> applicationClass) {
		this.applicationClass = applicationClass;
	}
	
}
