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
 * <p>
 * A route identifies the smallest unit of the request,
 * which encapsulates the path of the request,
 * the Http method, and the method of executing the route
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

    @Builder.Default
    private int sort = Integer.MAX_VALUE;

    /**
     * Url path params
     */
    private Map<String, String> pathParams = new HashMap<>(8);

    public Route(HttpMethod httpMethod, String path, Class<?> targetType, Method action) {
        super();
        this.httpMethod = httpMethod;
        this.path = PathKit.fixPath(path);
        this.targetType = targetType;
        this.action = action;
        sort = Integer.MAX_VALUE;
    }

    public Route(HttpMethod httpMethod, String path, Object target, Class<?> targetType, Method action) {
        super();
        this.httpMethod = httpMethod;
        this.path = PathKit.fixPath(path);
        this.target = target;
        this.targetType = targetType;
        this.action = action;
        sort = Integer.MAX_VALUE;
    }

    /**
     * Return http method
     *
     * @return HttpMethod
     */
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    /**
     * Return route path
     *
     * @return path string
     */
    public String getPath() {
        return path;
    }

    /**
     * Set route path
     *
     * @param path string path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Return route controller instance
     *
     * @return route handler instance
     */
    public Object getTarget() {
        return target;
    }

    /**
     * Set route handler instance
     *
     * @param target target bean
     */
    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * Return route method
     *
     * @return route Method
     */
    public Method getAction() {
        return action;
    }

    /**
     * Get route handler type
     *
     * @return return target type
     */
    public Class<?> getTargetType() {
        return targetType;
    }

    /**
     * Get route path parameters
     *
     * @return return path params
     */
    public Map<String, String> getPathParams() {
        return pathParams;
    }

    /**
     * Set path params
     *
     * @param pathParams path params map
     */
    public void setPathParams(Map<String, String> pathParams) {
        this.pathParams = pathParams;
    }

    /**
     * Get route execution sort, default is Integer.MAX_VALUE
     *
     * @return return sort
     */
    public int getSort() {
        return sort;
    }

    /**
     * Set route execution sort
     *
     * @param sort sort number
     */
    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getAllPath() {
        return this.path + "#" + this.httpMethod.name();
    }

    /**
     * Route to string
     *
     * @return return route string
     */
    @Override
    public String toString() {
        return httpMethod + "\t" + path;
    }

}