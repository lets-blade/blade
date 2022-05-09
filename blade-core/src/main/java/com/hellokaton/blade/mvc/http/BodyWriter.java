package com.hellokaton.blade.mvc.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponse;

import java.nio.channels.FileChannel;

public interface BodyWriter {

    HttpResponse onView(ViewBody body);

    HttpResponse onStatic(StaticFileBody body);

    HttpResponse onRawBody(RawBody body);

    HttpResponse onByteBuf(ByteBuf byteBuf);

    HttpResponse onByteBuf(String fileName, FileChannel channel);

}