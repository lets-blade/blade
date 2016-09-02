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

import java.io.InputStream;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blade.config.ApplicationConfig;
import com.blade.config.BaseConfig;
import com.blade.config.ConfigLoader;
import com.blade.embedd.EmbedServer;
import com.blade.exception.EmbedServerException;
import com.blade.interceptor.Interceptor;
import com.blade.ioc.Ioc;
import com.blade.ioc.SimpleIoc;
import com.blade.kit.Assert;
import com.blade.kit.Environment;
import com.blade.kit.StringKit;
import com.blade.kit.reflect.ReflectKit;
import com.blade.plugin.Plugin;
import com.blade.route.Route;
import com.blade.route.RouteBuilder;
import com.blade.route.RouteException;
import com.blade.route.RouteGroup;
import com.blade.route.RouteHandler;
import com.blade.route.Routers;
import com.blade.route.loader.ClassPathRouteLoader;
import com.blade.web.http.HttpMethod;

/**
 * Blade Core Class
 * 
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.6.6
 */
public final class Blade {

	// blade initialize
	private boolean isInit = false;

	// blade bootstrap config class
	private Bootstrap bootstrap = null;

	// global configuration Object
	private ApplicationConfig applicationConfig = null;

	// ioc container
	private Ioc ioc = new SimpleIoc();

	// routes
	private Routers routers = new Routers();

	// routebuilder
	private RouteBuilder routeBuilder;

	// jetty start port
	private int port = Const.DEFAULT_PORT;
	
	// default context path
	private String contextPath = Const.DEFAULT_CONTEXTPATH;

	// enableServer
	private Boolean enableServer = false;

	// plugins
	private Set<Class<? extends Plugin>> plugins;

	// global environment
	private Environment environment;

	// config loader
	private ConfigLoader configLoader;
	
	private Blade() {
		this.environment = new Environment();
		this.applicationConfig = new ApplicationConfig();
		this.plugins = new HashSet<Class<? extends Plugin>>();
		this.routeBuilder = new RouteBuilder(this.routers);
		this.configLoader = new ConfigLoader(this.ioc, this.applicationConfig);
	}

	static final class BladeHolder {
		private static final Blade $ = new Blade();
	}

	/**
	 * @return Single case method returns Blade object
	 */
	@Deprecated
	public static final Blade me() {
		return BladeHolder.$;
	}

	/**
	 * 
	 * @param appConf
	 * @return
	 */
	@Deprecated
	public static final Blade me(String confPath) {
		Blade blade = BladeHolder.$;
		blade.environment.add(confPath);
		return blade;
	}

	/**
	 * @return Single case method returns Blade object
	 */
	public static final Blade $() {
		return BladeHolder.$;
	}

	/**
	 * @param confPath
	 * @return
	 */
	public static final Blade $(String confPath) {
		Assert.notEmpty(confPath);
		Blade blade = BladeHolder.$;
		blade.environment.add(confPath);
		return blade;
	}

	/**
	 * Set Blade initialize
	 * 
	 * @param isInit
	 *            initialize
	 */
	public void init() {
		if (!this.isInit) {
			this.isInit = true;
		}
	}
	
	/**
	 * @return return route manager
	 */
	public Routers routers() {
		return routers;
	}

	/**
	 * @return return RouteBuilder
	 */
	public RouteBuilder routeBuilder() {
		return routeBuilder;
	}

	/**
	 * @return return ConfigLoader
	 */
	public ConfigLoader configLoader() {
		return configLoader;
	}

	/**
	 * @return return blade ioc container
	 */
	public Ioc ioc() {
		return ioc;
	}
	
	/**
	 * Setting a ioc container
	 * 
	 * @param container
	 *            ioc object
	 * @return return blade
	 */
	public Blade container(Ioc ioc) {
		Assert.notNull(ioc);
		this.ioc = ioc;
		this.configLoader.setIoc(ioc);
		return this;
	}

	/**
	 * Setting Properties configuration file File path based on classpath
	 * 
	 * @param confPath
	 *            properties file name
	 * @return return blade
	 */
	public Blade loadAppConf(String confPath) {
		Assert.notBlank(confPath);
		environment.add(confPath);
		return this;
	}

