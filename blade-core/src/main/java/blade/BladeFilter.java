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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import blade.kit.log.Logger;
import blade.route.RouteMatcherBuilder;

/**
 * blade核心过滤器，mvc总线
 * 匹配所有请求过滤
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class BladeFilter implements Filter {
	
	private static final Logger LOGGER = Logger.getLogger(BladeFilter.class);
	
	/**
	 * blade全局初始化类
	 */
    private static final String APPLCATION_CLASS = "applicationClass";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    	
    	// 防止重复初始化
    	try {
			if(!Blade.IS_INIT){
				
				Blade.webRoot(filterConfig.getServletContext().getRealPath("/"));
				
				BladeWebContext.servletContext(filterConfig.getServletContext());
				
				final Bootstrap bootstrap = getBootstrap(filterConfig.getInitParameter(APPLCATION_CLASS));
				bootstrap.init();
			    Blade.app(bootstrap);
			    
			    // 构建路由
			    RouteMatcherBuilder.building();
			    
			    IocApplication.init();
			    
			    bootstrap.contextInitialized(BladeWebContext.servletContext());
			    
			    Blade.init();
			    
			    LOGGER.info("blade init complete!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
    	
    }
    
    /**
     * 获取全局初始化对象，初始化应用
     * 
     * @param botstrapClassName 		全局初始化类名
     * @return 							一个全局初始化对象
     * @throws ServletException
     */
    private Bootstrap getBootstrap(String botstrapClassName) throws ServletException {
    	Bootstrap bootstrapClass = null;
        try {
        	if(null != botstrapClassName){
            	Class<?> applicationClass = Class.forName(botstrapClassName);
                if(null != applicationClass){
                	bootstrapClass = (Bootstrap) applicationClass.newInstance();
                }
        	} else {
        		throw new ServletException("bootstrapClass is null !");
			}
        } catch (Exception e) {
            throw new ServletException(e);
        }
		return bootstrapClass;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
        
    	HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        httpRequest.setCharacterEncoding(Blade.encoding());
        httpResponse.setCharacterEncoding(Blade.encoding());
        
        /**
         * 是否被RequestHandler执行
         */
        boolean isHandler = RequestHandler.single().handler(httpRequest, httpResponse);
        if(!isHandler){
        	chain.doFilter(httpRequest, httpResponse);
        }
    }	
	
    @Override
    public void destroy() {
    	IocApplication.destroy();
    	LOGGER.info("blade destroy!");
    }

}