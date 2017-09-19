package com.blade.mvc.route;

import com.blade.kit.PathKit;
import com.blade.mvc.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Route Bean
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
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
    public String toString() {
        return httpMethod + "\t" + path;
    }

}