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
    	if(!Blade.IS_INIT){
    		
    		BladeBase.webRoot(filterConfig.getServletContext().getRealPath("/"));
        	
        	BladeWebContext.servletContext(filterConfig.getServletContext());
        	
            final BladeApplication application = getApplication(filterConfig);
            application.init();
            Blade.app(application);
            
            // 构建所有路由
            RequestHandler.routeMatcher = RouteMatcherBuilder.building();
            
            // 全局初始化
            IocApplication.init();
            
            application.contextInitialized(BladeWebContext.servletContext());
            
            LOGGER.info("blade init complete!");
            BladeBase.init();
    	}
    	
    }
    
    /**
     * 获取全局初始化对象，初始化应用
     * 
     * @param filterConfig 		过滤器配置对象
     * @return 					一个全局初始化对象
     * @throws ServletException
     */
    private BladeApplication getApplication(FilterConfig filterConfig) throws ServletException {
        try {
        	String applicationClassName = filterConfig.getInitParameter(APPLCATION_CLASS);
        	if(!BladeBase.runJetty && null != applicationClassName){
            	Class<?> applicationClass = Class.forName(applicationClassName);
                return (BladeApplication) applicationClass.newInstance();
        	}
        	return BladeBase.bladeApplication;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
        
    	HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        httpRequest.setCharacterEncoding(BladeBase.encoding());
        httpResponse.setCharacterEncoding(BladeBase.encoding());
        
        /**
         * 是否被RequestHandler执行
         */
        boolean isHandler = RequestHandler.single().handler(httpRequest, httpResponse);
        if(!isHandler && !httpResponse.isCommitted()){
        	chain.doFilter(httpRequest, httpResponse);
        }
    }	
	
    @Override
    public void destroy() {
    	IocApplication.destroy();
    	LOGGER.info("blade destroy!");
    }

}
