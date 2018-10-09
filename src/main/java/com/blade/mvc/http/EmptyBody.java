package com.blade.mvc.http;

import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmptyBody implements Body {

    private static final EmptyBody INSTANCE = new EmptyBody();

    public static EmptyBody empty() {
        return INSTANCE;
    }

    @Override
    public void write(BodyWriter writer) {
        writer.onByteBuf( Unpooled.buffer(0));
    }

}