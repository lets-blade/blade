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

    public String method() {
        return this.request.method();
    }

    public String uri() {
        return this.request.uri();
    }

    public Session session() {
        return this.request.session();
    }

    public boolean isIE() {
        return this.request.isIE();
    }

    public String header(String headerName) {
        return this.request.header(headerName);
    }

    public RouteContext attribute(String key, Object value) {
        this.request.attribute(key, value);
        return this;
    }

    public String fromString(String paramName) {
        return this.request.query(paramName).orElse(null);
    }

    public String fromString(String paramName, String defaultValue) {
        return this.request.query(paramName, defaultValue);
    }

    public Integer fromInt(String paramName) {
        return this.request.queryInt(paramName).orElse(null);
    }

    public Integer fromInt(String paramName, Integer defaultValue) {
        return this.request.queryInt(paramName, defaultValue);
    }

    public Long fromLong(String paramName) {
        return this.request.queryLong(paramName).orElse(null);
    }

    public Long fromLong(String paramName, Long defaultValue) {
        return this.request.queryLong(paramName, defaultValue);
    }

    public String pathString(String paramName) {
        return this.request.pathString(paramName);
    }

    public Integer pathInt(String paramName) {
        return this.request.pathInt(paramName);
    }

    public Long pathLong(String paramName) {
        return this.request.pathLong(paramName);
    }

    public String userAgent() {
        return this.request.userAgent();
    }

    public String address() {
        return this.request.address();
    }

    public String remoteAddress() {
        return this.request.remoteAddress();
    }

    public String cookie(String name) {
        return this.request.cookie(name);
    }

    public RouteContext contentType(String contentType) {
        this.response.contentType(contentType);
        return this;
    }

    public RouteContext status(int statusCode) {
        this.response.status(statusCode);
        return this;
    }

    public RouteContext header(String name, String value) {
        this.response.header(name, value);
        return this;
    }

    public RouteContext badRequest() {
        this.response.badRequest();
        return this;
    }

    public RouteContext render(String view) {
        this.response.render(view);
        return this;
    }

    public RouteContext render(ModelAndView modelAndView) {
        this.response.render(modelAndView);
        return this;
    }

    public RouteContext text(String text) {
        this.response.text(text);
        return this;
    }

    public RouteContext json(String json) {
        this.response.json(json);
        return this;
    }

    public RouteContext json(Object object) {
        this.response.json(object);
        return this;
    }

    public RouteContext html(String html) {
        this.response.html(html);
        return this;
    }

    public Map<String, String> headers() {
        return this.request.headers();
    }

    public Map<String, List<String>> parameters() {
        return this.request.parameters();
    }

    public String contentType() {
        return this.request.contentType();
    }

    public String bodyToString() {
        return this.request.bodyToString();
    }

    public Body body() {
        return this.body();
    }

    public RouteContext body(Body body) {
        this.response.body(body);
        return this;
    }

    public RouteContext cookie(String name, String value) {
        this.response.cookie(name, value);
        return this;
    }

    public RouteContext cookie(String name, String value, int maxAge) {
        this.response.cookie(name, value, maxAge);
        return this;
    }

    public void redirect(String url) {
        this.response.redirect(url);
    }

    public void initRoute(Route route) {
        this.request.initPathParams(route);
        this.route = route;
        Method action = route.getAction();
        if (null != action && !action.getDeclaringClass().getName().contains(LAMBDA_IDENTIFY)) {
            this.routeActionParameters = getRouteActionParameters(this);
        }
    }

    public boolean next() {
        return true;
    }

    public Response response() {
        return this.response;
    }

    public Request request() {
        return this.request;
    }

    public Route route() {
        return this.route;
    }

    public Class<?> targetType() {
        return this.route.getTargetType();
    }

    public Object routeTarget() {
        return this.route.getTarget();
    }

    public Method routeAction() {
        return this.route.getAction();
    }

    public Object[] routeParameters() {
        return this.routeActionParameters;
    }

}
