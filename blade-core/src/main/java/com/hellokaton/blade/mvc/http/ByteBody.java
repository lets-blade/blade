package com.hellokaton.blade.mvc.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author biezhi
 * @date 2018/9/21
 */
public class ByteBody implements Body {

    private final ByteBuf byteBuf;

    public ByteBody(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public ByteBody(File file) {
        try {
            this.byteBuf = Unpooled.copiedBuffer(Files.readAllBytes(Paths.get(file.toURI())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static ByteBody of(ByteBuf byteBuf) {
        return new ByteBody(byteBuf);
    }

    public static ByteBody of(byte[] bytes) {
        return new ByteBody(Unpooled.copiedBuffer(bytes));
    }

    @Override
    public HttpResponse write(BodyWriter writer) {
        return writer.onByteBuf(byteBuf);
    }

}
