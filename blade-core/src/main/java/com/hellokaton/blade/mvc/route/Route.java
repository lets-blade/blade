package com.hellokaton.blade.mvc.route;

import com.hellokaton.blade.kit.PathKit;
import com.hellokaton.blade.mvc.http.HttpMethod;
import com.hellokaton.blade.mvc.ui.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

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
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 1.5
 */
@Builder
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

    private ResponseType responseType;

    private boolean isWildcard;

    private int sort;

    /**
     * Url path params
     */
    @Builder.Default
    private Map<String, String> pathParams = new HashMap<>(2);

    public Route() {
        this.sort = Integer.MAX_VALUE;
    }

    public Route(HttpMethod httpMethod, String path, Class<?> targetType, Method action) {
        super();
        this.httpMethod = httpMethod;
        this.path = PathKit.fixPath(path);
        this.targetType = targetType;
        this.action = action;
        this.sort = Integer.MAX_VALUE;
    }

    public Route(HttpMethod httpMethod, String path, Object target,
                 Class<?> targetType, Method action, ResponseType responseType) {
        super();
        this.httpMethod = httpMethod;
        this.path = PathKit.fixPath(path);
        this.target = target;
        this.targetType = targetType;
        this.action = action;
        this.responseType = responseType;
        sort = Integer.MAX_VALUE;
    }

    public Route(Route route) {
        this.httpMethod = route.httpMethod;
        this.path = route.path;
        this.target = route.target;
        this.targetType = route.targetType;
        this.action = route.action;
        this.responseType = route.responseType;
        this.sort = route.sort;
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

    public void setWildcard(boolean wildcard) {
        this.isWildcard = wildcard;
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
        return this.pathParams;
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

    public ResponseType getResponseType() {
        return this.responseType;
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