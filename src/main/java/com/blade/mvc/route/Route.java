package com.blade.mvc.route;

import com.blade.kit.PathKit;
import com.blade.mvc.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Route Bean
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class Route {

    /**
     * HTTP Request Method
     */
    private HttpMethod httpMethod;

    /**
     * Route path
     */
    private String path;

    /**
     * Logical controller object
     */
    private Object target;

    /**
     * PathKit Class EventType
     */
    private Class<?> targetType;

    /**
     * Implementation logic controller method
     */
    private Method action;

    private int sort = Integer.MAX_VALUE;

    /**
     * Url path params
     */
    private Map<String, String> pathParams = new HashMap<>();

    public Route() {
    }

    public Route(HttpMethod httpMethod, String path, Class<?> targetType, Method action) {
        super();
        this.httpMethod = httpMethod;
        this.path = PathKit.fixPath(path);
        this.targetType = targetType;
        this.action = action;
    }

    public Route(HttpMethod httpMethod, String path, Object target, Class<?> targetType, Method action) {
        super();
        this.httpMethod = httpMethod;
        this.path = PathKit.fixPath(path);
        this.target = target;
        this.targetType = targetType;
        this.action = action;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getAction() {
        return action;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public void setPathParams(Map<String, String> pathParams) {
        this.pathParams = pathParams;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        if (sort != route.sort) return false;
        if (httpMethod != route.httpMethod) return false;
        if (path != null ? !path.equals(route.path) : route.path != null) return false;
        if (target != null ? !target.equals(route.target) : route.target != null) return false;
        if (targetType != null ? !targetType.equals(route.targetType) : route.targetType != null) return false;
        if (action != null ? !action.equals(route.action) : route.action != null) return false;
        if (pathParams != null ? !pathParams.equals(route.pathParams) : route.pathParams != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = httpMethod != null ? httpMethod.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (targetType != null ? targetType.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + sort;
        result = 31 * result + (pathParams != null ? pathParams.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return httpMethod + "\t" + path;
    }

}