package com.hellokaton.blade.mvc.http;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;

public class RawBody implements Body {

    private HttpResponse    httpResponse;
    private FullHttpResponse fullHttpResponse;

    public RawBody(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public RawBody(FullHttpResponse fullHttpResponse) {
        this.fullHttpResponse = fullHttpResponse;
    }

    public HttpResponse httpResponse() {
        return httpResponse;
    }

    public FullHttpResponse defaultHttpResponse() {
        return fullHttpResponse;
    }

    @Override
    public HttpResponse write(BodyWriter writer) {
        return writer.onRawBody(this);
    }

}