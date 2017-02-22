/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade;

import com.blade.config.BConfig;
import com.blade.embedd.EmbedServer;
import com.blade.exception.BladeException;
import com.blade.exception.EmbedServerException;
import com.blade.exception.RouteException;
import com.blade.ioc.Ioc;
import com.blade.ioc.SimpleIoc;
import com.blade.kit.Assert;
import com.blade.kit.CollectionKit;
import com.blade.kit.StringKit;
import com.blade.kit.base.Config;
import com.blade.kit.reflect.ReflectKit;
import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.interceptor.Interceptor;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.RouteBuilder;
import com.blade.mvc.route.RouteGroup;
import com.blade.mvc.route.Routers;
import com.blade.mvc.route.loader.ClassPathRouteLoader;
import com.blade.plugin.Plugin;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Blade Core Class
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.7.0-beta
 */
public final class Blade {

    /**
     * Indicates whether the framework has been initialized
     */
    private boolean isInit = false;

    /**
     * Framework Global Configuration
     */
    private BConfig bConfig;

    /**
     * Default ioc container
     */
    private Ioc ioc = new SimpleIoc();

    /**
     * Default routes
     */
    private Routers routers = new Routers();

    /**
     * Route builder
     */
    private RouteBuilder routeBuilder;

    /**
     * Is enabled server
     */
    private Boolean enableServer = false;

    /**
     * plugins
     */
    private Set<Class<? extends Plugin>> plugins;

    /**
     * filters
     */
    private Map<Class<? extends Filter>, String[]> filters = CollectionKit.newHashMap(8);

    /**
     * servlets
     */
    private Map<Class<? extends HttpServlet>, String[]> servlets = CollectionKit.newHashMap(8);

    /**
     * embed web server e.g:jetty/tomcat
     */
    private EmbedServer embedServer;

    private Blade() {
        this.bConfig = new BConfig();
        this.plugins = CollectionKit.newHashSet();
        this.routeBuilder = new RouteBuilder(this.routers);
    }

    private static final class BladeHolder {
        private static final Blade $ = new Blade();
    }

    /**
     * @return Single case method returns Blade object
     */
    @Deprecated
    public static Blade me() {
        return BladeHolder.$;
    }

    /**
     * @param location
     * @return
     */
    @Deprecated
    public static Blade me(String location) {
        return $(location);
    }

    /**
     * @return Single case method returns Blade object
     */
    public static Blade $() {
        return BladeHolder.$;
    }

    /**
     * load blade application config file
     *
     * @param location
     * @return
     */
    public static Blade $(String location) {
        Assert.notEmpty(location);
        Blade blade = BladeHolder.$;
        blade.loadAppConf(location);
        return blade;
    }

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

