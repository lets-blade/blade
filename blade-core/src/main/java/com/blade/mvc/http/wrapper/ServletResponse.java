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
package com.blade.mvc.http.wrapper;

import com.blade.context.WebContextHolder;
import com.blade.kit.Assert;
import com.blade.kit.DispatchKit;
import com.blade.mvc.http.HttpStatus;
import com.blade.mvc.http.Path;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.view.ModelAndView;
import com.blade.mvc.view.ViewSettings;
import com.blade.mvc.view.resolve.JSONParser;
import com.blade.mvc.view.template.TemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * ServletResponse
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
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
        return cookie(name, value, -1);
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
        if (null != path) {
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
        if (null != cookie.getPath()) {
            servletCookie.setPath(cookie.getPath());
        }
        if (null != cookie.getDomain()) {
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
            this.header("Cache-Control", "no-cache");
            this.contentType("text/plain;charset=utf-8");
            DispatchKit.print(text, response.getWriter());
            this.written = true;
            return this;
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Response html(String html) {
        try {
            this.header("Cache-Control", "no-cache");
            this.contentType("text/html;charset=utf-8");

            DispatchKit.print(html, response.getWriter());
            this.written = true;
            return this;
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Response json(String json) {
        Request request = WebContextHolder.request();
        String userAgent = request.userAgent();
        if (userAgent.contains("MSIE")) {
            this.contentType("text/html;charset=utf-8");
        } else {
            this.contentType("application/json;charset=utf-8");
        }
        try {
            this.header("Cache-Control", "no-cache");
            DispatchKit.print(json, response.getWriter());
            this.written = true;
            return this;
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
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
            this.header("Cache-Control", "no-cache");
            this.contentType("text/xml;charset=utf-8");
            DispatchKit.print(xml, response.getWriter());
            this.written = true;
            return this;
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
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
    public Response render(String view) {
        String viewPath = Path.cleanPath(view);
        ModelAndView modelAndView = new ModelAndView(viewPath);
        try {
            templateEngine.render(modelAndView, response.getWriter());
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return this;
    }

    @Override
    public Response render(ModelAndView modelAndView) {
        Assert.notBlank(modelAndView.getView(), "view not is null");

        String viewPath = Path.cleanPath(modelAndView.getView());
        modelAndView.setView(viewPath);
        try {
            templateEngine.render(modelAndView, response.getWriter());
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return this;
    }

    @Override
    public void redirect(String path) {
        try {
            response.sendRedirect(path);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void go(String path) {
        try {
            String ctx = WebContextHolder.servletContext().getContextPath();
            String location = Path.fixPath(ctx + path);
            response.sendRedirect(location);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean isWritten() {
        return written;
    }

}
