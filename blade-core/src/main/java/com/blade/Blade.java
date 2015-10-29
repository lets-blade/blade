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
import com.blade.loader.ClassPathRouteLoader;
import com.blade.loader.Config;
import com.blade.loader.Configurator;
import com.blade.plugin.Plugin;
import com.blade.render.JspRender;
import com.blade.render.Render;
import com.blade.route.Route;
import com.blade.route.RouteHandler;
import com.blade.route.Router;
import com.blade.route.RouteException;
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
	
	/**
	 * 当前最新版本
	 */
	public static final String VERSION = "1.4.1-alpha";
	
	/**
	 * Blade单例
	 */
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
    
    /**
     * 路由管理对象
     */
    private Router router = new Router();
    
    /**
     * 默认启动端口
     */
    private static final int DEFAULT_PORT = 9000;
    
    /**
     * blade启动端口，jetty服务
     */
    private int port = DEFAULT_PORT;
    
    /**
     * jetty服务
     */
    private Server bladeServer;
    
	private Blade() {
	}
	
	/**
	 * @return	单例方式返回Blade对象
	 */
	public static Blade me(){
		return ME;
	}
	
	/**
	 * 设置App是否已经初始化
	 * @param isInit	是否初始化
	 */
	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}
	
	/**
	 * 创建一个Jetty服务
	 * @param port		服务端口
	 * @return			返回Blade封装的Server
	 */
	public Server createServer(int port){
		return new Server(port);
	}
	
	/**
	 * @return		返回路由管理对象
	 */
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
	public void setAppConf(String confName){
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
	public void setJsonConf(String jsonPath){
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
	public void setAppJson(String json){
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
    
    /**
     * 添加一个路由
     * 
     * @param path		路由路径
     * @param target	路由执行的目标对象
     * @param method	路由执行的方法名称（同时指定HttpMethod的方式是：post:saveUser，如不指定则为HttpMethod.ALL）
     * @return			返回Blade单例实例
     */
	public Blade route(String path, Object target, String method){
		router.route(path, target, method);
		return this;
	}
	
	/**
     * 添加一个路由
     * 
     * @param path		路由路径
     * @param target	路由执行的目标对象
     * @param method	路由执行的方法名称（同时指定HttpMethod的方式是：post:saveUser，如不指定则为HttpMethod.ALL）
     * @return			返回Blade单例实例
     */
	@Deprecated
	public Blade addRoute(String path, Object target, String method){
		return route(path, target, method);
	}
	
	/**
     * 添加一个路由
     * 
     * @param path		路由路径
     * @param target	路由执行的目标对象
     * @param method	路由执行的方法名称（同时指定HttpMethod的方式是：post:saveUser，如不指定则为HttpMethod.ALL）
     * @param httpMethod
     * @return			返回Blade单例实例
     */
	@Deprecated
	public Blade addRoute(String path, Object target, String method, HttpMethod httpMethod){
		return route(path, target, method, httpMethod);
	}
	
	/**
     * 添加一个路由
     * 
     * @param path		路由路径
     * @param target	路由执行的目标对象
     * @param method	路由执行的方法名称（同时指定HttpMethod的方式是：post:saveUser，如不指定则为HttpMethod.ALL）
     * @param httpMethod
     * @return			返回Blade单例实例
     */
	public Blade route(String path, Object target, String method, HttpMethod httpMethod){
		router.route(path, target, method, httpMethod);
		return this;
	}
	
	/**
	 * 注册一个函数式的路由
	 * 方法上指定请求类型（同时指定HttpMethod的方式是：post:saveUser，如不指定则为HttpMethod.ALL）
	 * 
	 * @param path			路由url	
	 * @param clazz			路由处理类
	 * @param method		路由处理方法名称
	 * @return Blade		返回Blade单例实例
	 */
	public Blade route(String path, Class<?> clazz, String method){
		router.route(path, clazz, method);
		return this;
	}
	
	/**
	 * 注册一个函数式的路由
	 * 
	 * @param path			路由url	
	 * @param clazz			路由处理类
	 * @param methodName	路由处理方法名称
	 * @param httpMethod	请求类型,GET/POST
	 * @return Blade		返回Blade单例实例
	 */
	public Blade route(String path, Class<?> clazz, String method, HttpMethod httpMethod){
		router.route(path, clazz, method, httpMethod);
		return this;
	}
	
	/**
	 * 注册一个GET请求的路由
	 * 
	 * @param path		路由路径
	 * @param handler	执行路由的Handle
	 * @return			返回Blade单例实例
	 */
	public Blade get(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.GET);
		return this;
	}
	
	/**
	 * 注册一个POST请求的路由
	 * 
	 * @param path		路由路径
	 * @param handler	执行路由的Handle
	 * @return			返回Blade单例实例
	 */
	public Blade post(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.POST);
		return this;
	}
	
	/**
	 * 注册一个DELETE请求的路由
	 * 
	 * @param path		路由路径
	 * @param handler	执行路由的Handle
	 * @return			返回Blade单例实例
	 */
	public Blade delete(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.DELETE);
		return this;
	}
	
	/**
	 * 注册一个PUT请求的路由
	 * 
	 * @param path		路由路径
	 * @param handler	执行路由的Handle
	 * @return			返回Blade单例实例
	 */
	public Blade put(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.PUT);
		return this;
	}
	
	/**
	 * 注册一个任意请求的路由
	 * 
	 * @param path		路由路径
	 * @param handler	执行路由的Handle
	 * @return			返回Blade单例实例
	 */
	public Blade all(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.ALL);
		return this;
	}
	
	/**
	 * 注册一个任意请求的路由
	 * 
	 * @param path		路由路径
	 * @param handler	执行路由的Handle
	 * @return			返回Blade单例实例
	 */
	public Blade any(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.ALL);
		return this;
	}
	
	/**
	 * 注册一个前置拦截器请求的路由
	 * 
	 * @param path		路由路径
	 * @param handler	执行路由的Handle
	 * @return			返回Blade单例实例
	 */
	public Blade before(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.BEFORE);
		return this;
	}

	/**
	 * 注册一个后置拦截器请求的路由
	 * 
	 * @param path		路由路径
	 * @param handler	执行路由的Handle
	 * @return			返回Blade单例实例
	 */
	public Blade after(String path, RouteHandler handler){
		router.route(path, handler, HttpMethod.AFTER);
		return this;
	}
	
	/**
	 * 设置渲染引擎，默认是JSP引擎
	 * 
	 * @param render 	渲染引擎对象
	 * @return			返回Blade单例实例
	 */
	public Blade viewEngin(Render render) {
		this.render = render;
		return this;
	}
	
	/**
	 * 设置默认视图前缀，默认为WEB_ROOT/WEB-INF目录
	 * 
	 * @param prefix 	视图路径，如：/WEB-INF/views
	 * @return			返回Blade单例实例
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
	 * @return			返回Blade单例实例
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
	 * @return			返回Blade单例实例
	 */
	public Blade view(final String viewPath, final String viewExt) {
		viewPrefix(viewPath);
		viewSuffix(viewExt);
		return this;
	}
	
	/**
	 * 设置框架静态文件所在文件夹
	 * 
	 * @param folders	要过滤的目录数组，如："/public,/static,/images"
	 * @return			返回Blade单例实例
	 */
	public Blade staticFolder(final String ... folders) {
		config.setStaticFolders(folders);
		return this;
	}
	
	/**
	 * 设置是否启用XSS防御
	 * 
	 * @param enableXSS	是否启用XSS防御，默认不启用
	 * @return			返回Blade单例实例
	 */
	public Blade enableXSS(boolean enableXSS){
		config.setEnableXSS(enableXSS);
		return this; 
	}
	
    /**
     * 动态设置全局初始化类
     * 
     * @param bootstrap 	全局初始化bladeApplication
     * @return				返回Blade单例实例
     */
    public <T> Blade app(Bootstrap bootstrap){
    	this.bootstrap = bootstrap;
    	return this;
    }
    
    /**
     * 动态设置全局初始化类
     * 
     * @param bootstrap 	全局初始化bladeApplication
     * @return				返回Blade单例实例
     */
    public <T> Blade app(Class<? extends Bootstrap> bootstrap){
    	this.bootstrap = (Bootstrap) ReflectKit.newInstance(bootstrap);
    	return this;
    }
    
    /**
     * 设置404视图页面
     * 
     * @param view404	404视图页面
     * @return			返回Blade单例实例
     */
    public Blade setView404(final String view404){
    	config.setView404(view404);
    	return this;
    }
    
    /**
     * 设置500视图页面
     * 
     * @param view500	500视图页面
     * @return			返回Blade单例实例
     */
    public Blade setView500(final String view500){
    	config.setView500(view500);
    	return this;
    }

    /**
     * 设置web根目录
     * 
     * @param webRoot	web根目录物理路径
     * @return			返回Blade单例实例
     */
    public Blade webRoot(final String webRoot){
    	config.setWebRoot(webRoot);
    	return this;
    }
    
    /**
	 * 设置系统是否以debug方式运行
	 * 
	 * @param isdebug	true:是，默认true；false:否
	 * @return			返回Blade单例实例
	 */
	public Blade debug(boolean isdebug){
		config.setDebug(isdebug);
		return this;
	}
	
	/**
	 * 设置Jetty服务监听端口
	 * 
	 * @param port		端口，默认9000
	 * @return			返回Blade单例实例
	 */
	public Blade listen(int port){
		this.port = port;
		return this;
	}
	
	/**
	 * 设置jetty启动上下文
	 * 
	 * @param contextPath	设置上下文contextPath，默认/
	 * @return				返回Blade单例实例
	 */
	public void start(String contextPath) {
		try {
			bladeServer = new Server(this.port);
			bladeServer.start(contextPath);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	    
	}
	
	/**
	 * 启动Jetty服务
	 */
	public void start() {
		this.start("/");
	}
	
	/**
	 * 停止jetty服务
	 */
	public void stop() {
		try {
			bladeServer.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * join in server
	 * @throws InterruptedException 
	 * @throws Exception 
	 */
	public void join() throws InterruptedException {
		bladeServer.join();
	}
	
	/**
	 * @return	返回系统配置对象
	 */
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

	/**
	 * 注册一个配置文件的路由
	 * 
	 * @param basePackage	控制器包名
	 * @param conf			配置文件路径，配置文件必须在classpath下
	 * @return				返回Blade单例实例
	 */
	public Blade routeConf(String basePackage, String conf) {
		try {
			InputStream ins = Blade.class.getResourceAsStream("/" + conf);
			ClassPathRouteLoader routesLoader = new ClassPathRouteLoader(ins);
			routesLoader.setBasePackage(basePackage);
			List<Route> routes = routesLoader.load();
			router.addRoutes(routes);
		} catch (RouteException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return this;
	}
}
