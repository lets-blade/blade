package com.hellokaton.blade.mvc.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

import java.nio.channels.FileChannel;

public interface BodyWriter {

    FullHttpResponse onView(ViewBody body);

    FullHttpResponse onStatic(StaticFileBody body);

    FullHttpResponse onRawBody(RawBody body);

    FullHttpResponse onByteBuf(ByteBuf byteBuf);

    FullHttpResponse onFileChannel(String fileName, FileChannel channel);

}