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

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import blade.ioc.Container;
import blade.ioc.DefaultContainer;
import blade.render.Render;
import blade.render.RenderFactory;
import blade.route.DefaultRouteMatcher;
import blade.server.BladeServer;

/**
 * Blade的基础类
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
abstract class BladeBase {
	
	/**
	 * 默认路由后缀包，用户扫描路由所在位置，默认为route，用户可自定义
	 */
	public static String PACKAGE_ROUTE = "route";
	
	protected static final String DEFAULT_ACCEPT_TYPE = "*/*";
	
	/**
	 * 默认拦截器后缀包，用户扫描拦截器所在位置，默认为interceptor，用户可自定义
	 */
	public static String PACKAGE_INTERCEPTOR = "interceptor";
	
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	
	/**
	 * 是否以jetty方式运行
	 */
	public static boolean runJetty = false;
	
	/**
	 * web应用根目录，应用启动时载入
	 */
	private static String WEB_ROOT = "";
    
	/**
     * Blade默认编码，可修改
     */
    protected static String ENCODING = UTF_8.toString();
    
    /**
     * 默认视图的路径，默认渲染引擎为JSP,设置WEB-INF目录更安全，可配置
     */
    protected static String VIEW_PATH = "/WEB-INF/";
    
    /**
	 * 静态资源所在文件夹
	 */
	protected static String[] STATIC_FOLDER = null;
    
    /**
     * 框架是否已经初始化
     */
    protected static boolean IS_INIT = false;
    
    /**
	 * 默认视图文件后缀名
	 */
	protected static String VIEW_EXT = ".jsp";
	
	/**
     * blade全局初始化对象，在web.xml中配置，必须
     */
    protected static BladeApplication bladeApplication;

    /**
     * 路由匹配器，用于添加，删除，查找路由
     */
    protected static DefaultRouteMatcher routeMatcher;
    
    /**
     * 存放要扫描的包map
     */
    protected static final Map<PackageNames, String[]> packageMap = new HashMap<PackageNames, String[]>();
    
    /**
     * 默认的404视图
     */
    protected static String VIEW_404 = null;
    
    /**
     * 默认的500视图
     */
    protected static String VIEW_500 = null;
    
    /**
     * jetty启动的默认端口
     */
    protected static int PORT = 9000;
    
    protected static boolean DEBUG = true;
    
    /**
     * IOC容器，存储路由到ioc中
     */
    private final static Container container = DefaultContainer.single();
    
    /**
     * 包类型枚举
     * 
     * basepackge		基础包，默认的路由，拦截器包
     * route			路由包，所有路由所在包，可递归
     * interceptor		拦截器包，所有拦截器所在包，不可递归
     * ioc				IOC对象所在包，可递归
     * @author biezhi
     *
     */
    public enum PackageNames {
    	basepackge, route, interceptor, ioc
    }
    
    protected BladeBase() {
	}
    
    /*--------------------SET CONST:START-------------------------*/
    
    /**
     * 设置路由包，如：com.baldejava.route
     * 可传入多个包，所有的路由类都在该包下
     * 
     * @param pckages 	路由包路径
     */
    public static synchronized void routes(String...pckages){
    	if(null != pckages && pckages.length >0){
    		packageMap.put(PackageNames.route, pckages);
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
    		packageMap.put(PackageNames.basepackge, new String[]{basePackage});
    	}
    }
    
    /**
     * 设置拦截器所在的包路径，如：com.bladejava.interceptor
     * 
     * @param packageName 拦截器所在的包
     */
	public static synchronized void interceptor(String packageName) {
		if(null != packageName && packageName.length() >0){
    		packageMap.put(PackageNames.interceptor, new String[]{packageName});
    	}
	}
	
	/**
     * 设置依赖注入包，如：com.bladejava.service
     * 
     * @param pckages 	所有需要做注入的包，可传入多个
     */
    public static synchronized void ioc(String...pckages){
    	if(null != pckages && pckages.length >0){
    		packageMap.put(PackageNames.ioc, pckages);
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
	 * 设置默认视图路径，默认为WEB_ROOT/WEB-INF目录
	 * 
	 * @param viewPath 	视图路径，如：/WEB-INF/views
	 */
	public static synchronized void viewPath(final String viewPath) {
		if(null != viewPath && viewPath.startsWith("/")){
			VIEW_PATH = viewPath;
		}
	}
	
	/**
	 * 设置视图默认后缀名，默认为.jsp
	 * 
	 * @param viewExt	视图后缀，如：.html	 .vm
	 */
	public static synchronized void viewExt(final String viewExt) {
		if(null != viewExt && viewExt.startsWith(".")){
			VIEW_EXT = viewExt;
		}
	}
	
	/**
	 * 同事设置视图所在目录和视图后缀名
	 * 
	 * @param viewPath	视图路径，如：/WEB-INF/views
	 * @param viewExt	视图后缀，如：.html	 .vm
	 */
	public static synchronized void view(final String viewPath, final String viewExt) {
		viewPath(viewPath);
		viewExt(viewExt);
	}
	
	/**
	 * 设置框架静态文件所在文件夹
	 * 
	 * @param folder
	 */
	public static synchronized void staticFolder(final String ... folder) {
		STATIC_FOLDER = folder;
	}
	
    /**
     * 动态设置全局初始化类
     * 
     * @param clazz 	全局初始化Class
     */
    public static synchronized void app(Class<? extends BladeApplication> clazz){
    	try {
			BladeBase.bladeApplication = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
    }
    /**
     * 动态设置全局初始化类
     * 
     * @param bladeApplication 	全局初始化bladeApplication
     */
    public static synchronized <T> void app(BladeApplication bladeApplication){
    	BladeBase.bladeApplication = bladeApplication;
    }
    
    /**
     * 设置404视图页面
     * 
     * @param view404	404视图页面
     */
    public static synchronized void view404(final String view404){
    	BladeBase.VIEW_404 = view404;
    }
    
    /**
     * 设置500视图页面
     * 
     * @param view500	500视图页面
     */
    public static synchronized void view500(final String view500){
    	BladeBase.VIEW_500 = view500;
    }

    /**
     * 设置web根目录
     * 
     * @param webRoot	web根目录物理路径
     */
    public static synchronized void webRoot(final String webRoot){
    	BladeBase.WEB_ROOT = webRoot;
    }
    
    /**
	 * 设置系统是否以debug方式运行
	 * @param isdebug	true:是，默认true；false:否
	 */
	public static synchronized void debug(boolean isdebug){
		BladeBase.DEBUG = isdebug;
	}
	
    /**--------------------SET CONST:END-------------------------*/
    
    
    
    /**--------------------GET CONST:START-------------------------*/
    
    /**
     * @return	返回Blade要扫描的基础包
     */
    public static String[] defaultRoutes(){
    	return packageMap.get(PackageNames.basepackge);
    }
    
	/**
     * @return	返回路由包数组
     */
    public static String[] routes(){
    	return packageMap.get(PackageNames.route);
    }
    
    /**
     * @return	返回拦截器包数组，只有一个元素 这里统一用String[]
     */
    public static String[] interceptor(){
    	return packageMap.get(PackageNames.interceptor);
    }
    
    /**
     * @return	返回视图存放路径
     */
    public static String viewPath(){
    	return VIEW_PATH;
    }
    
    /**
     * @return	返回系统默认字符编码
     */
    public static String encoding(){
    	return ENCODING;
    }
    
    /**
     * @return	返回balde启动端口
     */
    public static String viewExt(){
    	return VIEW_EXT;
    }
    
    /**
     * @return	返回404视图
     */
    public static String view404(){
    	return BladeBase.VIEW_404;
    }
    
    /**
     * @return	返回500视图
     */
    public static String view500(){
    	return BladeBase.VIEW_500;
    }
    
    /**
     * @return	返回webroot路径
     */
    public static String webRoot(){
    	return BladeBase.WEB_ROOT;
    }
    
    /**
	 * @return	返回系统是否以debug方式运行
	 */
	public static boolean debug(){
		return BladeBase.DEBUG;
	}
	
	/**
	 * @return	返回静态资源目录
	 */
	public static String[] staticFolder(){
		return BladeBase.STATIC_FOLDER;
	}
	
	/**
	 * @return	返回BladeApplication对象
	 */
	public static BladeApplication application(){
		return bladeApplication; 
	}
	
    /**--------------------GET CONST:END-------------------------*/
    
    
    /**----------------------jetty:START-------------------------*/
    
    /**
     * 设置jetty启动端口
     * 
     * @param port		端口号，范围在0~65535之间，默认为9000
     */
    public static synchronized void port(int port){
    	if(port > 0 && port < 65535){
    		PORT = port;
    	}
    }
    
    /**
     * 运行jetty服务
     * 
     * @param port		端口号，范围在0~65535之间，默认为9000
     * @param host		host，默认为本机；127.0.0.1/localhost
     * @param context	context，应用上下文，默认为"/"
     */
	public static void run(Integer port, String host, String context) {
		PORT = port;
		BladeServer.run(port, host, context);
	}
	
	/**
	 * 运行jetty服务
	 */
	public static void run() {
		run(PORT, null, null);
	}
	
	/**
	 * 运行jetty服务并设置主机
	 * 
	 * @param host		host，默认为本机；127.0.0.1/localhost
	 */
	public static void run(String host) {
		run(PORT, host, null);
	}
	
	/**
	 * 运行jetty服务并设置端口
	 * 
	 * @param port		端口号，范围在0~65535之间，默认为9000
	 */
	public static void run(Integer port) {
		run(port, null, null);
	}
	
	public static void run(Class<? extends BladeApplication> clazz, Integer port) {
		if(null != clazz){
			app(clazz);
		}
		run(port, null, null);
	}
	
	/**
	 * 运行jetty服务并设置端口和主机
	 * 
	 * @param host		host，默认为本机；127.0.0.1/localhost
	 * @param port		端口号，范围在0~65535之间，默认为9000
	 */
	public static void run(String host, Integer port) {
		run(port, host, null);
	}
	/**----------------------jetty:END-------------------------*/
	
	/**
	 * 手动注册一个对象到ioc容器中
	 * 
	 * @param object	要注册的object
	 */
	public static synchronized void register(Object object){
		container.registBean(object);
	}
	
	static synchronized void init() {
        BladeBase.IS_INIT = true;
    }
	
}