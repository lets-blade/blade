package com.blade;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blade.context.BladeWebContext;
import com.blade.http.HttpStatus;
import com.blade.http.Path;
import com.blade.http.Request;
import com.blade.http.Response;
import com.blade.render.ModelAndView;
import com.blade.route.Route;
import com.blade.route.RouteBuilder;
import com.blade.route.RouteHandler;
import com.blade.route.RouteMatcher;
import com.blade.servlet.wrapper.ServletRequest;
import com.blade.servlet.wrapper.ServletResponse;

import blade.exception.BladeException;
import blade.kit.ReflectKit;
import blade.kit.StringKit;
import blade.kit.base.ThrowableKit;
import blade.kit.log.Logger;

public class DispatcherServlet extends HttpServlet {
	
	private static final long serialVersionUID = -2607425162473178733L;
	
	private static final Logger LOGGER = Logger.getLogger(DispatcherServlet.class);
	
	private Blade blade = Blade.me();
	
	private Bootstrap bootstrap; 
	
	private ServletContext servletContext;
	
	private RouteMatcher routeMatcher;
	
	public DispatcherServlet() {
	}
	
	public DispatcherServlet(Bootstrap bootstrap) {
		this.bootstrap = bootstrap;
		blade.setInit(true);
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		if(!blade.isInit){
			this.bootstrap = blade.bootstrap();
			if(null == bootstrap){
				String bootStrapClassName = config.getInitParameter("bootstrap");
				if(StringKit.isNotBlank(bootStrapClassName)){
					bootstrap = getBootstrap(bootStrapClassName);
				} else {
					bootstrap = new Bootstrap() {
						@Override
						public void init(Blade blade) {
						}
					}; 
				}
				blade.app(bootstrap);
			}
			bootstrap.init(blade);
			
		    // 构建路由
			new RouteBuilder(blade).building();
			
			// 初始化IOC
			blade.iocInit();
			
		    blade.setInit(true);
		    
		    routeMatcher = new RouteMatcher(blade.routers());
		    blade.bootstrap().contextInitialized(blade);
		    servletContext = config.getServletContext();
		    
		    LOGGER.info("blade init complete!");
		}
	}

	@Override
	protected void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		httpRequest.setCharacterEncoding(blade.encoding());
		httpResponse.setCharacterEncoding(blade.encoding());
        
