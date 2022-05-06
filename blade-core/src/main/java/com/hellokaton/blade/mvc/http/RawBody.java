package com.hellokaton.blade.mvc.http;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;

public class RawBody implements Body {

    private HttpResponse    httpResponse;
    private DefaultHttpResponse defaultHttpResponse;

    public RawBody(FullHttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public RawBody(DefaultHttpResponse defaultHttpResponse) {
        this.defaultHttpResponse = defaultHttpResponse;
    }

    public HttpResponse httpResponse() {
        return httpResponse;
    }

    public DefaultHttpResponse defaultHttpResponse() {
        return defaultHttpResponse;
    }

    @Override
    public HttpResponse write(BodyWriter writer) {
        return writer.onRawBody(this);
    }

}