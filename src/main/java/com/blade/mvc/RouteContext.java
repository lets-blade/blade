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

import com.blade.mvc.http.Body;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.http.Session;
import com.blade.mvc.route.Route;
import com.blade.mvc.ui.ModelAndView;
import lombok.var;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.blade.mvc.handler.RouteActionArguments.getRouteActionParameters;

/**
 * Integration of Request and Response operations
 *
 * @author biezhi
 * @date 2018/6/21
 * @see Request
 * @see Response
 * @since 2.0.9.ALPHA1
 */
public class RouteContext {

    private Route    route;
    private Request  request;
    private Response response;
    private Object[] routeActionParameters;

    private static final String LAMBDA_IDENTIFY = "$$Lambda$";

    public RouteContext() {
    }

    public RouteContext(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    /**
     * Get current request http method. e.g: GET
     *
     * @return Return request method
     */
    public String method() {
        return this.request.method();
    }

    /**
     * Get request uri
     *
     * @return Return request uri
     */
    public String uri() {
        return this.request.uri();
    }

    /**
     * Get current request is KeepAlive, HTTP1.1 is true.
     *
     * @return return current request connection keepAlive
     */
    public boolean keepAlive() {
        return request.keepAlive();
    }

    /**
     * Get current request session, if null then create
     *
     * @return Return current session
     */
    public Session session() {
        return this.request.session();
    }

    /**
     * Gets the current request is the head of the IE browser
     *
     * @return return current request is IE browser
     */
    public boolean isIE() {
        return this.request.isIE();
    }

    /**
     * Get header information
     *
     * @param headerName Parameter name
     * @return Return header information
     */
    public String header(String headerName) {
        return this.request.header(headerName);
    }

    /**
     * Setting Request Attribute
     *
     * @param key   attribute name
     * @param value attribute Value
     * @return set attribute value and return current request instance
     */
    public RouteContext attribute(String key, Object value) {
        this.request.attribute(key, value);
        return this;
    }

    /**
     * Get a request parameter
     *
     * @param paramName Parameter name
     * @return Return request parameter value
     */
    public String fromString(String paramName) {
        return this.request.query(paramName).orElse(null);
    }

    /**
     * Get a request parameter, if NULL is returned to defaultValue
     *
     * @param paramName    parameter name
     * @param defaultValue default String value
     * @return Return request parameter values
     */
    public String fromString(String paramName, String defaultValue) {
        return this.request.query(paramName, defaultValue);
    }

    /**
     * Returns a request parameter for a Int type
     *
     * @param paramName Parameter name
     * @return Return Int parameter values
     */
    public Integer fromInt(String paramName) {
        return this.request.queryInt(paramName).orElse(null);
    }

    /**
     * Returns a request parameter for a Int type
     *
     * @param paramName    Parameter name
     * @param defaultValue default int value
     * @return Return Int parameter values
     */
    public Integer fromInt(String paramName, Integer defaultValue) {
        return this.request.queryInt(paramName, defaultValue);
    }

    /**
     * Returns a request parameter for a Long type
     *
     * @param paramName Parameter name
     * @return Return Long parameter values
     */
    public Long fromLong(String paramName) {
        return this.request.queryLong(paramName).orElse(null);
    }

    /**
     * Returns a request parameter for a Long type
     *
     * @param paramName    Parameter name
     * @param defaultValue default long value
     * @return Return Long parameter values
     */
    public Long fromLong(String paramName, Long defaultValue) {
        return this.request.queryLong(paramName, defaultValue);
    }

    /**
     * Get a URL parameter
     *
     * @param paramName Parameter name
     * @return Return parameter value
     */
    public String pathString(String paramName) {
        return this.request.pathString(paramName);
    }

    /**
     * Return a URL parameter for a Int type
     *
     * @param paramName Parameter name
     * @return Return Int parameter value
     */
    public Integer pathInt(String paramName) {
        return this.request.pathInt(paramName);
    }

    /**
     * Return a URL parameter for a Long type
     *
     * @param paramName Parameter name
     * @return Return Long parameter value
     */
    public Long pathLong(String paramName) {
        return this.request.pathLong(paramName);
    }

    /**
     * Get request user-agent
     *
     * @return return user-agent
     */
    public String userAgent() {
        return this.request.userAgent();
    }

    /**
     * Get client ip address
     *
     * @return Return server remote address
     */
    public String address() {
        return this.request.address();
    }

    /**
     * Get client remote address. e.g: 102.331.234.11:38227
     *
     * @return Return client ip and port
     */
    public String remoteAddress() {
        return this.request.remoteAddress();
    }

    /**
     * Get String Cookie Value
     *
     * @param name cookie name
     * @return Return Cookie Value
     */
    public String cookie(String name) {
        return this.request.cookie(name);
    }

    /**
     * Get current request headers.
     *
     * @return Return header information Map
     */
    public Map<String, String> headers() {
        return this.request.headers();
    }

    /**
     * Get current request query parameters
     *
     * @return Return request query Map
     */
    public Map<String, List<String>> parameters() {
        return this.request.parameters();
    }

    /**
     * Get current request contentType. e.g: "text/html; charset=utf-8"
     *
     * @return Return contentType
     */
    public String contentType() {
        return this.request.contentType();
    }

    /**
     * Get current request body as string
     *
     * @return return request body to string
     */
    public String bodyToString() {
        return this.request.bodyToString();
    }

    /**
     * Get current response body
     *
     * @return {@link Body}
     */
    public Body body() {
        return response.body();
    }

    /**
     * Setting Response ContentType
     *
     * @param contentType content type
     * @return RouteContext
     */
    public RouteContext contentType(String contentType) {
        this.response.contentType(contentType);
        return this;
    }

    /**
     * Setting Response Status
     *
     * @param statusCode status code
     * @return RouteContext
     */
    public RouteContext status(int statusCode) {
        this.response.status(statusCode);
        return this;
    }

    /**
     * Set current response header
     *
     * @param name  Header Name
     * @param value Header Value
     * @return RouteContext
     */
    public RouteContext header(String name, String value) {
        this.response.header(name, value);
        return this;
    }

    /**
     * Set current response http code 400
     *
     * @return RouteContext
     */
    public RouteContext badRequest() {
        this.response.badRequest();
        return this;
    }

    /**
     * Render view, can be modified after WebHook
     *
     * @param view view page
     * @return RouteContext
     */
    public RouteContext render(String view) {
        this.response.render(view);
        return this;
    }

    /**
     * Render view And Setting Data, can be modified after WebHook
     *
     * @param modelAndView ModelAndView object
     * @return RouteContext
     */
    public RouteContext render(ModelAndView modelAndView) {
        this.response.render(modelAndView);
        return this;
    }

    /**
     * Render by text
     *
     * @param text text content
     * @return RouteContext
     */
    public RouteContext text(String text) {
        this.response.text(text);
        return this;
    }

    /**
     * Render by json
     *
     * @param json json content
     * @return RouteContext
     */
    public RouteContext json(String json) {
        this.response.json(json);
        return this;
    }

    /**
     * Render by json
     *
     * @param bean bean instance
     * @return RouteContext
     */
    public RouteContext json(Object bean) {
        this.response.json(bean);
        return this;
    }

    /**
     * Render by html
     *
     * @param html html content
     * @return RouteContext
     */
    public RouteContext html(String html) {
        this.response.html(html);
        return this;
    }

    /**
     * Send body to client
     *
     * @param body {@link Body}
     * @return RouteContext
     */
    public RouteContext body(Body body) {
        this.response.body(body);
        return this;
    }

    /**
     * Add Cookie
     *
     * @param name  Cookie Name
     * @param value Cookie Value
     * @return Return Response
     */
    public RouteContext cookie(String name, String value) {
        this.response.cookie(name, value);
        return this;
    }

    /**
     * Setting Cookie
     *
     * @param name   Cookie Name
     * @param value  Cookie Value
     * @param maxAge Period of validity
     * @return Return Response
     */
    public RouteContext cookie(String name, String value, int maxAge) {
        this.response.cookie(name, value, maxAge);
        return this;
    }

    /**
     * Redirect to newUri
     *
     * @param newUri new url
     */
    public void redirect(String newUri) {
        this.response.redirect(newUri);
    }

    public boolean next() {
        return true;
    }

    /**
     * Get response instance
     *
     * @return {@link Response}
     */
    public Response response() {
        return this.response;
    }

    /**
     * Get request instance
     *
     * @return {@link Request}
     */
    public Request request() {
        return this.request;
    }

    /**
     * Get current request route instance
     *
     * @return {@link Route}
     */
    public Route route() {
        return this.route;
    }

    /**
     * Get current request route target type
     *
     * @return controller class type
     */
    public Class<?> targetType() {
        return this.route.getTargetType();
    }

    /**
     * Get current request route target instance
     *
     * @return route target instance
     */
    public Object routeTarget() {
        return this.route.getTarget();
    }

    /**
     * Get current request route method
     *
     * @return route logic method
     */
    public Method routeAction() {
        return this.route.getAction();
    }

    /**
     * Get current route method parameters
     *
     * @return route method parameters
     */
    public Object[] routeParameters() {
        return this.routeActionParameters;
    }

    public void initRoute(Route route) {
        this.request.initPathParams(route);
        this.route = route;
        var action = route.getAction();
        if (null != action && !action.getDeclaringClass().getName().contains(LAMBDA_IDENTIFY)) {
            this.routeActionParameters = getRouteActionParameters(this);
        }
    }

}