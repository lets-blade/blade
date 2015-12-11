package com.blade.web;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import blade.exception.BladeException;
import blade.kit.StringKit;
import blade.kit.base.ThrowableKit;
import blade.kit.log.Logger;

import com.blade.Blade;
import com.blade.Const;
import com.blade.context.BladeWebContext;
import com.blade.render.ModelAndView;
import com.blade.route.Route;
import com.blade.route.RouteHandler;
import com.blade.route.RouteMatcher;
import com.blade.route.Routers;
import com.blade.web.http.HttpStatus;
import com.blade.web.http.Path;
import com.blade.web.http.Request;
import com.blade.web.http.Response;
import com.blade.web.http.wrapper.ServletRequest;
import com.blade.web.http.wrapper.ServletResponse;

/**
 * 同步请求处理器
 * @author biezhi
 */
public class SyncRequestHandler {
	
	private static final Logger LOGGER = Logger.getLogger(SyncRequestHandler.class);

	private Blade blade = Blade.me();
	
	private ServletContext servletContext;
	
	private RouteMatcher routeMatcher;
	
	public SyncRequestHandler(ServletContext servletContext, Routers routers) {
		this.servletContext = servletContext;
		this.routeMatcher = new RouteMatcher(routers);
	}
	
	public void handle(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
		
		Response response = null;
        try {
        	// http方法, GET/POST ...
            String method = httpRequest.getMethod();
            
            // 请求的uri
            String uri = Path.getRelativePath(httpRequest.getRequestURI(), servletContext.getContextPath());
            
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
			RouteArgument.executeMethod(target, actionMethod, request, response, route);
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
	
}
