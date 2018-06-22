package com.blade.mvc;

import com.blade.mvc.http.*;
import com.blade.mvc.ui.ModelAndView;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Integration of Request and Response operations
 *
 * @author biezhi
 * @date 2018/6/21
 * @see Request
 * @see Response
 * @since 2.0.9.ALPHA1
 */
@Data
public class RouteContext {

    private Request  request;
    private Response response;

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

    public boolean next() {
        return true;
    }

    public Response response() {
        return this.response;
    }

    public Request request() {
        return this.request;
    }

}
