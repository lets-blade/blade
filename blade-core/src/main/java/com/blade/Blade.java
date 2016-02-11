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

import com.blade.ioc.Ioc;
import com.blade.ioc.SampleIoc;
import com.blade.loader.ClassPathRouteLoader;
import com.blade.loader.BladeConfig;
import com.blade.loader.Configurator;
import com.blade.plugin.Plugin;
import com.blade.render.JspRender;
import com.blade.render.Render;
import com.blade.route.Route;
import com.blade.route.RouteException;
import com.blade.route.RouteHandler;
import com.blade.route.Routers;
import com.blade.server.Server;
import com.blade.web.http.HttpMethod;

import blade.kit.Assert;
import blade.kit.IOKit;
import blade.kit.config.loader.ConfigLoader;
import blade.kit.json.JSONKit;

/**
 * Blade Core Class
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Blade {
	
	/**
     * Blade initialize
     */
    private boolean isInit = false;
    
    /**
     * Servlet asynchronous
     */
    private boolean isAsyn = false;
    
    /**
     * Blade initialize config class
     */
    private Bootstrap bootstrap = null;
    
    /**
	 * Global configuration Object
	 */
	private BladeConfig config = null;
	
    /**
     * IOC Container, save javabean
     */
    private Ioc ioc = null;
    
    /**
	 * ioc application object
	 */
	private IocApplication iocApplication = null;
    
    /**
     * default render is jspRender
     */
    private Render render = null;
    
    /**
     * manage route
     */
    private Routers routers = null;
    
    /**
     * jetty start port
     */
    private int port = Const.DEFAULT_PORT;
    
    /**
     * jetty server
     */
    private Server bladeServer;
    
	private Blade() {
		this.config = new BladeConfig();
		this.ioc = new SampleIoc();
		this.iocApplication = new IocApplication(ioc);
		this.routers = new Routers(ioc);
		this.render = new JspRender();
	}
	
	public static final class BladeHolder {
		private static final Blade ME = new Blade();
	}
	
	/**
	 * @return	Single case method returns Blade object
	 */
	public static final Blade me(){
		return BladeHolder.ME;
	}
	
	/**
	 * Set Blade initialize
	 * @param isInit	initialize
	 */
	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}
	
	/**
	 * create a jetty server
	 * @param port	server port
	 * @return		return server object
	 */
	public Server createServer(int port){
		return new Server(port, isAsyn);
	}
	
	/**
	 * @return	return route manager
	 */
	public Routers routers() {
		return routers;
	}
	
	/**
	 * @return	return blade ioc container
	 */
	public Ioc ioc(){
		return ioc;
	}
	
	/**
	 * Setting a ioc container
	 * @param container	ioc object
	 * @return			return blade
	 */
	public Blade container(Ioc ioc){
		Assert.notNull(ioc);
		this.ioc = ioc;
		return this;
	}
	
	/**
	 * Setting Properties configuration file
	 * File path based on classpath
	 * 
	 * @param confName		properties file name
	 * @return				return blade
	 */
	public Blade setAppConf(String confName){
		Assert.notBlank(confName);
		blade.kit.config.Config config = ConfigLoader.load(confName);
		this.configuration(config.getConfigMap());
		return this;
	}
	
	/**
	 * Setting JSON file configuration
	 * File path based on classpath
	 * 
	 * @param fileName		json file name
	 * @return				return blade
	 */
	public Blade setJsonConf(String fileName){
		Assert.notBlank(fileName);
		InputStream inputStream = Blade.class.getResourceAsStream(fileName);
		if(null != inputStream){
			try {
				String json = IOKit.toString(inputStream);
				Map<String, String> configMap = JSONKit.toMap(json);
				configuration(configMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this;
	}
	
	/**
	 * Setting json character config, Not recommended
	 * 
	 * @param json	json character
	 * @return		return blade
	 */
	public Blade setAppJson(String json){
		Assert.notBlank(json);
		Map<String, String> configMap = JSONKit.toMap(json);
		configuration(configMap);
		return this;
	}
	
	/**
	 * Setting Map config
	 * @param configMap	set config map	
	 */
	private void configuration(Map<String, String> configMap){
		Assert.notEmpty(configMap);
		new Configurator(config, configMap).run();
	}
	
	/**
     * Setting route package，e.g：com.baldejava.route
     * Can be introduced into multiple packages, all of which are in the package.
     * 
     * @param packages 	route package path, is your package name
     * @return			return blade
     */
    public Blade routes(String...packages){
    	Assert.notNull(packages);
    	config.setRoutePackages(packages);
    	return this;
    }
    
    /**
     * Setting top package frame automatically for routing packets and packet interceptor, e.g:com.bladejava
     * As above, it will be over:com.bladejava.route、com.bladejava.interceptor Routing and interceptor inside
     * 
     * @param basePackage 	default package path
     * @return				return blade
     */
    public Blade defaultRoute(String basePackage){
    	Assert.notBlank(basePackage);
   		config.setBasePackage(basePackage);
    	return this;
    }
    
    /**
     * Setting the path where the interceptor, e.g:com.bladejava.interceptor
     * 
     * @param packageName 	interceptor packagename
     * @return				return blade
     */
	public Blade interceptor(String packageName) {
		Assert.notBlank(packageName);
		config.setInterceptorPackage(packageName);
		return this;
	}
	
	/**
     * Setting Ioc packages, e.g:com.bladejava.service
     * 
     * @param packages 	All need to do into the package, can be introduced into a number of
     * @return			return blade
     */
    public Blade ioc(String...packages){
    	Assert.notNull(packages);
    	if(packages.length >0){
    		config.setIocPackages(packages);
    	}
    	return this;
    }
    
    /**
     * Add a route
     * 
     * @param path		route path
     * @param target	Target object for routing
     * @param method	The method name of the route (at the same time, the HttpMethod is specified: post:saveUser, if not specified, HttpMethod.ALL)
     * @return			return blade
     */
	public Blade route(String path, Object target, String method){
		routers.route(path, target, method);
		return this;
	}
	
	/**
     * Add a route
     * 
     * @param path			route path
     * @param target		Target object for routing
     * @param method		The method name of the route (at the same time, the HttpMethod is specified: post:saveUser, if not specified, HttpMethod.ALL)
     * @param httpMethod 	HTTPMethod
     * @return				return blade
     */
	public Blade route(String path, Object target, String method, HttpMethod httpMethod){
		routers.route(path, target, method, httpMethod);
		return this;
	}
	
	/**
     * Add a route
     * 
     * @param path			route path
     * @param target		Target object for routing
     * @param method		The method name of the route (at the same time, the HttpMethod is specified: post:saveUser, if not specified, HttpMethod.ALL)
     * @return				return blade
     */
	public Blade route(String path, Class<?> clazz, String method){
		routers.route(path, clazz, method);
		return this;
	}
	
	/**
	 * Register a functional route
	 * 
	 * @param path			route url	
	 * @param clazz			route processing class
	 * @param method		route processing method name
	 * @param httpMethod	HttpMethod Type, GET/POST/...
	 * @return Blade		return blade
	 */
	public Blade route(String path, Class<?> clazz, String method, HttpMethod httpMethod){
		routers.route(path, clazz, method, httpMethod);
		return this;
	}
	
	/**
	 * Add a route list
	 * @param routes	route list
	 * @return			return blade
	 */
	public Blade routes(List<Route> routes){
		routers.addRoutes(routes);
		return this;
	}
	
	/**
	 * Register a GET request route
	 * 
	 * @param path		route path, request url
	 * @param handler	execute route Handle
	 * @return			return blade
	 */
	public Blade get(String path, RouteHandler handler){
		routers.route(path, handler, HttpMethod.GET);
		return this;
	}
	
	/**
	 * Register a POST request route
	 * 
	 * @param path		route path, request url
	 * @param handler	execute route Handle
	 * @return			return blade
	 */
	public Blade post(String path, RouteHandler handler){
		routers.route(path, handler, HttpMethod.POST);
		return this;
	}
	
	/**
	 * Register a DELETE request route
	 * 
	 * @param path		route path, request url
	 * @param handler	execute route Handle
	 * @return			return blade
	 */
	public Blade delete(String path, RouteHandler handler){
		routers.route(path, handler, HttpMethod.DELETE);
		return this;
	}
	
	/**
	 * Register a PUT request route
	 * 
	 * @param path		route path, request url
	 * @param handler	execute route Handle
	 * @return			return blade
	 */
	public Blade put(String path, RouteHandler handler){
		routers.route(path, handler, HttpMethod.PUT);
		return this;
	}
	
	/**
	 * Register for any request routing
	 * 
	 * @param path		route path, request url
	 * @param handler	execute route Handle
	 * @return			return blade
	 */
	public Blade all(String path, RouteHandler handler){
		routers.route(path, handler, HttpMethod.ALL);
		return this;
	}
	
	/**
	 * Register for any request routing
	 * 
	 * @param path		route path, request url
	 * @param handler	execute route Handle
	 * @return			return blade
	 */
	public Blade any(String path, RouteHandler handler){
		routers.route(path, handler, HttpMethod.ALL);
		return this;
	}
	
	/**
	 * Register a pre routing request interceptor
	 * 
	 * @param path		route path, request url
	 * @param handler	execute route Handle
	 * @return			return blade
	 */
	public Blade before(String path, RouteHandler handler){
		routers.route(path, handler, HttpMethod.BEFORE);
		return this;
	}

	/**
	 * Register a after routing request interceptor
	 * 
	 * @param path		route path, request url
	 * @param handler	execute route Handle
	 * @return			return blade
	 */
	public Blade after(String path, RouteHandler handler){
		routers.route(path, handler, HttpMethod.AFTER);
		return this;
	}
	
	/**
	 * Setting Render Engin, Default is JspRender
	 * 
	 * @param render 	Render engine object
	 * @return			return blade
	 */
	public Blade viewEngin(Render render) {
		Assert.notNull(render);
		this.render = render;
		return this;
	}
	
	/**
	 * Setting default view prefix, default is WEBROOT/WEB-INF/
	 * 
	 * @param prefix 	views path, e.g:/WEB-INF/views/
	 * @return			return blade
	 */
	public Blade viewPrefix(final String prefix) {
		Assert.notBlank(prefix);
		if(prefix.startsWith("/")){
			config.setViewPrefix(prefix);
		}
		return this;
	}
	
	/**
	 * Setting view default suffix, default is .jsp
	 * 
	 * @param suffix	view suffix, e.g:.html .vm
	 * @return			return blade
	 */
	public Blade viewSuffix(final String suffix) {
		Assert.notBlank(suffix);
		if(suffix.startsWith(".")){
			config.setViewSuffix(suffix);
		}
		return this;
	}
	
	/**
	 * Also set the view's directory and view the suffix name
	 * 
	 * @param viewPath	views path, e.g:/WEB-INF/views
	 * @param viewExt	view suffix, e.g:.html .vm
	 * @return			return blade
	 */
	public Blade view(final String viewPath, final String viewExt) {
		Assert.notBlank(viewPath);
		Assert.notBlank(viewExt);
		viewPrefix(viewPath);
		viewSuffix(viewExt);
		return this;
	}
	
	/**
	 * Setting the frame static file folder
	 * 
	 * @param folders	List of directories to filter, e.g: "/public,/static,/images"
	 * @return			return blade
	 */
	public Blade staticFolder(final String ... folders) {
		Assert.notNull(folders);
		config.setStaticFolders(folders);
		return this;
	}
	
	/**
	 * Setting XSS is enable
	 * 
	 * @param enableXSS	enable XSS, default is false
	 * @return			return blade
	 */
	public Blade enableXSS(boolean enableXSS){
		config.setEnableXSS(enableXSS);
		return this; 
	}
	
	/**
     * Dynamically set the global initialization class, the embedded Jetty boot
     * 
     * @param bootstrap 	global initialization config class
     * @return				return blade
     */
    public Blade app(Bootstrap bootstrap){
    	Assert.notNull(bootstrap);
    	this.bootstrap = bootstrap;
    	return this;
    }
    
    /**
     * Dynamically set global initialization class
     * 
     * @param bootstrap 	global initialization config class
     * @return				return blade
     */
    public Blade app(Class<? extends Bootstrap> bootstrap){
    	Assert.notNull(bootstrap);
    	ioc.addBean(bootstrap);
    	return this;
    }
    
    /**
     * Setting 404 view page
     * 
     * @param view404	404 view page
     * @return			return blade
     */
    public Blade setView404(final String view404){
    	Assert.notBlank(view404);
    	config.setView404(view404);
    	return this;
    }
    
    /**
     * Setting 500 view page
     * 
     * @param view500	500 view page
     * @return			return blade
     */
    public Blade setView500(final String view500){
    	Assert.notBlank(view500);
    	config.setView500(view500);
    	return this;
    }

    /**
     * Setting blade web root path
     * 
     * @param webRoot	web root path
     * @return			return blade
     */
    public Blade webRoot(final String webRoot){
    	Assert.notBlank(webRoot);
    	config.setWebRoot(webRoot);
    	return this;
    }
    
    /**
	 * Setting blade run mode
	 * 
	 * @param isDev		is dev mode
	 * @return			return blade
	 */
	public Blade isDev(boolean isDev){
		config.setDev(isDev);
		return this;
	}
	
	/**
	 * Setting jetty listen port
	 * 
	 * @param port		port, default is 9000
	 * @return			return blade
	 */
	public Blade listen(int port){
		this.port = port;
		return this;
	}
	
	/**
	 * Setting servlet asynchronous
	 * @param isAsyn	is asynchronous
	 * @return			return blade
	 */
	public Blade isAsyn(boolean isAsyn){
		this.isAsyn = isAsyn;
		return this;
	}
	
	/**
	 * Setting jetty context
	 * 
	 * @param contextPath	context path, default is /
	 */
	public void start(String contextPath) {
		try {
			Assert.notBlank(contextPath);
			bladeServer = new Server(this.port, this.isAsyn);
			bladeServer.start(contextPath);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	    
	}
	
	/**
	 * Start jetty server
	 */
	public void start() {
		this.start("/");
	}
	
	/**
	 * Jetty sever shutdown
	 */
	public void stop() {
		try {
			bladeServer.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Join in server
	 * 
	 * @throws InterruptedException join exception
	 */
	public void join() throws InterruptedException {
		bladeServer.join();
	}
	
	/**
	 * @return	Return blade config object
	 */
	public BladeConfig config(){
    	return config;
    }
	
    /**
     * @return	Return blade scan base package
     */
    public String basePackage(){
    	return config.getBasePackage();
    }
    
	/**
     * @return	Return route packages
     */
    public String[] routePackages(){
    	return config.getRoutePackages();
    }
    
    /**
     * @return	Return ioc packages
     */
    public String[] iocs(){
    	return config.getIocPackages();
    }
    
    /**
     * @return	Returns the interceptor array, only one element here use String[]
     */
    public String interceptorPackage(){
    	return config.getInterceptorPackage();
    }
    
    
    /**
     * @return	Return views prefix
     */
    public String viewPrefix(){
    	return config.getViewPrefix();
    }
    
    /**
     * @return	Return blade encoding, default is UTF-8
     */
    public String encoding(){
    	return config.getEncoding();
    }
    
    /**
     * @return	Return view suffix
     */
    public String viewSuffix(){
    	return config.getViewSuffix();
    }
    
    /**
     * @return	Return 404 view
     */
    public String view404(){
    	return config.getView404();
    }
    
    /**
     * @return	Return 500 view
     */
    public String view500(){
    	return config.getView500();
    }
    
    /**
     * @return	Return blade web root path
     */
    public String webRoot(){
    	return config.getWebRoot();
    }
    
    /**
	 * @return	Return is dev mode
	 */
	public boolean isDev(){
		return config.isDev();
	}
	
	/**
	 * @return	Return static resource directory
	 */
	public String[] staticFolder(){
		return config.getStaticFolders();
	}
	
	/**
	 * @return	Return bootstrap object
	 */
	public Bootstrap bootstrap(){
		return ioc.getBean(Bootstrap.class); 
	}
	
	/**
	 * @return	Return current render engine
	 */
	public Render render() {
		return this.render;
	}

	/**
	 * @return	Return XSS is enabled
	 */
	public boolean enableXSS(){
		return config.isEnableXSS(); 
	}
	
	/**
	 * return register plugin object
	 * 
	 * @param plugin		plugin class
	 * @param <T>			generic
	 * @return				return plugin object
	 */
	@SuppressWarnings("unchecked")
	public <T> T plugin(Class<? extends Plugin> plugin){
		Assert.notNull(plugin);
		Object object = iocApplication.getPlugin(plugin);
		if(null == object){
			object = iocApplication.registerPlugin(plugin);
		}
		return (T) object;
	}

	/**
	 * Registration of a configuration file, e.g: "com.xxx.route","route.conf"
	 * 
	 * @param basePackage	controller package name
	 * @param conf			Configuration file path, the configuration file must be in classpath
	 * @return				return blade
	 */
	public Blade routeConf(String basePackage, String conf) {
		try {
			Assert.notBlank(basePackage);
			Assert.notBlank(conf);
			InputStream ins = Blade.class.getResourceAsStream("/" + conf);
			ClassPathRouteLoader routesLoader = new ClassPathRouteLoader(ins);
			routesLoader.setBasePackage(basePackage);
			List<Route> routes = routesLoader.load();
			routers.addRoutes(routes);
		} catch (RouteException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * @return	Return IocApplication object
	 */
	public IocApplication iocApplication(){
		return iocApplication;
	}
	
	/**
	 * @return	Initialize ioc application and return blade
	 */
	public Blade iocInit(){
		iocApplication.init(iocs(), bootstrap);
		return this;
	}

	/**
	 * @return	Return blade is initialize 
	 */
	public boolean isInit() {
		return isInit;
	}
	
}
