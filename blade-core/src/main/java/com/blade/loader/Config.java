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
import java.util.List;
import java.util.Map;

import blade.kit.CollectionKit;
import blade.kit.PatternKit;

/**
 * Blade配置类
 * 
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 *
 */
public class Config {

	// 存放所有变量
	private Map<String, String> configMap = CollectionKit.newHashMap();
	
	// 存放所有路由的包
	private List<String> routePackages = CollectionKit.newArrayList();
	
	// 存放所有IOC的包
	private List<String> iocPackages = CollectionKit.newArrayList();
	
	// 存放所有过滤目录
	private List<String> staticFolders = CollectionKit.newArrayList();
	
	// 基础包
	private String basePackage;
	
	// 拦截器包
	private String interceptorPackage;
	
	// 编码
	private String encoding = "utf-8";
	
	// 视图前缀
	private String viewPrefix = "/WEB-INF/";
	
	// 视图后缀
	private String viewSuffix = ".jsp";
	
	// webroot根目录，物理路径
	private String webRoot;
	
	// 404视图位置
	private String view404;
	
	// 500视图位置
	private String view500;
	
	// 是否是DEBUG模式
	private boolean isDebug = true;
	
	// 是否开启XSS防御
	private boolean enableXSS = false;
	
	public Config() {
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
	
	public Integer getToInt(String key){
		String val = get(key);
		if(null != val && PatternKit.isNumber(val)){
			return Integer.valueOf(val);
		}
		return null;
	}
	
	public Long getToLong(String key){
		String val = get(key);
		if(null != val && PatternKit.isNumber(val)){
			return Long.valueOf(val);
		}
		return null;
	}
	
	public Boolean getToBoolean(String key){
		String val = get(key);
		if(null != val){
			return Boolean.valueOf(val);
		}
		return null;
	}
	
	public Double getToDouble(String key){
		String val = get(key);
		if(null != val){
			return Double.valueOf(val);
		}
		return null;
	}
	
	public Float getToFloat(String key){
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
	
	public void setRoutePackages(String ... packages) {
		routePackages.addAll(Arrays.asList(packages));
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

	public void setIocPackages(String ... packages) {
		iocPackages.addAll(Arrays.asList(packages));
	}

	public String getInterceptorPackage() {
		return interceptorPackage;
	}

	public void setInterceptorPackage(String interceptorPackage) {
		this.interceptorPackage = interceptorPackage;
	}

	public String getViewPrefix() {
		return viewPrefix;
	}

	public void setViewPrefix(String viewPrefix) {
		this.viewPrefix = viewPrefix;
	}

	public String getViewSuffix() {
		return viewSuffix;
	}


	public void setViewSuffix(String viewSuffix) {
		this.viewSuffix = viewSuffix;
	}

	public String[] getStaticFolders() {
		String[] folderArr = new String[staticFolders.size()];
		return staticFolders.toArray(folderArr);
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

	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isEnableXSS() {
		return enableXSS;
	}

	public void setEnableXSS(boolean enableXSS) {
		this.enableXSS = enableXSS;
	}

}
