package com.blade.mvc.hook;

import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import lombok.*;

import java.lang.reflect.Method;

/**
 * @author biezhi
 *         2017/6/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Invoker {

    private Route route;
    private Method action;
    private Request request;
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

}
