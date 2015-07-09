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
package blade.servlet;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import blade.kit.log.Logger;
import blade.render.ModelAndView;
import blade.render.Render;
import blade.render.RenderFactory;

/**
 * HttpServletResponse响应包装类
 * <p>
 * 封装HttpServletResponse的一些方法
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Response {

    private static final Logger LOGGER = Logger.getLogger(Response.class);
    
    private HttpServletResponse response;
    
    // 默认JSP引擎
 	private Render render = RenderFactory.getRender();
 	
    private String body;

    protected Response() {
    }

    public Response(HttpServletResponse response) {
        this.response = response;
    }


    /**
     * 设置响应状态码
     *
     * @param statusCode 状态码
     */
    public void status(int statusCode) {
        response.setStatus(statusCode);
    }

    /**
     * 设置contentType
     *
     * @param contentType contentType
     */
    public void contentType(String contentType) {
        response.setContentType(contentType);
    }

    /**
     * 设置响应内容
     *
     * @param body 响应主体
     */
    public void body(String body) {
        this.body = body;
    }

    /**
     * @return 返回响应体内容
     */
    public String body() {
        return this.body;
    }
    
    /**
     * @return 返回原生HttpServletResponse
     */
    public HttpServletResponse servletResponse() {
        return response;
    }

    /**
     * 重定向到location
     * 
     * @param location		重定向的location
     */
    public void redirect(String location) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Redirecting ({} {} to {}", "Found", HttpServletResponse.SC_FOUND, location);
        }
        try {
            response.sendRedirect(location);
        } catch (IOException ioException) {
            LOGGER.warn("Redirect failure", ioException);
        }
    }

    /**
     * 重定向并修改响应码
     * 
     * @param location		重定向的location
     * @param statusCode	状态码
     */
    public void redirect(String location, int statusCode) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Redirecting ({} to {}", statusCode, location);
        }
        response.setStatus(statusCode);
        response.setHeader("Location", location);
        response.setHeader("Connection", "close");
        try {
            response.sendError(statusCode);
        } catch (IOException e) {
            LOGGER.warn("Exception when trying to redirect permanently", e);
        }
    }

    /**
     * 设置响应头
     * 
     * @param header		头信息键
     * @param value			头信息值
     */
    public void header(String header, String value) {
        response.addHeader(header, value);
    }

    /**
     * 设置cookie
     * 
     * @param name			cookie name
     * @param value			cookie value
     */
    public void cookie(String name, String value) {
        cookie(name, value, -1, false);
    }

    /**
     * 设置cookie
     * 
     * @param name			cookie name
     * @param value			cookie name
     * @param maxAge		cookie有效期
     */
    public void cookie(String name, String value, int maxAge) {
        cookie(name, value, maxAge, false);
    }

    /**
     * 设置cookie
     * 
     * @param name			cookie name
     * @param value			cookie name
     * @param maxAge		cookie有效期
     * @param secured		是否SSL
     */
    public void cookie(String name, String value, int maxAge, boolean secured) {
        cookie("", name, value, maxAge, secured);
    }

    /**
     * 设置cookie
     * @param path			cookie所在域
     * @param name			cookie name
     * @param value			cookie name
     * @param maxAge		cookie有效期
     * @param secured		是否SSL
     */
    public void cookie(String path, String name, String value, int maxAge, boolean secured) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(secured);
        response.addCookie(cookie);
    }

    /**
     * 移除cookie
     * 
     * @param name			要移除的cookie name
     */
    public void removeCookie(String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    
    /**
	 * 渲染一个视图
	 * @param		view
	 */
	public void render(String view){
		render.render(view);
	}
	
	/**
	 * 根据ModelAndView进行渲染
	 * @param		modelAndView
	 */
	public void render(ModelAndView modelAndView){
		render.render(modelAndView);
	}
	
	/**
	 * 返回文字格式
	 * @param text
	 */
	public void text(String text){
		render.text(text);
	}
	
	/**
	 * 返回json格式
	 * @param json
	 */
	public void json(String json){
		render.json(json);
	}
	
	/**
	 * 返回xml格式
	 * @param xml
	 */
	public void xml(String xml){
		render.xml(xml);
	}
	
	/**
	 * 返回js格式
	 * @param javascript
	 */
	public void javascript(String javascript){
		render.javascript(javascript);
	}
	
	/**
	 * 返回html格式
	 * @param html
	 */
	public void html(String html){
		render.html(html);
	}
	
	/**
	 * 404默认视图
	 * 
	 * @param viewName
	 */
	public void render404(String viewName){
		render.render404(this.response, viewName);
	}
	
	/**
	 * 500默认视图
	 * 
	 * @param bodyContent
	 */
	public void render500(String bodyContent){
		render.render500(bodyContent);
	}
}
