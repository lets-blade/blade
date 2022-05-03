package com.hellokaton.blade.mvc.http;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;

public class RawBody implements Body {

    private FullHttpResponse    httpResponse;
    private DefaultHttpResponse defaultHttpResponse;

    public RawBody(FullHttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public RawBody(DefaultHttpResponse defaultHttpResponse) {
        this.defaultHttpResponse = defaultHttpResponse;
    }

    public FullHttpResponse httpResponse() {
        return httpResponse;
    }

    public DefaultHttpResponse defaultHttpResponse() {
        return defaultHttpResponse;
    }

    @Override
    public FullHttpResponse write(BodyWriter writer) {
        return writer.onRawBody(this);
    }

}