    public RouteBuilder routeBuilder() {
        return routeBuilder;
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
     * @param ioc object
     * @return return blade
     */
    public Blade container(Ioc ioc) {
        Assert.notNull(ioc);
        this.ioc = ioc;
        return this;
    }

    /**
     * Setting Properties configuration file File path based on classpath
     *
     * @param location properties file name
     * @return return blade
     */
    public Blade loadAppConf(String location) {
        Assert.notBlank(location);
        bConfig.load(location);
        return this;
    }

    /**
     * set base package
     *
     * @param basePackage
     * @return
     */
    public Blade basePackage(String basePackage) {
        Assert.notBlank(basePackage);
        bConfig.setBasePackage(basePackage);
        return this;
    }

    @Deprecated
    public Blade ioc(String... packages) {
        return this.scan(packages);
    }

    /**
     * Setting Ioc packages, e.g:com.bladejava.service
     *
     * @param packages All need to do into the package, can be introduced into a
     *                 number of
     * @return return blade
     */
    public Blade scan(String... packages) {
        bConfig.addScanPackage(packages);
        return this;
    }

    /**
     * Add a route
     *
     * @param path   route path
     * @param clazz  Target object for routing
     * @param method The method name of the route (at the same time, the HttpMethod
     *               is specified: post:saveUser, if not specified, HttpMethod.ALL)
     * @return return blade
     */
    public Blade route(String path, Class<?> clazz, String method) {
        routers.route(path, clazz, method);
        return this;
    }

    /**
     * regsiter filter
     *
     * @param clazz
     * @param pathSpec
     * @return
     */
    public Blade registerFilter(Class<? extends Filter> clazz, String... pathSpec) {
        filters.put(clazz, pathSpec);
        return this;
    }

    /**
     * regsiter servlet
     *
     * @param clazz
     * @param pathSpec
     * @return
     */
    public Blade registerServlet(Class<? extends HttpServlet> clazz, String... pathSpec) {
        servlets.put(clazz, pathSpec);
        return this;
    }

    public Map<Class<? extends Filter>, String[]> filters() {
        return filters;
    }

    public Map<Class<? extends HttpServlet>, String[]> servlets() {
        return servlets;
    }

    /**
     * Register a functional route
     *
     * @param path       route url
     * @param clazz      route processing class
     * @param method     route processing method name
     * @param httpMethod HttpMethod Type, GET/POST/...
     * @return Blade return blade
     */
    public Blade route(String path, Class<?> clazz, String method, HttpMethod httpMethod) {
        routers.route(path, clazz, method, httpMethod);
        return this;
    }

    /**
     * Add a route list
     *
     * @param routes route list
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
     * @param path    route path, request url
     * @param handler execute route Handle
     * @return return blade
     */
    public Blade get(String path, RouteHandler handler) {
        routers.route(path, handler, HttpMethod.GET);
        return this;
    }

    /**
     * Register a POST request route
     *
     * @param path    route path, request url
     * @param handler execute route Handle
     * @return return blade
     */
    public Blade post(String path, RouteHandler handler) {
        routers.route(path, handler, HttpMethod.POST);
        return this;
    }

    /**
     * Register a DELETE request route
     *
     * @param path    route path, request url
     * @param handler execute route Handle
     * @return return blade
     */
    public Blade delete(String path, RouteHandler handler) {
        routers.route(path, handler, HttpMethod.DELETE);
        return this;
    }

    /**
     * Register a PUT request route
     *
     * @param path    route path, request url
     * @param handler execute route Handle
     * @return return blade
     */
    public Blade put(String path, RouteHandler handler) {
        routers.route(path, handler, HttpMethod.PUT);
        return this;
    }

    /**
     * Register for any request routing
     *
     * @param path    route path, request url
     * @param handler execute route Handle
     * @return return blade
     */
    public Blade all(String path, RouteHandler handler) {
        routers.route(path, handler, HttpMethod.ALL);
        return this;
    }

    /**
     * Register for any request routing
     *
     * @param path    route path, request url
     * @param handler execute route Handle
     * @return return blade
     */
    public Blade any(String path, RouteHandler handler) {
        routers.route(path, handler, HttpMethod.ALL);
        return this;
    }

    /**
     * Route Group. e.g blade.group('/users').get().post()
     *
     * @param prefix
     * @return return blade
     */
    public RouteGroup group(String prefix) {
        Assert.notNull(prefix, "Route group prefix not is null");
        return new RouteGroup(this, prefix);
    }

    /**
     * Register a pre routing request interceptor
     *
     * @param path    route path, request url
     * @param handler execute route Handle
     * @return return blade
     */
    public Blade before(String path, RouteHandler handler) {
        routers.route(path, handler, HttpMethod.BEFORE);
        return this;
    }

    /**
     * Register a after routing request interceptor
     *
     * @param path    route path, request url
     * @param handler execute route Handle
     * @return return blade
     */
    public Blade after(String path, RouteHandler handler) {
        routers.route(path, handler, HttpMethod.AFTER);
        return this;
    }

    /**
     * Setting the frame static file folder
     *
     * @param statics List of directories to filter, e.g: "/public,/static,/images"
     * @return return blade
     */
    public Blade addStatics(final String... statics) {
        bConfig.addStatic(statics);
        return this;
    }

    /**
     * add interceptor
     *
     * @param interceptor interceptor class
     * @return return blade obj
     */
    public Blade addInterceptor(Class<? extends Interceptor> interceptor) {
        routeBuilder.addInterceptor(interceptor);
        return this;
    }

    /**
     * Setting blade web root path
     *
     * @param webRoot web root path
     * @return return blade
     */
    public Blade webRoot(final String webRoot) {
        Assert.notBlank(webRoot);
        bConfig.setWebRoot(webRoot);
        return this;
    }

    /**
     * Setting blade run mode
     *
     * @param isDev is dev mode
     * @return return blade
     */
    public Blade isDev(boolean isDev) {
        bConfig.setDev(isDev);
        return this;
    }

    /**
     * Setting jetty listen port
     *
     * @param port port, default is 9000
     * @return return blade
     */
    public Blade listen(int port) {
        config().put(Const.SERVER_PORT, port);
        return this;
    }

    /**
     * start web server
     *
     * @param applicationClass your app root package starter
     */
    public EmbedServer start(Class<?> applicationClass, String contextPath) {
        startNoJoin(applicationClass, contextPath);
        embedServer.join();
        return embedServer;
    }

    public EmbedServer start(Class<?> applicationClass) {
        return start(applicationClass, "/");
    }

    public EmbedServer startNoJoin(Class<?> applicationClass, String contextPath) {
        this.loadAppConf(Const.APP_PROPERTIES);
        if (null != applicationClass) {
            bConfig.setApplicationClass(applicationClass);
            if (StringKit.isBlank(bConfig.getBasePackage()) && null != applicationClass.getPackage()) {
                bConfig.setBasePackage(applicationClass.getPackage().getName());
            }
        }
        Class<?> embedClazz = ReflectKit.newClass(Const.JETTY_SERVER_CLASS);
        if (null == embedClazz) {
            embedClazz = ReflectKit.newClass(Const.TOMCAT_SERVER_CLASS);
        }
        if (null != embedClazz) {
            if (!bConfig().isInit()) {
                loadAppConf(Const.APP_PROPERTIES);
                bConfig().setEnv(config());
            }
            this.embedServer = (EmbedServer) ReflectKit.newBean(embedClazz);
            this.embedServer.startup(config().getInt(Const.SERVER_PORT, Const.DEFAULT_PORT), contextPath);
            this.enableServer = true;
            return embedServer;
        } else {
            throw new EmbedServerException("Not found EmbedServer");
        }
    }

    /**
     * @return Return EmbedServer
     */
    public EmbedServer embedServer() {
        return this.embedServer;
    }

    /**
     * @return Return blade config object
     */
    public BConfig bConfig() {
        return bConfig;
    }

    /**
     * @return Return blade config object
     */
    public Config config() {
        return bConfig.config();
    }

    /**
     * @return Return blade encoding, default is UTF-8
     */
    public String encoding() {
        return bConfig.getEncoding();
    }

    /**
     * @return Return blade web root path
     */
    public String webRoot() {
        return bConfig.webRoot();
    }

    /**
     * @return Return is dev mode
     */
    public boolean isDev() {
        return bConfig.isDev();
    }

    /**
     * return register plugin object
     *
     * @param plugin plugin class
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
     * @param basePackage controller package name
     * @return return blade
     */
    public Blade routeConf(String basePackage) {
        return routeConf(basePackage, Const.DEFAULT_ROUTE_CONF);
    }

    /**
     * Registration of a configuration file, e.g: "com.xxx.route","route.conf"
     *
     * @param basePackage controller package name
     * @param conf        Configuration file path, the configuration file must be in
     *                    classpath
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
        } catch (RouteException | ParseException e) {
            throw new BladeException(e);
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

    /**
     * startup arguments
     *
     * @param args
     * @return
     */
    public Blade args(String... args) {
        System.out.println("args: " + args);
        return this;
    }

    public boolean enableServer() {
        return this.enableServer;
    }

    public Set<Class<? extends Plugin>> plugins() {
        return this.plugins;
    }

}