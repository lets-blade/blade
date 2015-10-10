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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.blade.ioc.Container;
import com.blade.ioc.impl.DefaultContainer;
import com.blade.plugin.Plugin;
import com.blade.render.Render;
import com.blade.render.RenderFactory;

import blade.kit.IOKit;
import blade.kit.PropertyKit;
import blade.kit.json.JSONKit;

/**
 * Blade Core Class
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Blade {
	
	public static final String VERSION = "1.4.0-alpha";
	
	private static final Blade ME = new Blade();
	
	/**
     * 框架是否已经初始化
     */
    boolean isInit = false;
    
    /**
     * blade全局初始化对象，在web.xml中配置，必须
     */
    Bootstrap bootstrap;
    
    /**
	 * 全局配置对象
	 */
	protected Config config = new Config();
	
    /**
     * IOC容器，存储路由到ioc中
     */
    private Container container = DefaultContainer.single();
    
    /**
     * 默认启动端口
     */
    public int DEFAULT_PORT = 9000;
    
	private Blade() {
	}
	
	public static Blade me(){
		return ME;
	}
	
	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}
	
	/**
	 * <pre>
	 * 手动注册一个对象到ioc容器中
	 * </pre>
	 * 
	 * @param object		要注册的object
	 */
	public void regObject(Object object){
		container.registBean(object);
	}
	
	/**
	 * <pre>
	 * Properties配置文件方式
	 * 文件的路径基于classpath
	 * </pre>
	 * 
	 * @param confName		配置文件路径
	 */
	public void config(String confName){
		Map<String, String> configMap = PropertyKit.getPropertyMap(confName);
		configuration(configMap);
	}
	
	/**
	 * <pre>
	 * JSON文件的配置
	 * 文件的路径基于classpath
	 * </pre>
	 * 
	 * @param jsonPath		json文件路径
	 */
	public void configJsonPath(String jsonPath){
		InputStream inputStream = Blade.class.getResourceAsStream(jsonPath);
		if(null != inputStream){
			try {
				String json = IOKit.toString(inputStream);
				Map<String, String> configMap = JSONKit.toMap(json);
				configuration(configMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <pre>
	 * JSON格式的配置
	 * </pre>
	 * 
	 * @param json		json配置
	 */
	public void configJson(String json){
		Map<String, String> configMap = JSONKit.toMap(json);
		configuration(configMap);
	}
	
	/**
	 * <pre>
	 * 根据配置map保存配置
	 * </pre>
	 * 
	 * @param configMap		存放配置的map
	 */
	private void configuration(Map<String, String> configMap){
		new Configurator(config, configMap).run();
	}
	
	/**
     * 设置路由包，如：com.baldejava.route
     * 可传入多个包，所有的路由类都在该包下
     * 
     * @param packages 	路由包路径
     */
    public void routes(String...packages){
    	if(null != packages && packages.length >0){
    		config.setRoutePackages(packages);
    	}
    }
    
    /**
     * 设置顶层包，框架自动寻找路由包和拦截器包，如：com.bladejava
     * 如上规则，会超找com.bladejava.route、com.bladejava.interceptor下的路由和拦截器
     * 
     * @param basePackage 	默认包路径
     */
    public void defaultRoute(String basePackage){
    	if(null != basePackage){
    		config.setBasePackage(basePackage);
    	}
    }
    
    /**
     * 设置拦截器所在的包路径，如：com.bladejava.interceptor
     * 
     * @param packageName 拦截器所在的包
     */
	public void interceptor(String packageName) {
		if(null != packageName && packageName.length() >0){
			config.setInterceptorPackage(packageName);
    	}
	}
	
	/**
     * 设置依赖注入包，如：com.bladejava.service
     * 
     * @param packages 	所有需要做注入的包，可传入多个
     */
    public void ioc(String...packages){
    	if(null != packages && packages.length >0){
    		config.setIocPackages(packages);
    	}
    }
    
	/**
	 * 设置渲染引擎，默认是JSP引擎
	 * 
	 * @param render 	渲染引擎对象
	 */
	public void viewEngin(Render render) {
		RenderFactory.init(render);
	}
	
	/**
	 * 设置默认视图前缀，默认为WEB_ROOT/WEB-INF目录
	 * 
	 * @param prefix 	视图路径，如：/WEB-INF/views
	 */
	public void viewPrefix(final String prefix) {
		if(null != prefix && prefix.startsWith("/")){
			config.setViewPrefix(prefix);
		}
	}
	
	/**
	 * 设置视图默认后缀名，默认为.jsp
	 * 
	 * @param viewExt	视图后缀，如：.html	 .vm
	 */
	public void viewSuffix(final String suffix) {
		if(null != suffix && suffix.startsWith(".")){
			config().setViewSuffix(suffix);
		}
	}
	
	/**
	 * 同事设置视图所在目录和视图后缀名
	 * 
	 * @param viewPath	视图路径，如：/WEB-INF/views
	 * @param viewExt	视图后缀，如：.html	 .vm
	 */
	public void view(final String viewPath, final String viewExt) {
		viewPrefix(viewPath);
		viewSuffix(viewExt);
	}
	
	/**
	 * 设置框架静态文件所在文件夹
	 * 
	 * @param folders
	 */
	public void staticFolder(final String ... folders) {
		config.setStaticFolders(folders);
	}
	
    /**
     * 动态设置全局初始化类
     * 
     * @param bladeApplication 	全局初始化bladeApplication
     */
    public <T> void app(Bootstrap bootstrap){
    	this.bootstrap = bootstrap;
    }
    
    /**
     * 设置404视图页面
     * 
     * @param view404	404视图页面
     */
    public void view404(final String view404){
    	config().setView404(view404);
    }
    
    /**
     * 设置500视图页面
     * 
     * @param view500	500视图页面
     */
    public void view500(final String view500){
    	config.setView500(view500);
    }

    /**
     * 设置web根目录
     * 
     * @param webRoot	web根目录物理路径
     */
    public void webRoot(final String webRoot){
    	config.setWebRoot(webRoot);
    }
    
    /**
	 * 设置系统是否以debug方式运行
	 * @param isdebug	true:是，默认true；false:否
	 */
	public void debug(boolean isdebug){
		config.setDebug(isdebug);
	}
	
	public Blade listen(int port){
		DEFAULT_PORT = port;
		return this;
	}
	
	public void start() {
		
	}
	
	public Config config(){
    	return config();
    }
	
    /**
     * @return	返回Blade要扫描的基础包
     */
    public String basePackage(){
    	return config.getBasePackage();
    }
    
	/**
     * @return	返回路由包数组
     */
    public String[] routes(){
    	return config.getRoutePackages();
    }
    
    /**
     * @return	返回IOC所有包
     */
    public String[] iocs(){
    	return config.getIocPackages();
    }
    
    /**
     * @return	返回拦截器包数组，只有一个元素 这里统一用String[]
     */
    public String interceptor(){
    	return config.getInterceptorPackage();
    }
    
    
    /**
     * @return	返回视图存放路径
     */
    public String viewPrefix(){
    	return config.getViewPrefix();
    }
    
    /**
     * @return	返回系统默认字符编码
     */
    public String encoding(){
    	return config.getEncoding();
    }
    
    /**
     * @return	返回balde启动端口
     */
    public String viewSuffix(){
    	return config.getViewSuffix();
    }
    
    /**
     * @return	返回404视图
     */
    public String view404(){
    	return config.getView404();
    }
    
    /**
     * @return	返回500视图
     */
    public String view500(){
    	return config.getView500();
    }
    
    /**
     * @return	返回webroot路径
     */
    public String webRoot(){
    	return config.getWebRoot();
    }
    
    /**
	 * @return	返回系统是否以debug方式运行
	 */
	public boolean debug(){
		return config.isDebug();
	}
	
	/**
	 * @return	返回静态资源目录
	 */
	public String[] staticFolder(){
		return config.getStaticFolders();
	}
	
	/**
	 * @return	返回Bootstrap对象
	 */
	public Bootstrap bootstrap(){
		return bootstrap; 
	}
	
	/**
	 * 返回插件对象
	 * @param pluginClazz	插件class
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Plugin> T plugin(Class<T> pluginClazz){
		Object object = IocApplication.getPlugin(pluginClazz);
		if(null == object){
			object = IocApplication.registerPlugin(pluginClazz);
		}
		return (T) object;
	}
	
}
