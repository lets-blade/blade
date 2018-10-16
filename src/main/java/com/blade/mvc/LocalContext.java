package com.blade.mvc;

import com.blade.mvc.http.HttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

/**
 * Cache the request context of the current thread, use it for RequestHandler.
 *
 * @author biezhi
 * @date 2018/10/15
 */
public class LocalContext {

    private HttpObject             msg;
    private HttpRequest            request;
    private HttpPostRequestDecoder decoder;

    public LocalContext(HttpObject msg, HttpRequest request, HttpPostRequestDecoder decoder) {
        this.msg = msg;
        this.request = request;
        this.decoder = decoder;
    }

    public HttpObject msg() {
        return this.msg;
    }

    public HttpRequest request() {
        return this.request;
    }

    public HttpPostRequestDecoder decoder() {
        return decoder;
    }

    public boolean hasDecoder() {
        return null != decoder;
    }

    public void updateMsg(HttpObject msg) {
        this.msg = msg;
    }

}
