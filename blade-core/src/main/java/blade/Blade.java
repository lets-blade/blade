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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import blade.ioc.Container;
import blade.ioc.DefaultContainer;
import blade.kit.IOKit;
import blade.kit.PropertyKit;
import blade.kit.json.JSONKit;
import blade.render.Render;
import blade.render.RenderFactory;
import blade.route.HttpMethod;
import blade.route.Router;
import blade.route.RouteMatcherBuilder;

/**
 * Blade Core Class
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class Blade {
	
	public static final String VERSION = "1.2.8-beta";
	/**
     * 框架是否已经初始化
     */
    protected static boolean IS_INIT = false;
    
    /**
     * blade全局初始化对象，在web.xml中配置，必须
     */
    protected static Bootstrap bootstrap;
    
    /**
	 * 全局配置对象
	 */
	protected final static BladeConfig BLADE_CONFIG = new BladeConfig();
	
    /**
     * IOC容器，存储路由到ioc中
     */
    private final static Container container = DefaultContainer.single();
    
	private Blade() {
	}
	
	static synchronized void init() {
        Blade.IS_INIT = true;
    }
	
	/**
	 * <pre>
	 * 手动注册一个对象到ioc容器中
	 * </pre>
	 * 
	 * @param object		要注册的object
	 */
	public static synchronized void regObject(Object object){
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
	public static synchronized void config(String confName){
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
	public static synchronized void configJsonPath(String jsonPath){
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
	public static synchronized void configJson(String json){
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
	private static void configuration(Map<String, String> configMap){
		new BladeConfigurator(Blade.BLADE_CONFIG, configMap).run();
	}
	
	/**
     * 设置路由包，如：com.baldejava.route
     * 可传入多个包，所有的路由类都在该包下
     * 
     * @param packages 	路由包路径
     */
    public static synchronized void routes(String...packages){
    	if(null != packages && packages.length >0){
    		BLADE_CONFIG.setRoutePackages(packages);
    	}
    }
    
    /**
     * 设置顶层包，框架自动寻找路由包和拦截器包，如：com.bladejava
     * 如上规则，会超找com.bladejava.route、com.bladejava.interceptor下的路由和拦截器
     * 
     * @param basePackage 	默认包路径
     */
    public static synchronized void defaultRoute(String basePackage){
    	if(null != basePackage){
    		BLADE_CONFIG.setBasePackage(basePackage);
    	}
    }
    
    /**
     * 设置拦截器所在的包路径，如：com.bladejava.interceptor
     * 
     * @param packageName 拦截器所在的包
     */
	public static synchronized void interceptor(String packageName) {
		if(null != packageName && packageName.length() >0){
			BLADE_CONFIG.setInterceptorPackage(packageName);
    	}
	}
	
	/**
     * 设置依赖注入包，如：com.bladejava.service
     * 
     * @param packages 	所有需要做注入的包，可传入多个
     */
    public static synchronized void ioc(String...packages){
    	if(null != packages && packages.length >0){
    		BLADE_CONFIG.setIocPackages(packages);
    	}
    }
    
	/**
	 * 设置渲染引擎，默认是JSP引擎
	 * 
	 * @param render 	渲染引擎对象
	 */
	public static synchronized void viewEngin(Render render) {
		RenderFactory.init(render);
	}
	
	/**
	 * 设置默认视图前缀，默认为WEB_ROOT/WEB-INF目录
	 * 
	 * @param prefix 	视图路径，如：/WEB-INF/views
	 */
	public static synchronized void viewPrefix(final String prefix) {
		if(null != prefix && prefix.startsWith("/")){
			BLADE_CONFIG.setViewPrefix(prefix);
		}
	}
	
	/**
	 * 设置视图默认后缀名，默认为.jsp
	 * 
	 * @param viewExt	视图后缀，如：.html	 .vm
	 */
	public static synchronized void viewSuffix(final String suffix) {
		if(null != suffix && suffix.startsWith(".")){
			BLADE_CONFIG.setViewSuffix(suffix);
		}
	}
	
	/**
	 * 同事设置视图所在目录和视图后缀名
	 * 
	 * @param viewPath	视图路径，如：/WEB-INF/views
	 * @param viewExt	视图后缀，如：.html	 .vm
	 */
	public static synchronized void view(final String viewPath, final String viewExt) {
		viewPrefix(viewPath);
		viewSuffix(viewExt);
	}
	
	/**
	 * 设置框架静态文件所在文件夹
	 * 
	 * @param folders
	 */
	public static synchronized void staticFolder(final String ... folders) {
		BLADE_CONFIG.setStaticFolders(folders);
	}
	
    /**
     * 动态设置全局初始化类
     * 
     * @param bladeApplication 	全局初始化bladeApplication
     */
    public static synchronized <T> void app(Bootstrap bootstrap){
    	Blade.bootstrap = bootstrap;
    }
    
    /**
     * 设置404视图页面
     * 
     * @param view404	404视图页面
     */
    public static synchronized void view404(final String view404){
    	BLADE_CONFIG.setView404(view404);
    }
    
    /**
     * 设置500视图页面
     * 
     * @param view500	500视图页面
     */
    public static synchronized void view500(final String view500){
    	BLADE_CONFIG.setView500(view500);
    }

    /**
     * 设置web根目录
     * 
     * @param webRoot	web根目录物理路径
     */
    public static synchronized void webRoot(final String webRoot){
    	BLADE_CONFIG.setWebRoot(webRoot);
    }
    
    /**
	 * 设置系统是否以debug方式运行
	 * @param isdebug	true:是，默认true；false:否
	 */
	public static synchronized void debug(boolean isdebug){
		BLADE_CONFIG.setDebug(isdebug);
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
	public static synchronized void regRoute(String path, Class<?> clazz, String methodName){
		RouteMatcherBuilder.buildFunctional(path, clazz, methodName, null);
	}
	
	/**
	 * 注册一个函数式的路由
	 * @param path			路由url	
	 * @param clazz			路由处理类
	 * @param methodName	路由处理方法名称
	 * @param httpMethod	请求类型,GET/POST
	 */
	public static synchronized void regRoute(String path, Class<?> clazz, String methodName, HttpMethod httpMethod){
		RouteMatcherBuilder.buildFunctional(path, clazz, methodName, httpMethod);
	}
	
	/**
	 * get请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void get(String path, Router routeHandler){
		RouteMatcherBuilder.buildHandler(path, routeHandler, HttpMethod.GET);
	}
	
	/**
	 * post请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void post(String path, Router routeHandler){
		RouteMatcherBuilder.buildHandler(path, routeHandler, HttpMethod.POST);
	}
	
	/**
	 * delete请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void delete(String path, Router routeHandler){
		RouteMatcherBuilder.buildHandler(path, routeHandler, HttpMethod.DELETE);
	}
	
	/**
	 * put请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void put(String path, Router routeHandler){
		RouteMatcherBuilder.buildHandler(path, routeHandler, HttpMethod.PUT);
	}
	
	/**
	 * patch请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void patch(String path, Router routeHandler){
		RouteMatcherBuilder.buildHandler(path, routeHandler, HttpMethod.PATCH);
	}
	
	/**
	 * head请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void head(String path, Router routeHandler){
		RouteMatcherBuilder.buildHandler(path, routeHandler, HttpMethod.HEAD);
	}
	
	/**
	 * trace请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void trace(String path, Router routeHandler){
		RouteMatcherBuilder.buildHandler(path, routeHandler, HttpMethod.TRACE);
	}
	
	/**
	 * options请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void options(String path, Router routeHandler){
		RouteMatcherBuilder.buildHandler(path, routeHandler, HttpMethod.OPTIONS);
	}
	
	/**
	 * connect请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void connect(String path, Router routeHandler){
		RouteMatcherBuilder.buildHandler(path, routeHandler, HttpMethod.CONNECT);
	}
	
	/**
	 * 任意请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void all(String path, Router routeHandler){
		RouteMatcherBuilder.buildHandler(path, routeHandler, HttpMethod.ALL);
	}
	
	/**
	 * 拦截器before请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void before(String path, Router routeHandler){
		RouteMatcherBuilder.buildInterceptor(path, routeHandler, HttpMethod.BEFORE);
	}
	
	/**
	 * 拦截器after请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void after(String path, Router routeHandler){
		RouteMatcherBuilder.buildInterceptor(path, routeHandler, HttpMethod.AFTER);
	}
	
	/**
	 * 注册一个函数式的拦截器</br>
	 * <p>
	 * 方法上指定请求类型，如：post:signin
	 * </p>
	 * @param path			路由url	
	 * @param clazz			路由处理类
	 * @param methodName	路由处理方法名称
	 */
	public static synchronized void regInterceptor(String path, Class<?> clazz, String methodName){
		RouteMatcherBuilder.buildInterceptor(path, clazz, methodName, null);
	}
	
	/**
	 * 注册一个函数式的拦截器</br>
	 * <p>
	 * 方法上指定请求类型，如：post:signin
	 * </p>
	 * @param path			路由url	
	 * @param clazz			路由处理类
	 * @param methodName	路由处理方法名称
	 * @param acceptType	acceptType
	 */
	public static synchronized void regInterceptor(String path, Class<?> clazz, String methodName, String acceptType){
		RouteMatcherBuilder.buildInterceptor(path, clazz, methodName, acceptType);
	}
	
	public final static BladeConfig config(){
    	return BLADE_CONFIG;
    }
	
    /**
     * @return	返回Blade要扫描的基础包
     */
    public static String basePackage(){
    	return BLADE_CONFIG.getBasePackage();
    }
    
	/**
     * @return	返回路由包数组
     */
    public static String[] routes(){
    	return BLADE_CONFIG.getRoutePackages();
    }
    
    /**
     * @return	返回IOC所有包
     */
    public static String[] iocs(){
    	return BLADE_CONFIG.getIocPackages();
    }
    
    /**
     * @return	返回拦截器包数组，只有一个元素 这里统一用String[]
     */
    public static String interceptor(){
    	return BLADE_CONFIG.getInterceptorPackage();
    }
    
    
    /**
     * @return	返回视图存放路径
     */
    public static String viewPrefix(){
    	return BLADE_CONFIG.getViewPrefix();
    }
    
    /**
     * @return	返回系统默认字符编码
     */
    public static String encoding(){
    	return BLADE_CONFIG.getEncoding();
    }
    
    /**
     * @return	返回balde启动端口
     */
    public static String viewSuffix(){
    	return BLADE_CONFIG.getViewSuffix();
    }
    
    /**
     * @return	返回404视图
     */
    public static String view404(){
    	return BLADE_CONFIG.getView404();
    }
    
    /**
     * @return	返回500视图
     */
    public static String view500(){
    	return BLADE_CONFIG.getView500();
    }
    
    /**
     * @return	返回webroot路径
     */
    public static String webRoot(){
    	return BLADE_CONFIG.getWebRoot();
    }
    
    /**
	 * @return	返回系统是否以debug方式运行
	 */
	public static boolean debug(){
		return BLADE_CONFIG.isDebug();
	}
	
	/**
	 * @return	返回静态资源目录
	 */
	public static String[] staticFolder(){
		return BLADE_CONFIG.getStaticFolders();
	}
	
	/**
	 * @return	返回Bootstrap对象
	 */
	public static Bootstrap bootstrap(){
		return bootstrap; 
	}
}
