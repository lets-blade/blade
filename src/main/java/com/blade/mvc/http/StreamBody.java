package com.blade.mvc.http;

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
    public void write(BodyWriter writer) {
//        writer.onStream(content);
        writer.onByteBuf(content);
    }
}