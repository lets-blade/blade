package com.blade.mvc.hook;

import com.blade.mvc.RouteContext;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import lombok.*;

import java.lang.reflect.Method;

/**
 * Signature
 *
 * @author biezhi
 * 2017/6/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Deprecated
public class Signature {

    private static final String LAMBDA = "$$Lambda$";

    private Route    route;
    private Method   action;
    private Request  request;
    private Response response;
    private Object[] parameters;

    private RouteContext routeContext;

    public Request request() {
        return request;
    }

    public Response response() {
        return response;
    }

    public RouteContext routeContext() {
        return new RouteContext(request, response);
    }

    public boolean next() {
        return true;
    }

    public void setRoute(Route route) throws Exception {
        this.route = route;
        this.action = route.getAction();
        if (null != this.action &&
                !this.action.getDeclaringClass().getName().contains(LAMBDA)) {
//            this.parameters = MethodArgument.getArgs(this);
        }
    }

}
