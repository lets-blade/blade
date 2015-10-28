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
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.blade.http.HttpMethod;
import com.blade.ioc.Container;
import com.blade.ioc.SampleContainer;
import com.blade.loader.ClassPathRoutesLoader;
import com.blade.loader.Config;
import com.blade.loader.Configurator;
import com.blade.plugin.Plugin;
import com.blade.render.JspRender;
import com.blade.render.Render;
import com.blade.route.Route;
import com.blade.route.RouteHandler;
import com.blade.route.Router;
import com.blade.route.RoutesException;
import com.blade.server.Server;

import blade.kit.IOKit;
import blade.kit.PropertyKit;
import blade.kit.ReflectKit;
import blade.kit.json.JSONKit;

/**
 * Blade Core Class
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Blade {
	
	public static final String VERSION = "1.4.1-alpha";
	
	private static final Blade ME = new Blade();
	
	/**
     * 框架是否已经初始化
     */
    boolean isInit = false;
    
    /**
     * blade全局初始化对象，在web.xml中配置，必须
     */
    private Bootstrap bootstrap = new Bootstrap() {
		@Override
		public void init() {
		}
	};
    
    /**
	 * 全局配置对象
	 */
	private Config config = new Config();
	
    /**
     * IOC容器，存储路由到ioc中
     */
    private Container container = SampleContainer.single();
    
    /**
     * 默认JSP渲染
     */
    private Render render = new JspRender();
    
    private Router router = new Router();
    
    /**
     * 默认启动端口
     */
    private static final int DEFAULT_PORT = 9000;
    
    private int port = DEFAULT_PORT;
    
    
	private Blade() {
	}
	
	public static Blade me(){
		return ME;
	}
	
	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}
	
	public Server createServer(int port){
		return new Server(port);
	}
	
	public Router router() {
		return router;
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
	public void appConf(String confName){
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
	public void appJsonPath(String jsonPath){
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
	public void appJson(String json){
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
    public Blade routes(String...packages){
    	if(null != packages && packages.length >0){
    		config.setRoutePackages(packages);
    	}
    	return this;
    }
    
    /**
     * 设置顶层包，框架自动寻找路由包和拦截器包，如：com.bladejava
     * 如上规则，会超找com.bladejava.route、com.bladejava.interceptor下的路由和拦截器
     * 
     * @param basePackage 	默认包路径
     */
    public Blade defaultRoute(String basePackage){
    	if(null != basePackage){
    		config.setBasePackage(basePackage);
    	}
    	return this;
    }
    
    /**
     * 设置拦截器所在的包路径，如：com.bladejava.interceptor
     * 
     * @param packageName 拦截器所在的包
     */
	public Blade interceptor(String packageName) {
		if(null != packageName && packageName.length() >0){
			config.setInterceptorPackage(packageName);
    	}
		return this;
	}
	
	/**
     * 设置依赖注入包，如：com.bladejava.service
     * 
     * @param packages 	所有需要做注入的包，可传入多个
     */
    public Blade ioc(String...packages){
    	if(null != packages && packages.length >0){
    		config.setIocPackages(packages);
    	}
    	return this;
    }
    
	public Blade route(String path, Object target, String method){
		router.route(path, target, method);
		return this;
	}
	
	public Blade addRoute(String path, Object target, String method){
		return route(path, target, method);
	}
	
	public Blade addRoute(String path, Object target, String method, HttpMethod httpMethod){
		return route(path, target, method, httpMethod);
	}
	
	/**
	 * 注册一个函数式的路由</br>
	 * <p>
	 * 方法上指定请求类型，如：post:signin
	 * </p>
	 * @param path			路由url	
	 * @param clazz			路由处理类
	 * @param methodName	路由处理方法名称
	 */
	public Blade route(String path, Class<?> clazz, String method){
		router.route(path, clazz, method);
		return this;
	}
	
	/**
	 * 注册一个函数式的路由
	 * @param path			路由url	
	 * @param clazz			路由处理类
	 * @param methodName	路由处理方法名称
	 * @param httpMethod	请求类型,GET/POST
	 */
	public Blade route(String path, Class<?> clazz, String method, HttpMethod httpMethod){
		router.route(path, clazz, method, httpMethod);
		return this;
	}
	
	public Blade route(String path, Object target, String method, HttpMethod httpMethod){
		router.route(path, target, method, httpMethod);
		return this;
	}
	
	/**
	 * get请求
	 * @param path
	 * @param handler
	 */
	public Blade get(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.GET);
		return this;
	}
	
	
	/**
	 * post请求
	 * @param path
	 * @param handler
	 */
	public Blade post(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.POST);
		return this;
	}
	
	
	/**
	 * delete请求
	 * @param path
	 * @param handler
	 */
	public Blade delete(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.DELETE);
		return this;
	}
	
	
	/**
	 * put请求
	 * @param paths
	 */
	public Blade put(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.PUT);
		return this;
	}
	
	/**
	 * patch请求
	 * @param path
	 * @param handler
	 */
	public Blade patch(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.PATCH);
		return this;
	}

	/**
	 * head请求
	 * @param path
	 * @param handler
	 */
	public Blade head(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.HEAD);
		return this;
	}
	
	/**
	 * trace请求
	 * @param path
	 * @param handler
	 */
	public Blade trace(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.TRACE);
		return this;
	}
	
	/**
	 * options请求
	 * @param path
	 * @param handler
	 */
	public Blade options(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.OPTIONS);
		return this;
	}
	
	/**
	 * connect请求
	 * @param path
	 * @param handler
	 */
	public Blade connect(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.CONNECT);
		return this;
	}
	
	/**
	 * 任意请求
	 * @param path
	 * @param handler
	 */
	public Blade all(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.ALL);
		return this;
	}
	
	/**
	 * 拦截器before请求
	 * @param path
	 * @param routeHandler
	 */
	public Blade before(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.BEFORE);
		return this;
	}

	/**
	 * 拦截器after请求
	 * @param path
	 * @param routeHandler
	 */
	public Blade after(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.AFTER);
		return this;
	}
	
	/**
	 * 设置渲染引擎，默认是JSP引擎
	 * 
	 * @param render 	渲染引擎对象
	 */
	public Blade viewEngin(Render render) {
		this.render = render;
		return this;
	}
	
	/**
	 * 设置默认视图前缀，默认为WEB_ROOT/WEB-INF目录
	 * 
	 * @param prefix 	视图路径，如：/WEB-INF/views
	 */
	public Blade viewPrefix(final String prefix) {
		if(null != prefix && prefix.startsWith("/")){
			config.setViewPrefix(prefix);
		}
		return this;
	}
	
	/**
	 * 设置视图默认后缀名，默认为.jsp
	 * 
	 * @param viewExt	视图后缀，如：.html	 .vm
	 */
	public Blade viewSuffix(final String suffix) {
		if(null != suffix && suffix.startsWith(".")){
			config.setViewSuffix(suffix);
		}
		return this;
	}
	
	/**
	 * 同事设置视图所在目录和视图后缀名
	 * 
	 * @param viewPath	视图路径，如：/WEB-INF/views
	 * @param viewExt	视图后缀，如：.html	 .vm
	 */
	public Blade view(final String viewPath, final String viewExt) {
		viewPrefix(viewPath);
		viewSuffix(viewExt);
		return this;
	}
	
	/**
	 * 设置框架静态文件所在文件夹
	 * 
	 * @param folders
	 */
	public Blade staticFolder(final String ... folders) {
		config.setStaticFolders(folders);
		return this;
	}
	
	/**
	 * 设置是否启用XSS防御
	 * @param enableXSS
	 * @return
	 */
	public Blade enableXSS(boolean enableXSS){
		config.setEnableXSS(enableXSS);
		return this; 
	}
	
    /**
     * 动态设置全局初始化类
     * 
     * @param bootstrap 	全局初始化bladeApplication
     */
    public <T> Blade app(Bootstrap bootstrap){
    	this.bootstrap = bootstrap;
    	return this;
    }
    
    /**
     * 动态设置全局初始化类
     * 
     * @param bootstrap 	全局初始化bladeApplication
     */
    public <T> Blade app(Class<? extends Bootstrap> bootstrap){
    	this.bootstrap = (Bootstrap) ReflectKit.newInstance(bootstrap);
    	return this;
    }
    
    /**
     * 设置404视图页面
     * 
     * @param view404	404视图页面
     */
    public Blade view404(final String view404){
    	config.setView404(view404);
    	return this;
    }
    
    /**
     * 设置500视图页面
     * 
     * @param view500	500视图页面
     */
    public Blade view500(final String view500){
    	config.setView500(view500);
    	return this;
    }

    /**
     * 设置web根目录
     * 
     * @param webRoot	web根目录物理路径
     */
    public Blade webRoot(final String webRoot){
    	config.setWebRoot(webRoot);
    	return this;
    }
    
    /**
	 * 设置系统是否以debug方式运行
	 * @param isdebug	true:是，默认true；false:否
	 */
	public Blade debug(boolean isdebug){
		config.setDebug(isdebug);
		return this;
	}
	
	public Blade listen(int port){
		this.port = port;
		return this;
	}
	
	public void start(String contextPath) {
		try {
			Server bladeServer = new Server(this.port);
			bladeServer.run(contextPath);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	    
	}
	
	public void start() {
		this.start("/");
	}
	
	public void handle(){
		
	}
	
	public Config config(){
    	return config;
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
    public String[] routePackages(){
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
    public String interceptorPackage(){
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
	
	public Render render() {
		return render;
	}

	/**
	 * @return	返回是否启用XSS防御
	 */
	public boolean enableXSS(){
		return config.isEnableXSS(); 
	}
	
	/**
	 * 返回插件对象
	 * @param pluginClazz	插件class
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T plugin(Class<? extends Plugin> pluginClazz){
		Object object = IocApplication.getPlugin(pluginClazz);
		if(null == object){
			object = IocApplication.registerPlugin(pluginClazz);
		}
		return (T) object;
	}

	public Blade routeConf(String basePackage, String conf) {
		try {
			InputStream ins = Blade.class.getResourceAsStream("/" + conf);
			ClassPathRoutesLoader routesLoader = new ClassPathRoutesLoader(ins);
			routesLoader.setBasePackage(basePackage);
			List<Route> routes = routesLoader.load();
			router.addRoutes(routes);
		} catch (RoutesException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return this;
	}
}
