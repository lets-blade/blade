package com.blade.mvc.hook;

import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;

/**
 * @author biezhi
 *         2017/6/2
 */
public class Invoker {

    private Request request;
    private Response response;

    public Invoker(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

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
