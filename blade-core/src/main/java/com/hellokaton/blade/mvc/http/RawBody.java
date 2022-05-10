package com.hellokaton.blade.mvc.http;

import io.netty.handler.codec.http.FullHttpResponse;

public class RawBody implements Body {

    private final FullHttpResponse fullHttpResponse;

    public RawBody(FullHttpResponse fullHttpResponse) {
        this.fullHttpResponse = fullHttpResponse;
    }

    public FullHttpResponse fullHttpResponse() {
        return fullHttpResponse;
    }

    @Override
    public FullHttpResponse write(BodyWriter writer) {
        return writer.onRawBody(this);
    }

}