package com.blade.mvc.http;

import io.netty.buffer.ByteBuf;

import java.io.Closeable;

public interface BodyWriter {

    void onStream(Closeable closeable);

    void onView(ViewBody body);

    void onRawBody(RawBody body);

    void onByteBuf(Object byteBuf);

    void onByteBuf(ByteBuf byteBuf);

}