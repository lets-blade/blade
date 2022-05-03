package com.hellokaton.blade.mvc.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

public interface BodyWriter {

    FullHttpResponse onView(ViewBody body);

    FullHttpResponse onRawBody(RawBody body);

    FullHttpResponse onByteBuf(Object byteBuf);

    FullHttpResponse onByteBuf(ByteBuf byteBuf);

}