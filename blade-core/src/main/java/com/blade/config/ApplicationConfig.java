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

import static com.blade.Blade.$;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Const;
import com.blade.context.DynamicContext;
import com.blade.kit.Assert;
import com.blade.kit.StringKit;
import com.blade.kit.base.Config;
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
	
	private Packages packages;
	
	// Encoding
	private String encoding = "utf-8";
	
	// Is dev mode
	private boolean isDev = true;
	
	private boolean isInit  = false;
	
	private String webRoot;
	
	private Class<?> applicationClass;

	public ApplicationConfig() {
		this.packages = new Packages();
		this.addResources("/public", "/assets", "/static");
	}

	public void setEnv(Config config) {
		if (null != config && !isInit) {
			
			// get dev mode
			this.isDev = config.getBoolean("app.dev", true);
			
			// get ioc packages
			packages.put(Const.IOC_PKGS, config.get("app.ioc"));
			
			// get view 404, 500 page
			ViewSettings.$().setView500(config.get("mvc.view.500"));
			ViewSettings.$().setView404(config.get("mvc.view.404"));
			
			// get http encoding
			this.encoding = config.get("http.encoding", "UTF-8");
			
			// get mvc static folders
			String statics = config.get("mvc.statics");
			
			// get app base package
			String basePackage = config.get("app.base-package");
			
			// get server start port
			Integer port = config.getInt("server.port", Const.DEFAULT_PORT);
			$().listen(port);
			
			if (StringKit.isNotBlank(statics)) {
				this.addResources(StringKit.split(statics, ','));
			}
			
			if (StringKit.isNotBlank(basePackage)) {
				this.setBasePackage(basePackage);
			}
			isInit = true;
		}
	}
	
	public void addRoutePkgs(String... pkgs){
		packages.add(Const.ROUTE_PKGS, pkgs);
	}
	
	public void addIocPkgs(String... pkgs){
		packages.add(Const.IOC_PKGS, pkgs);
	}
	
	public String getBasePackage() {
		return packages.first(Const.BASE_PKG);
	}

	public String[] getConfigPkgs(){
		return packages.array(Const.CONFIG_PKGS);
	}
	
	public String[] getIocPkgs(){
		return packages.array(Const.IOC_PKGS);
	}
	
	public String[] getRoutePkgs(){
		return packages.array(Const.ROUTE_PKGS);
	}
	
	public Set<String> getResources(){
		return packages.values(Const.RESOURCE_PKGS);
	}
	
	public String getInterceptorPkg(){
		return packages.first(Const.INTERCEPTOR_PKG);
	}
	
	public String getFilterPkg(){
		return packages.first(Const.FILTER_PKG);
	}
	
	public String getListenerPkg(){
		return packages.first(Const.LISTENER_PKG);
	}
	
	public boolean isDev() {
		return isDev;
	}
	
	public String getEncoding() {
		return encoding;
	}

	public boolean isInit(){
		return this.isInit;
	}

	public String webRoot() {
		return this.webRoot;
	}
	
	public Class<?> getApplicationClass() {
		return applicationClass;
	}
	
	public void setApplicationClass(Class<?> applicationClass) {
		this.applicationClass = applicationClass;
		DynamicContext.init(applicationClass);
	}
	
	public void  setInterceptorPackage(String interceptorPkg) {
		packages.put(Const.INTERCEPTOR_PKG, interceptorPkg);
	}
	
	public void setBasePackage(String basePackage) {
		Assert.notBlank(basePackage);
		
		packages.put(Const.BASE_PKG, basePackage);
		packages.put(Const.INTERCEPTOR_PKG, basePackage + ".interceptor");
		packages.put(Const.FILTER_PKG, basePackage + ".filter");
		packages.put(Const.LISTENER_PKG, basePackage + ".listener");
		
		packages.add(Const.CONFIG_PKGS, basePackage + ".config");
		packages.add(Const.IOC_PKGS, basePackage + ".service.*");
		packages.add(Const.ROUTE_PKGS, basePackage + ".controller");
	}
	
	public void addResources(String... resources) {
		Assert.notNull(resources);
		for(String resource : resources){
			LOGGER.debug("Add Resource: {}", resource);
		}
		packages.add(Const.RESOURCE_PKGS, resources);
	}

	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}
	
	public void setDev(boolean isDev) {
		this.isDev = isDev;
	}
	
}
