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
package com.blade.web.http.wrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.context.ApplicationWebContext;
import com.blade.kit.Assert;
import com.blade.view.ModelAndView;
import com.blade.view.ViewSettings;
import com.blade.view.parser.JSONParser;
import com.blade.view.template.TemplateEngine;
import com.blade.view.template.TemplateException;
import com.blade.web.DispatchKit;
import com.blade.web.http.HttpStatus;
import com.blade.web.http.Path;
import com.blade.web.http.Request;
import com.blade.web.http.Response;

/**
 * ServletResponse
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.5
 */
public class ServletResponse implements Response {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServletResponse.class);
	
	private HttpServletResponse response;
	
	private boolean written = false;
	
	private ViewSettings viewSettings;
	
	private TemplateEngine templateEngine;
	
	private JSONParser jsonParser;
			
	public ServletResponse(HttpServletResponse response) {
		this.response = response;
		this.viewSettings = ViewSettings.$();
		this.templateEngine = viewSettings.templateEngine();
		this.jsonParser = viewSettings.JSONParser();
	}
	
	@Override
	public HttpServletResponse raw() {
		return response;
	}

	@Override
	public int status() {
		return response.getStatus();
	}

	@Override
	public Response status(int status) {
		response.setStatus(status);
		return this;
	}

	@Override
	public Response badRequest() {
		response.setStatus(HttpStatus.BAD_REQUEST);
		return this;
	}

	@Override
	public Response unauthorized() {
		response.setStatus(HttpStatus.UNAUTHORIZED);
		return this;
	}

	@Override
	public Response notFound() {
		response.setStatus(HttpStatus.NOT_FOUND);
		return this;
	}

	@Override
	public Response conflict() {
		response.setStatus(HttpStatus.CONFLICT);
		return this;
	}

	@Override
	public String contentType() {
		return response.getContentType();
	}

	@Override
	public Response contentType(String contentType) {
		response.setContentType(contentType);
		return this;
	}

	@Override
	public String header(String name) {
		return response.getHeader(name);
	}

	@Override
	public Response header(String name, String value) {
		response.setHeader(name, value);
		return this;
	}

	@Override
	public Response cookie(Cookie cookie) {
		response.addCookie(cookie);
		return this;
	}

	@Override
	public Response cookie(String name, String value) {
		return cookie(name, value);
	}

	@Override
	public Response cookie(String name, String value, int maxAge) {
		return cookie(name, value, maxAge, false);
	}

	@Override
	public Response cookie(String name, String value, int maxAge, boolean secured) {
		return cookie(null, name, value, maxAge, secured);
	}

	@Override
	public Response cookie(String path, String name, String value, int maxAge, boolean secured) {
		Cookie cookie = new Cookie(name, value);
		if(null != path){
			cookie.setPath(path);
		}
        cookie.setMaxAge(maxAge);
        cookie.setSecure(secured);
        response.addCookie(cookie);
		return this;
	}
	
	@Override
	public Response removeCookie(Cookie cookie) {
        cookie.setMaxAge(0);
        response.addCookie(map(cookie));
		return this;
	}

	javax.servlet.http.Cookie map(Cookie cookie) {
		javax.servlet.http.Cookie servletCookie = new javax.servlet.http.Cookie(cookie.getName(), cookie.getValue());
		servletCookie.setMaxAge(cookie.getMaxAge());
		if (cookie.getPath() != null) {
			servletCookie.setPath(cookie.getPath());
		}
		if (cookie.getDomain() != null) {
			servletCookie.setDomain(cookie.getDomain());
		}
		servletCookie.setHttpOnly(cookie.isHttpOnly());
        servletCookie.setSecure(cookie.getSecure());
		return servletCookie;
	}
	
	@Override
	public Response removeCookie(String name) {
		Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
		return this;
	}
	
	@Override
	public Response text(String text) {
		try {
			response.setHeader("Cache-Control", "no-cache");
    		response.setContentType("text/plain;charset=utf-8");
			DispatchKit.print(text, response.getWriter());
			this.written = true;
			return this;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Response html(String html) {
		try {
			response.setHeader("Cache-Control", "no-cache");
			response.setContentType("text/html;charset=utf-8");
			
			PrintWriter writer = response.getWriter();
			DispatchKit.print(html, writer);
    		this.written = true;
			return this;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public Response json(String json) {
		Request request = ApplicationWebContext.request();
		String userAgent = request.userAgent();
		if (userAgent.contains("MSIE")) {
			response.setContentType("text/html;charset=utf-8");
		} else {
			response.setContentType("application/json;charset=utf-8");
		}
		try {
			response.setHeader("Cache-Control", "no-cache");
			PrintWriter writer = response.getWriter();
			DispatchKit.print(json, writer);
    		this.written = true;
			return this;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Response json(Object bean) {
		return this.json(jsonParser.toJSONSting(bean));
	}

	@Override
	public Response xml(String xml) {
		try {
			response.setHeader("Cache-Control", "no-cache");
			response.setContentType("text/xml;charset=utf-8");
			PrintWriter writer = response.getWriter();
			DispatchKit.print(xml, writer);
    		this.written = true;
			return this;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public ServletOutputStream outputStream() throws IOException {
		return response.getOutputStream();
	}
	
	@Override
	public PrintWriter writer() throws IOException {
		return response.getWriter();
	}

	@Override
	public Response render(String view){
		Assert.notBlank(view, "view not is null");
		
		String viewPath = Path.cleanPath(view);
		ModelAndView modelAndView = new ModelAndView(viewPath);
		try {
			templateEngine.render(modelAndView, response.getWriter());
		} catch (TemplateException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return this;
	}
	
	@Override
	public Response render(ModelAndView modelAndView) {
		Assert.notNull(modelAndView, "ModelAndView not is null!");
		Assert.notBlank(modelAndView.getView(), "view not is null");
		
		String viewPath = Path.cleanPath(modelAndView.getView());
		modelAndView.setView(viewPath);
		try {
			templateEngine.render(modelAndView, response.getWriter());
		} catch (TemplateException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return this;
	}

	@Override
	public void redirect(String path) {
		try {
			response.sendRedirect(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void go(String path) {
		try {
			String ctx = ApplicationWebContext.servletContext().getContextPath();
        	String location = Path.fixPath(ctx + path);
			response.sendRedirect(location);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isWritten() {
		return written;
	}

}