	/**
	 * Setting route package，e.g：com.baldejava.route Can be introduced into
	 * multiple packages, all of which are in the package.
	 * 
	 * @param packages
	 *            route package path, is your package name
	 * @return return blade
	 */
	public Blade addRoutePackage(String packageName) {
		return this.addRoutePackages(packageName);
	}

	/**
	 * Setting route package，e.g：com.baldejava.route Can be introduced into
	 * multiple packages, all of which are in the package.
	 * 
	 * @param packages
	 *            route package path, is your package name
	 * @return return blade
	 */
	public Blade addRoutePackages(String... packages) {
		Assert.notNull(packages);
		applicationConfig.addRoutePackages(packages);
		return this;
	}

	/**
	 * 
	 * @param basePackage
	 * @return
	 */
	public Blade basePackage(String basePackage) {
		Assert.notBlank(basePackage);
		applicationConfig.setBasePackage(basePackage);
		applicationConfig.addIocPackages(basePackage + ".service.*");
		applicationConfig.addRoutePackages(basePackage + ".controller");
		applicationConfig.setInterceptorPackage(basePackage + ".interceptor");
		return this;
	}

	/**
	 * Setting the path where the interceptor, e.g:com.bladejava.interceptor
	 * 
	 * @param packageName
	 *            interceptor packagename
	 * @return return blade
	 */
	public Blade interceptor(String packageName) {
		Assert.notBlank(packageName);
		applicationConfig.setInterceptorPackage(packageName);
		return this;
	}

	/**
	 * Setting Ioc packages, e.g:com.bladejava.service
	 * 
	 * @param packages
	 *            All need to do into the package, can be introduced into a
	 *            number of
	 * @return return blade
	 */
	public Blade ioc(String... packages) {
		Assert.notNull(packages);
		applicationConfig.addIocPackages(packages);
		return this;
	}

	/**
	 * Add a route
	 * 
	 * @param path
	 *            route path
	 * @param target
	 *            Target object for routing
	 * @param method
	 *            The method name of the route (at the same time, the HttpMethod
	 *            is specified: post:saveUser, if not specified, HttpMethod.ALL)
	 * @return return blade
	 */
	public Blade route(String path, Class<?> clazz, String method) {
		routers.route(path, clazz, method);
		return this;
	}

	/**
	 * Register a functional route
	 * 
	 * @param path
	 *            route url
	 * @param clazz
	 *            route processing class
	 * @param method
	 *            route processing method name
	 * @param httpMethod
	 *            HttpMethod Type, GET/POST/...
	 * @return Blade return blade
	 */
	public Blade route(String path, Class<?> clazz, String method, HttpMethod httpMethod) {
		routers.route(path, clazz, method, httpMethod);
		return this;
	}

	/**
	 * Add a route list
	 * 
	 * @param routes
	 *            route list
	 * @return return blade
	 */
	public Blade routes(List<Route> routes) {
		Assert.notEmpty(routes, "Routes not is empty!");
		routers.addRoutes(routes);
		return this;
	}

	/**
	 * Register a GET request route
	 * 
	 * @param path
	 *            route path, request url
	 * @param handler
	 *            execute route Handle
	 * @return return blade
	 */
	public Blade get(String path, RouteHandler handler) {
		routers.route(path, handler, HttpMethod.GET);
		return this;
	}

	/**
	 * Register a POST request route
	 * 
	 * @param path
	 *            route path, request url
	 * @param handler
	 *            execute route Handle
	 * @return return blade
	 */
	public Blade post(String path, RouteHandler handler) {
		routers.route(path, handler, HttpMethod.POST);
		return this;
	}

	/**
	 * Register a DELETE request route
	 * 
	 * @param path
	 *            route path, request url
	 * @param handler
	 *            execute route Handle
	 * @return return blade
	 */
	public Blade delete(String path, RouteHandler handler) {
		routers.route(path, handler, HttpMethod.DELETE);
		return this;
	}

