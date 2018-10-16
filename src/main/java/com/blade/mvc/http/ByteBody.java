package com.blade.mvc.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author biezhi
 * @date 2018/9/21
 */
public class ByteBody implements Body {

    private ByteBuf      byteBuf = null;
    private File         file;
    private OutputStream outputStream;

    private ByteBody() {

    }

    public ByteBody(File file) {
        try {
            this.file = file;
            this.byteBuf = Unpooled.copiedBuffer(Files.readAllBytes(Paths.get(file.toURI())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ByteBody(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public static ByteBody of(File file) {
        return new ByteBody(file);
    }

    public static ByteBody of(OutputStream outputStream) {
        return new ByteBody(outputStream);
    }

    public static ByteBody of(byte[] bytes) {
        ByteBody byteBody = new ByteBody();
        byteBody.byteBuf = Unpooled.copiedBuffer(bytes);
        return byteBody;
    }

    public static ByteBody of(ByteBuf byteBuf) {
        ByteBody byteBody = new ByteBody();
        byteBody.byteBuf = byteBuf;
        return byteBody;
    }

    @Override
    public void write(BodyWriter writer) {
        if (null != outputStream) {
            writer.onByteBuf(outputStream);
            return;
        }
        writer.onByteBuf(byteBuf);
    }

}
