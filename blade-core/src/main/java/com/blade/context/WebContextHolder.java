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
package com.blade.context;

import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.http.wrapper.Session;

import javax.servlet.ServletContext;

/**
 * BladeWebContext
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.6.6
 */
public class WebContextHolder {

    /**
     * BladeWebContext object for the current thread
     */
    private static final ThreadLocal<WebContextHolder> ctx = new ThreadLocal<>();

    /**
     * Request
     */
    private Request request;

    /**
     * Response
     */
    private Response response;

    private WebContextHolder() {
    }

    public static WebContextHolder me() {
        return ctx.get();
    }

    public static void init(Request request, Response response) {
        WebContextHolder bladeWebContext = new WebContextHolder();
        bladeWebContext.request = request;
        bladeWebContext.response = response;
        ctx.set(bladeWebContext);
    }

    /**
     * 移除当前线程的Request、Response对象
     */
    public static void remove() {
        ctx.remove();
    }

    public static Request request() {
        return me().request;
    }

    public static Response response() {
        return me().response;
    }

    public static Session session() {
        return request().session();
    }

    public static ServletContext servletContext() {
        return request().raw().getServletContext();
    }

    public static void destroy() {
        ctx.remove();
    }

}