	/**
	 * Register a PUT request route
	 * 
	 * @param path
	 *            route path, request url
	 * @param handler
	 *            execute route Handle
	 * @return return blade
	 */
	public Blade put(String path, RouteHandler handler) {
		routers.route(path, handler, HttpMethod.PUT);
		return this;
	}

	/**
	 * Register for any request routing
	 * 
	 * @param path
	 *            route path, request url
	 * @param handler
	 *            execute route Handle
	 * @return return blade
	 */
	public Blade all(String path, RouteHandler handler) {
		routers.route(path, handler, HttpMethod.ALL);
		return this;
	}

	/**
	 * Register for any request routing
	 * 
	 * @param path
	 *            route path, request url
	 * @param handler
	 *            execute route Handle
	 * @return return blade
	 */
	public Blade any(String path, RouteHandler handler) {
		routers.route(path, handler, HttpMethod.ALL);
		return this;
	}

	/**
	 * Route Group. e.g blade.group('/users').get().post()
	 * 
	 * @param g
	 * @return return blade
	 */
	public RouteGroup group(String prefix) {
		Assert.notNull(prefix, "Route group prefix not is null");
		return new RouteGroup(this, prefix);
	}

	/**
	 * Register a pre routing request interceptor
	 * 
	 * @param path
	 *            route path, request url
	 * @param handler
	 *            execute route Handle
	 * @return return blade
	 */
	public Blade before(String path, RouteHandler handler) {
		routers.route(path, handler, HttpMethod.BEFORE);
		return this;
	}

	/**
	 * Register a after routing request interceptor
	 * 
	 * @param path
	 *            route path, request url
	 * @param handler
	 *            execute route Handle
	 * @return return blade
	 */
	public Blade after(String path, RouteHandler handler) {
		routers.route(path, handler, HttpMethod.AFTER);
		return this;
	}

	/**
	 * Setting the frame static file folder
	 * 
	 * @param resources
	 *            List of directories to filter, e.g: "/public,/static,/images"
	 * @return return blade
	 */
	public Blade addResources(final String... resources) {
		applicationConfig.addResources(resources);
		return this;
	}

	/**
	 * Dynamically set the global initialization class, the embedded Jetty boot
	 * 
	 * @param bootstrap
	 *            global initialization config class
	 * @return return blade
	 */
	public Blade app(Bootstrap bootstrap) {
		Assert.notNull(bootstrap);
		this.bootstrap = bootstrap;
		return this;
	}

