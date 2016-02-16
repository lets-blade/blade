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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import blade.kit.PatternKit;
import blade.kit.config.Config;
import blade.kit.config.loader.ConfigLoader;

/**
 * Blade Config Class
 * 
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 *
 */
public class BladeConfig {

	// Store all variables 
	private Map<String, String> configMap;
	
	// Storage of all routing packets 
	private Set<String> routePackages = new HashSet<String>();
	
	// Store all IOC packages 
	private Set<String> iocPackages = new HashSet<String>();
	
	// Store all filter directories 
	private Set<String> staticFolders = new HashSet<String>();
	
	// Base package
	private String basePackage;
	
	// Interceptor package
	private String interceptorPackage;
	
	// Encoding
	private String encoding = "utf-8";
	
	// web root path
	private String webRoot;
	
	// 404 view page
	private String view404;
	
	// 500 view page
	private String view500;
	
	// Is dev mode
	private boolean isDev = true;
	
	// Enabled XSS
	private boolean httpXss = false;
	
	private boolean httpCache = false;
	
	private boolean configInit = false;
	
	public BladeConfig() {
		staticFolders.add("/public");
		staticFolders.add("/assets");
		staticFolders.add("/static");
	}
	
	public void init(){
		if(!configInit){
			try {
				Config config = ConfigLoader.load("blade.properties");
				if(null != config){
					Configurator.init(this, config);
				}
			} catch (Exception e) {
			}
		}
	}
	
	public Map<String, String> getConfigMap() {
		return configMap;
	}

	public void setConfigMap(Map<String, String> configMap) {
		this.configMap = configMap;
	}
	
	public String get(String key){
		return configMap.get(key);
	}
	
	public Integer getAsInt(String key){
		String val = get(key);
		if(null != val && PatternKit.isNumber(val)){
			return Integer.valueOf(val);
		}
		return null;
	}
	
	public Long getAsLong(String key){
		String val = get(key);
		if(null != val && PatternKit.isNumber(val)){
			return Long.valueOf(val);
		}
		return null;
	}
	
	public Boolean getAsBoolean(String key){
		String val = get(key);
		if(null != val){
			return Boolean.valueOf(val);
		}
		return null;
	}
	
	public Double getAsDouble(String key){
		String val = get(key);
		if(null != val){
			return Double.valueOf(val);
		}
		return null;
	}
	
	public Float getAsFloat(String key){
		String val = get(key);
		if(null != val){
			return Float.valueOf(val);
		}
		return null;
	}

	public String[] getRoutePackages() {
		String[] routeArr = new String[routePackages.size()];
		return routePackages.toArray(routeArr);
	}
	
	public void addRoutePackages(String ... packages) {
		if(null != packages && packages.length > 0){
			routePackages.addAll(Arrays.asList(packages));
		}
	}
	
	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String[] getIocPackages() {
		String[] iocArr = new String[iocPackages.size()];
		return iocPackages.toArray(iocArr);
	}

	public void addIocPackages(String ... packages) {
		if(null != packages && packages.length > 0){
			iocPackages.addAll(Arrays.asList(packages));
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
	
	public void setStaticFolders(String ... packages) {
		staticFolders.addAll(Arrays.asList(packages));
	}

	public String getView404() {
		return view404;
	}

	public void setView404(String view404) {
		this.view404 = view404;
	}

	public String getView500() {
		return view500;
	}

	public void setView500(String view500) {
		this.view500 = view500;
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

	public boolean isHttpXss() {
		return httpXss;
	}

	public void setHttpXss(boolean httpXss) {
		this.httpXss = httpXss;
	}

	public boolean isHttpCache() {
		return httpCache;
	}

	public void setHttpCache(boolean httpCache) {
		this.httpCache = httpCache;
	}
	
}
