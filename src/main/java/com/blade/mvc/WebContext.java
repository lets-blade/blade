/**
 * Copyright (c) 2017, biezhi 王爵 (biezhi.me@gmail.com)
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
package com.blade.mvc;

import com.blade.Blade;
import com.blade.Environment;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import com.blade.server.netty.HttpServerHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.NoArgsConstructor;
import lombok.var;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.blade.server.netty.HttpServerHandler.WEB_CONTEXT_THREAD_LOCAL;

/**
 * Blade Web Context
 * <p>
 * Route logic current thread context request and response instance.
 *
 * @author biezhi
 * 2017/6/1
 */
@NoArgsConstructor
public class WebContext {

    /**
     * Blade instance, when the project is initialized when it will permanently reside in memory
     */
    private static Blade blade;

    /**
     * ContextPath, default is "/"
     */
    private static String contextPath;

    /**
     * Http Request instance of current thread context
     */
    private Request request;

    /**
     * Http Response instance of current thread context
     */
    private Response response;

    private Route route;

    private ChannelHandlerContext channelHandlerContext;

    private CompletableFuture future;

    public WebContext(Request request, Response response, ChannelHandlerContext channelHandlerContext) {
        this.request = request;
        this.response = response;
        this.channelHandlerContext = channelHandlerContext;
    }

    /**
     * Get current thread context WebContext instance
     *
     * @return WebContext instance
     */
    public static WebContext get() {
        return WEB_CONTEXT_THREAD_LOCAL.get();
    }

    /**
     * Get current thread context Request instance
     *
     * @return Request instance
     */
    public static Request request() {
        var webContext = get();
        return null != webContext ? webContext.request : null;
    }

    /**
     * Get current thread context Response instance
     *
     * @return Response instance
     */
    public static Response response() {
        var webContext = get();
        return null != webContext ? webContext.response : null;
    }

    public static WebContext create(Request request, Response response, ChannelHandlerContext ctx, CompletableFuture future) {
        WebContext webContext = new WebContext();
        webContext.request = request;
        webContext.response = response;
        webContext.channelHandlerContext = ctx;
        webContext.future = future;
        WEB_CONTEXT_THREAD_LOCAL.set(webContext);
        return webContext;
    }

    public static void set(WebContext webContext) {
        HttpServerHandler.WEB_CONTEXT_THREAD_LOCAL.set(webContext);
    }

    public static void remove() {
        HttpServerHandler.WEB_CONTEXT_THREAD_LOCAL.remove();
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    /**
     * Initializes the project when it starts
     *
     * @param blade       Blade instance
     * @param contextPath context path
     */
    public static void init(Blade blade, String contextPath) {
        WebContext.blade = blade;
        WebContext.contextPath = contextPath;
    }


    /**
     * Get blade instance
     *
     * @return return Blade
     */
    public static Blade blade() {
        return blade;
    }

    /**
     * Get context path
     *
     * @return return context path string, e.g: /
     */
    public static String contextPath() {
        return contextPath;
    }

    public static void clean() {
        WEB_CONTEXT_THREAD_LOCAL.remove();
        blade = null;
    }

    public Environment environment() {
        return blade.environment();
    }

    /**
     * Get application environment information.
     *
     * @param key environment key
     * @return environment optional value
     */
    public Optional<String> env(String key) {
        return blade().env(key);
    }

    /**
     * Get application environment information.
     *
     * @param key          environment key
     * @param defaultValue default value, if value is null
     * @return environment optional value
     */
    public String env(String key, String defaultValue) {
        return blade().env(key, defaultValue);
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public ChannelHandlerContext getHandlerContext() {
        return channelHandlerContext;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public CompletableFuture getFuture() {
        return future;
    }

}
