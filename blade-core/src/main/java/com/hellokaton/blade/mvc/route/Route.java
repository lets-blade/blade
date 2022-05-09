package com.hellokaton.blade.mvc.route;

import com.hellokaton.blade.kit.PathKit;
import com.hellokaton.blade.mvc.http.HttpMethod;
import com.hellokaton.blade.mvc.ui.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * Route Bean
 * <p>
 * A route identifies the smallest unit of the request,
 * which encapsulates the path of the request,
 * the Http method, and the method of executing the route
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 2.1.2.RELEASE
 */
@Data
@Builder
@AllArgsConstructor
public class Route {

    /**
     * HTTP Request Method
     */
    private HttpMethod httpMethod;

    /**
     * Route path
     */
    private String path;

    private String rewritePath;

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
    private Map<String, String> pathParams = Collections.emptyMap();

    public Route() {
        this.sort = Integer.MAX_VALUE;
    }

    public Route(HttpMethod httpMethod, String path, Class<?> targetType, Method action) {
        this.httpMethod = httpMethod;
        this.path = PathKit.fixPath(path);
        if (this.path.contains("*")) {
            this.rewritePath = this.path.replaceAll("\\*", "");
        }
        this.targetType = targetType;
        this.action = action;
        this.sort = Integer.MAX_VALUE;
    }

    public Route(HttpMethod httpMethod, String path, Object target,
                 Class<?> targetType, Method action, ResponseType responseType) {
        super();
        this.httpMethod = httpMethod;
        this.path = PathKit.fixPath(path);
        if (this.path.contains("*")) {
            this.rewritePath = this.path.replaceAll("\\*", "");
        }
        this.target = target;
        this.targetType = targetType;
        this.action = action;
        this.responseType = responseType;
        sort = Integer.MAX_VALUE;
    }

    public Route(Route route) {
        this.httpMethod = route.httpMethod;
        this.path = route.path;
        if (this.path.contains("*")) {
            this.rewritePath = this.path.replaceAll("\\*", "");
        }
        this.target = route.target;
        this.targetType = route.targetType;
        this.action = route.action;
        this.isWildcard = route.isWildcard;
        this.responseType = route.responseType;
        this.sort = route.sort;
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