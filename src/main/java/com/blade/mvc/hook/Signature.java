package com.blade.mvc.hook;

import com.blade.mvc.handler.MethodArgument;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import lombok.*;

import java.lang.reflect.Method;

/**
 * Signature
 *
 * @author biezhi
 *         2017/6/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Signature {

    private Route    route;
    private Method   action;
    private Request  request;
    private Response response;
    private Object[] parameters;

    public Request request() {
        return request;
    }

    public Response response() {
        return response;
    }

    public boolean next() {
        return true;
    }

    public void setRoute(Route route) throws Exception {
        this.route = route;
        this.action = route.getAction();
        if (null != this.action && !this.action.toString().contains("$$Lambda$")) {
            this.initParameters();
        }
    }

    private void initParameters() throws Exception {
        if (null != this.action) {
            this.parameters = MethodArgument.getArgs(this);
        }
    }

}
