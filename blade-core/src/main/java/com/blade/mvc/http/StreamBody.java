package com.blade.mvc.http;

import io.netty.handler.codec.http.FullHttpResponse;

import java.io.InputStream;

public class StreamBody implements Body {

    private final InputStream content;

    public StreamBody(final InputStream content) {
        this.content = content;
    }

    public static StreamBody of(InputStream inputStream){
        return new StreamBody(inputStream);
    }

    @Override
    public FullHttpResponse write(BodyWriter writer) {
//        writer.onStream(content);
        return writer.onByteBuf(content);
    }
}