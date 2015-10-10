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
import blade.plugin.Plugin;
import blade.render.Render;
import blade.render.RenderFactory;
import blade.route.HttpMethod;
import blade.route.RouteBase;
import blade.route.RouteMatcherBuilder;
import blade.route.RouteHandler;
import blade.route.RouterExecutor;

/**
 * Blade Core Class
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class Blade {
	
	public static final String VERSION = "1.2.9";
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
	protected final static Config CONFIG = new Config();
	
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
		new Configurator(CONFIG, configMap).run();
	}
	
	/**
     * 设置路由包，如：com.baldejava.route
     * 可传入多个包，所有的路由类都在该包下
     * 
     * @param packages 	路由包路径
     */
    public static synchronized void routes(String...packages){
    	if(null != packages && packages.length >0){
    		CONFIG.setRoutePackages(packages);
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
    		CONFIG.setBasePackage(basePackage);
    	}
    }
    
    /**
     * 设置拦截器所在的包路径，如：com.bladejava.interceptor
     * 
     * @param packageName 拦截器所在的包
     */
	public static synchronized void interceptor(String packageName) {
		if(null != packageName && packageName.length() >0){
			CONFIG.setInterceptorPackage(packageName);
    	}
	}
	
	/**
     * 设置依赖注入包，如：com.bladejava.service
     * 
     * @param packages 	所有需要做注入的包，可传入多个
     */
    public static synchronized void ioc(String...packages){
    	if(null != packages && packages.length >0){
    		CONFIG.setIocPackages(packages);
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
			CONFIG.setViewPrefix(prefix);
		}
	}
	
	/**
	 * 设置视图默认后缀名，默认为.jsp
	 * 
	 * @param viewExt	视图后缀，如：.html	 .vm
	 */
	public static synchronized void viewSuffix(final String suffix) {
		if(null != suffix && suffix.startsWith(".")){
			CONFIG.setViewSuffix(suffix);
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
		CONFIG.setStaticFolders(folders);
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
    	CONFIG.setView404(view404);
    }
    
    /**
     * 设置500视图页面
     * 
     * @param view500	500视图页面
     */
    public static synchronized void view500(final String view500){
    	CONFIG.setView500(view500);
    }

    /**
     * 设置web根目录
     * 
     * @param webRoot	web根目录物理路径
     */
    public static synchronized void webRoot(final String webRoot){
    	CONFIG.setWebRoot(webRoot);
    }
    
    /**
	 * 设置系统是否以debug方式运行
	 * @param isdebug	true:是，默认true；false:否
	 */
	public static synchronized void debug(boolean isdebug){
		CONFIG.setDebug(isdebug);
	}
	
	/**
	 * 加载一个Route
	 * @param route
	 */
	public static synchronized void load(Class<? extends RouteBase> route){
		IocApplication.addRouteClass(route);
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
	public static synchronized void register(String path, Class<?> clazz, String methodName){
		RouteMatcherBuilder.buildFunctional(path, clazz, methodName, null);
	}
	
	/**
	 * 注册一个函数式的路由
	 * @param path			路由url	
	 * @param clazz			路由处理类
	 * @param methodName	路由处理方法名称
	 * @param httpMethod	请求类型,GET/POST
	 */
	public static synchronized void register(String path, Class<?> clazz, String methodName, HttpMethod httpMethod){
		RouteMatcherBuilder.buildFunctional(path, clazz, methodName, httpMethod);
	}
	
	/**
	 * get请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void get(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.GET);
	}
	
	/**
	 * get请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor get(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.GET);
		}
		return null;
	}
	
	/**
	 * post请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void post(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.POST);
	}
	
	/**
	 * post请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor post(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.POST);
		}
		return null;
	}
	
	/**
	 * delete请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void delete(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.DELETE);
	}
	
	/**
	 * delete请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor delete(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.DELETE);
		}
		return null;
	}
	
	/**
	 * put请求
	 * @param paths
	 */
	public static synchronized void put(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.PUT);
	}
	
	/**
	 * put请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor put(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.PUT);
		}
		return null;
	}
	
	/**
	 * patch请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void patch(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.PATCH);
	}

	/**
	 * patch请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor patch(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.PATCH);
		}
		return null;
	}
	
	/**
	 * head请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void head(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.HEAD);
	}
	
	/**
	 * head请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor head(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.HEAD);
		}
		return null;
	}
	
	/**
	 * trace请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void trace(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.TRACE);
	}
	
	/**
	 * trace请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor trace(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.TRACE);
		}
		return null;
	}
	
	/**
	 * options请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void options(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.OPTIONS);
	}
	
	/**
	 * options请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor options(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.OPTIONS);
		}
		return null;
	}
	
	/**
	 * connect请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void connect(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.CONNECT);
	}
	
	/**
	 * connect请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor connect(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.CONNECT);
		}
		return null;
	}
	
	/**
	 * 任意请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void all(String path, RouteHandler router){
		RouteMatcherBuilder.buildHandler(path, router, HttpMethod.ALL);
	}
	
	/**
	 * all请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor all(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.ALL);
		}
		return null;
	}
	
	/**
	 * 拦截器before请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void before(String path, RouteHandler routeHandler){
		RouteMatcherBuilder.buildInterceptor(path, routeHandler, HttpMethod.BEFORE);
	}

	/**
	 * before请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor before(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.BEFORE);
		}
		return null;
	}
	
	/**
	 * 拦截器after请求
	 * @param path
	 * @param routeHandler
	 */
	public static synchronized void after(String path, RouteHandler routeHandler){
		RouteMatcherBuilder.buildInterceptor(path, routeHandler, HttpMethod.AFTER);
	}
	
	/**
	 * after请求，多个路由
	 * @param paths
	 */
	public static synchronized RouterExecutor after(String... paths){
		if(null != paths && paths.length > 0){
			return new RouterExecutor(paths, HttpMethod.AFTER);
		}
		return null;
	}
	
	public final static Config config(){
    	return CONFIG;
    }
	
    /**
     * @return	返回Blade要扫描的基础包
     */
    public static String basePackage(){
    	return CONFIG.getBasePackage();
    }
    
	/**
     * @return	返回路由包数组
     */
    public static String[] routes(){
    	return CONFIG.getRoutePackages();
    }
    
    /**
     * @return	返回IOC所有包
     */
    public static String[] iocs(){
    	return CONFIG.getIocPackages();
    }
    
    /**
     * @return	返回拦截器包数组，只有一个元素 这里统一用String[]
     */
    public static String interceptor(){
    	return CONFIG.getInterceptorPackage();
    }
    
    
    /**
     * @return	返回视图存放路径
     */
    public static String viewPrefix(){
    	return CONFIG.getViewPrefix();
    }
    
    /**
     * @return	返回系统默认字符编码
     */
    public static String encoding(){
    	return CONFIG.getEncoding();
    }
    
    /**
     * @return	返回balde启动端口
     */
    public static String viewSuffix(){
    	return CONFIG.getViewSuffix();
    }
    
    /**
     * @return	返回404视图
     */
    public static String view404(){
    	return CONFIG.getView404();
    }
    
    /**
     * @return	返回500视图
     */
    public static String view500(){
    	return CONFIG.getView500();
    }
    
    /**
     * @return	返回webroot路径
     */
    public static String webRoot(){
    	return CONFIG.getWebRoot();
    }
    
    /**
	 * @return	返回系统是否以debug方式运行
	 */
	public static boolean debug(){
		return CONFIG.isDebug();
	}
	
	/**
	 * @return	返回静态资源目录
	 */
	public static String[] staticFolder(){
		return CONFIG.getStaticFolders();
	}
	
	/**
	 * @return	返回Bootstrap对象
	 */
	public static Bootstrap bootstrap(){
		return bootstrap; 
	}
	
	/**
	 * 返回插件对象
	 * @param pluginClazz	插件class
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Plugin> T plugin(Class<T> pluginClazz){
		Object object = IocApplication.getPlugin(pluginClazz);
		if(null == object){
			object = IocApplication.registerPlugin(pluginClazz);
		}
		return (T) object;
	}
	
}