	/**
	 * Dynamically set global initialization class
	 * 
	 * @param bootstrap
	 *            global initialization config class
	 * @return return blade
	 */
	public Blade app(Class<? extends Bootstrap> bootstrap) {
		Assert.notNull(bootstrap);
		try {
			Bootstrap object = (Bootstrap) ReflectKit.newInstance(bootstrap);
			ioc.addBean(Bootstrap.class.getName(), object);
			this.bootstrap = object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * add interceptor 
	 * 
	 * @param interceptor	interceptor class
	 * @return				return blade obj
	 */
	public Blade addInterceptor(Class<? extends Interceptor> interceptor) {
		routeBuilder.addInterceptor(interceptor);
		return this;
	}

	/**
	 * add config 
	 * 
	 * @param config		config class
	 * @return				return blade obj
	 */
	public Blade addConfig(Class<? extends BaseConfig> config) {
		configLoader.addConfig(config);
		return this;
	}

	/**
	 * Setting 404 view page
	 * 
	 * @param view404
	 *            404 view page
	 * @return return blade
	 */
	public Blade view404(final String view404) {
		Assert.notBlank(view404);
		applicationConfig.setView404(view404);
		return this;
	}

	/**
	 * Setting 500 view page
	 * 
	 * @param view500
	 *            500 view page
	 * @return return blade
	 */
	public Blade view500(final String view500) {
		Assert.notBlank(view500);
		applicationConfig.setView500(view500);
		return this;
	}

	/**
	 * Setting blade web root path
	 * 
	 * @param webRoot
	 *            web root path
	 * @return return blade
	 */
	public Blade webRoot(final String webRoot) {
		Assert.notBlank(webRoot);
		applicationConfig.setWebRoot(webRoot);
		return this;
	}

	/**
	 * Setting blade run mode
	 * 
	 * @param isDev
	 *            is dev mode
	 * @return return blade
	 */
	public Blade isDev(boolean isDev) {
		applicationConfig.setDev(isDev);
		return this;
	}

	/**
	 * Setting jetty listen port
	 * 
	 * @param port
	 *            port, default is 9000
	 * @return return blade
	 */
	public Blade listen(int port) {
		this.port = port;
		return this;
	}
	
	/**
	 * start web server
	 */
	public void start() {
		this.start(null);
	}
	
	/**
	 * start web server
	 * @param applicationClass	your app root package starter
	 */
	public void start(Class<?> applicationClass) {
		
	    this.loadAppConf(Const.APP_PROPERTIES);
	    
		// init blade environment config
	    applicationConfig.setEnv(environment);
	    
	    if(null != applicationClass){
	    	applicationConfig.setApplicationClass(applicationClass);
		    if(StringKit.isBlank(applicationConfig.getBasePackage())){
		    	applicationConfig.setBasePackage(applicationClass.getPackage().getName());
		    }
	    }
	    
	    try {
			Class<?> embedClazz = Class.forName("com.blade.embedd.EmbedJettyServer");
			if(null == embedClazz){
				embedClazz = Class.forName("com.blade.embedd.EmbedTomcatServer");
			}
			if(null != embedClazz){
				EmbedServer embedServer = (EmbedServer) embedClazz.newInstance();
				embedServer.startup(port, contextPath);
				this.enableServer = true;
			} else {
				throw new EmbedServerException("Not found EmbedServer");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Return blade config object
	 */
	public ApplicationConfig applicationConfig() {
		return applicationConfig;
	}

	/**
	 * @return Return blade config object
	 */
	public ApplicationConfig config() {
		return applicationConfig;
	}

	/**
	 * @return Return Blade Environment
	 */
	public Environment environment() {
		return environment;
	}

	/**
	 * @return Return blade encoding, default is UTF-8
	 */
	public String encoding() {
		return applicationConfig.getEncoding();
	}

	/**
	 * @return Return 404 view
	 */
	public String view404() {
		return applicationConfig.getView404();
	}

	/**
	 * @return Return 500 view
	 */
	public String view500() {
		return applicationConfig.getView500();
	}

	/**
	 * @return Return blade web root path
	 */
	public String webRoot() {
		return applicationConfig.getWebRoot();
	}

	/**
	 * @return Return is dev mode
	 */
	public boolean isDev() {
		return applicationConfig.isDev();
	}
	
	/**
	 * @return Return static resource directory
	 */
	public Set<String> staticFolder() {
		return applicationConfig.getStaticFolders();
	}

	/**
	 * @return Return bootstrap object
	 */
	public Bootstrap bootstrap() {
		return this.bootstrap;
	}
	
	/**
	 * return register plugin object
	 * 
	 * @param plugin
	 *            plugin class
	 * @return return blade
	 */
	public Blade plugin(Class<? extends Plugin> plugin) {
		Assert.notNull(plugin);
		plugins.add(plugin);
		return this;
	}

	/**
	 * Registration of a configuration file, e.g: "com.xxx.route","route.conf"
	 * 
	 * @param basePackage
	 *            controller package name
	 * @return return blade
	 */
	public Blade routeConf(String basePackage) {
		return routeConf(basePackage, "route.conf");
	}

	/**
	 * Registration of a configuration file, e.g: "com.xxx.route","route.conf"
	 * 
	 * @param basePackage
	 *            controller package name
	 * @param conf
	 *            Configuration file path, the configuration file must be in
	 *            classpath
	 * @return return blade
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
	 * @return Return blade is initialize
	 */
	public boolean isInit() {
		return isInit;
	}

	public Blade enableServer(boolean enableServer) {
		this.enableServer = enableServer;
		return this;
	}

	public boolean enableServer() {
		return this.enableServer;
	}

	public Set<Class<? extends Plugin>> plugins() {
		return this.plugins;
	}

}