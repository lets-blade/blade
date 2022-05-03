/**
 * Copyright (c) 2022, katon (hellokaton@gmail.com)
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
package com.hellokaton.blade.mvc;

import com.hellokaton.blade.Blade;
import com.hellokaton.blade.Environment;
import com.hellokaton.blade.mvc.http.Request;
import com.hellokaton.blade.mvc.http.Response;
import com.hellokaton.blade.mvc.route.Route;
import io.netty.channel.ChannelHandlerContext;
import lombok.NoArgsConstructor;
import lombok.var;

import java.util.Optional;

import static com.hellokaton.blade.mvc.BladeConst.ENV_KEY_SESSION_KEY;
import static com.hellokaton.blade.server.NettyHttpConst.DEFAULT_SESSION_KEY;
import static com.hellokaton.blade.server.HttpServerHandler.WEB_CONTEXT_THREAD_LOCAL;

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
     * Session Key, default is: SESSION
     */
    private static String sessionKey;

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

    public WebContext(Request request, Response response,
                      ChannelHandlerContext channelHandlerContext) {

        this.request = request;
        this.response = response;
        this.channelHandlerContext = channelHandlerContext;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public Environment environment() {
        return blade.environment();
    }

    public static String sessionKey() {
        return sessionKey;
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

    /**
     * Initializes the project when it starts
     *
     * @param blade       Blade instance
     * @param contextPath context path
     */
    public static void init(Blade blade, String contextPath) {
        WebContext.blade = blade;
        WebContext.contextPath = contextPath;
        WebContext.sessionKey = blade.environment().get(ENV_KEY_SESSION_KEY, DEFAULT_SESSION_KEY);
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

    public static WebContext create(Request request, Response response, ChannelHandlerContext ctx) {
        WebContext webContext = new WebContext();
        webContext.request = request;
        webContext.response = response;
        webContext.channelHandlerContext = ctx;
        WEB_CONTEXT_THREAD_LOCAL.set(webContext);
        return webContext;
    }

    public static void set(WebContext webContext) {
        WEB_CONTEXT_THREAD_LOCAL.set(webContext);
    }

    public static void remove() {
        WEB_CONTEXT_THREAD_LOCAL.remove();
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

}
