package com.hellokaton.blade.mvc.http;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;

import java.nio.charset.StandardCharsets;

public class StringBody implements Body {

    private final byte[] bytes;

    public StringBody(final String content) {
        this.bytes = content.getBytes(StandardCharsets.UTF_8);
    }

    public static StringBody of(String content) {
        return new StringBody(content);
    }

    @Override
    public FullHttpResponse write(BodyWriter writer) {
        return writer.onByteBuf(Unpooled.copiedBuffer(bytes));
    }

}