        Response response = null;
        try {
        	
        	// http方法, GET/POST ...
            String method = httpRequest.getMethod();
            
            // 请求的uri
            String uri = Path.getRelativePath(httpRequest);
            
            // 如果是静态资源则交给filter处理
            if(null != blade.staticFolder() && blade.staticFolder().length > 0){
            	if(!filterStaticFolder(uri)){
            		return;
            	}
            }
            
            if(blade.debug()){
            	LOGGER.debug("Request : " + method + "\t" + uri);
            }
        	
            // 创建请求对象
    		Request request = new ServletRequest(httpRequest);
            
    		// 创建响应对象
            response = new ServletResponse(httpResponse, blade.render());
            
            // 初始化context
         	BladeWebContext.setContext(servletContext, request, response);
         	
			Route route = routeMatcher.getRoute(method, uri);
			
			// 如果找到
			if (route != null) {
				// 执行before拦截
				List<Route> befores = routeMatcher.getBefore(uri);
				invokeInterceptor(request, response, befores);
				
				// 实际执行方法
				handle(request, response, route);
				
				// 执行after拦截
				List<Route> afters = routeMatcher.getAfter(uri);
				invokeInterceptor(request, response, afters);
				return;
			}
			
			// 没有找到
			render404(response, uri);
			return;
		} catch (BladeException bex) {
			
			String error = ThrowableKit.getStackTraceAsString(bex);
            LOGGER.error(error);
            ThrowableKit.propagate(bex);
			
            httpResponse.setStatus(500);
            // 写入内容到浏览器
            if (!httpResponse.isCommitted()) {
                response.html(Const.INTERNAL_ERROR);
                return;
            }
            
        } catch (Exception e) {
        	
        	String error = ThrowableKit.getStackTraceAsString(e);
            LOGGER.error(error);
            ThrowableKit.propagate(e);
            
        	httpResponse.setStatus(500);
        	// 写入内容到浏览器
            if (!httpResponse.isCommitted()) {
                response.html(Const.INTERNAL_ERROR);
                return;
            }
        }
        return;
	}
	
	/**
	 * 404视图渲染
	 * 
	 * @param response		响应对象
	 * @param uri			404的URI
	 */
	private void render404(Response response, String uri) {
		String view404 = blade.view404();
    	if(StringKit.isNotBlank(view404)){
    		ModelAndView modelAndView = new ModelAndView(view404);
    		modelAndView.add("viewName", uri);
    		response.render( modelAndView );
    	} else {
    		response.status(HttpStatus.NOT_FOUND);
    		response.html(String.format(Const.VIEW_NOTFOUND, uri));
		}
	}
	
	/**
	 * 执行拦截器的方法
	 * 
	 * @param request		请求对象
	 * @param response		响应对象
	 * @param interceptors	要执行的拦截器列表
	 */
	private void invokeInterceptor(Request request, Response response, List<Route> interceptors) {
		for(Route route : interceptors){
			handle(request, response, route);
		}
	}

	/**
	 * 实际的路由方法执行
	 * 
	 * @param request	请求对象
	 * @param response	响应对象
	 * @param route		路由对象
	 */
	private void handle(Request request, Response response, Route route){
		Object target = route.getTarget();
		request.initPathParams(route.getPath());
		
		// 初始化context
		BladeWebContext.setContext(servletContext, request, response);
		if(target instanceof RouteHandler){
			RouteHandler routeHandler = (RouteHandler)target;
			routeHandler.handle(request, response);
		} else {
			// 要执行的路由方法
			Method actionMethod = route.getAction();
			// 执行route方法
			executeMethod(target, actionMethod, request, response);
		}
	}
	
	/**
	 * 获取方法内的参数
	 * 
	 * @param request		Request对象，用于注入到method参数列表中
	 * @param response		Response对象，用于注入到method参数列表中
	 * @param params		params参数列表
	 * @return				返回生成后的参数数组
	 */
	private Object[] getArgs(Request request, Response response, Class<?>[] params){
		
		int len = params.length;
		Object[] args = new Object[len];
		
		for(int i=0; i<len; i++){
			Class<?> paramTypeClazz = params[i];
			if(paramTypeClazz.getName().equals(Request.class.getName())){
				args[i] = request;
			}
			if(paramTypeClazz.getName().equals(Response.class.getName())){
				args[i] = response;
			}
		}
		
		return args;
	}
	
	/**
	 * 执行路由方法
	 * 
	 * @param object		方法的实例，即该方法所在类的对象
	 * @param method		要执行的method
	 * @param request		Request对象，作为参数注入
	 * @param response		Response对象，作为参数注入
	 * @return				返回方法执行后的返回值
	 */
	private Object executeMethod(Object object, Method method, Request request, Response response){
		int len = method.getParameterTypes().length;
		method.setAccessible(true);
		if(len > 0){
			Object[] args = getArgs(request, response, method.getParameterTypes());
			return ReflectKit.invokeMehod(object, method, args);
		} else {
			return ReflectKit.invokeMehod(object, method);
		}
	}
	
	/**
	 * 要过滤掉的目录
	 * 
	 * @param uri	URI表示当前路径，在静态目录中进行过滤
	 * @return		返回false，过滤成功；返回true，不过滤
	 */
	private boolean filterStaticFolder(String uri){
		int len = blade.staticFolder().length;
    	for(int i=0; i<len; i++){
    		if(uri.startsWith(blade.staticFolder()[i])){
    			return false;
    		}
    	}
    	return true;
	}
	
	/**
     * 获取全局初始化对象，初始化应用
     * 
     * @param botstrapClassName 		全局初始化类名
     * @return 							一个全局初始化对象
     * @throws ServletException
     */
    @SuppressWarnings("unchecked")
	private Bootstrap getBootstrap(String botstrapClassName) throws ServletException {
    	Bootstrap bootstrapClass = null;
        try {
        	if(null != botstrapClassName){
            	Class<Bootstrap> applicationClass = (Class<Bootstrap>) Class.forName(botstrapClassName);
                if(null != applicationClass){
                	bootstrapClass = Aop.create(applicationClass);
                }
        	} else {
        		throw new ServletException("bootstrapClass is null !");
			}
        } catch (Exception e) {
            throw new ServletException(e);
        }
		return bootstrapClass;
    }
}
