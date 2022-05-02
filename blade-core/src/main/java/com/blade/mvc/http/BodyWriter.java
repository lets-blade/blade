package com.blade.mvc.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

import java.io.Closeable;

public interface BodyWriter {

    FullHttpResponse onStream(Closeable closeable);

    FullHttpResponse onView(ViewBody body);

    FullHttpResponse onRawBody(RawBody body);

    FullHttpResponse onByteBuf(Object byteBuf);

    FullHttpResponse onByteBuf(ByteBuf byteBuf);

}