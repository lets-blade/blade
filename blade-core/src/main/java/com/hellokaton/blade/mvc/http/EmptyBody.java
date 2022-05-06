package com.hellokaton.blade.mvc.http;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmptyBody implements Body {

    private static final EmptyBody INSTANCE = new EmptyBody();

    public static EmptyBody empty() {
        return INSTANCE;
    }

    @Override
    public HttpResponse write(BodyWriter writer) {
        return writer.onByteBuf( Unpooled.buffer(0));
    }

}