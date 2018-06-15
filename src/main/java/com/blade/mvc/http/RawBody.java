package com.blade.mvc.http;

import io.netty.handler.codec.http.FullHttpResponse;

public class RawBody implements Body {

    private final FullHttpResponse httpResponse;

    public RawBody(FullHttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public FullHttpResponse httpResponse() {
        return httpResponse;
    }

    @Override
    public <T> T write(BodyWriter<T> writer) {
        return writer.onRawBody(this);
    }

}