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
package blade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Blade配置类
 * 
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 *
 */
public class BladeConfig {

	private List<String> routePackages = new ArrayList<String>();
	private List<String> iocPackages = new ArrayList<String>();
	private List<String> staticFolders = new ArrayList<String>();
	private String basePackage;
	private String interceptorPackage;
	private String encoding = "utf-8";
	private String viewPrefix = "/WEB-INF/";
	private String viewSuffix = ".jsp";
	private String webRoot;
	private String view404;
	private String view500;
	private String dbUrl;
	private String dbDriver;
	private String dbUser;
	private String dbPass;
	private boolean isOpenCache = false;
	private boolean isDebug = false;

	public BladeConfig() {
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

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPass() {
		return dbPass;
	}

	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}

	public boolean isOpenCache() {
		return isOpenCache;
	}

	public void setOpenCache(boolean isOpenCache) {
		this.isOpenCache = isOpenCache;
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
	
}
