package com.blade.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author biezhi
 * @date 2018/6/1
 */
public class StaticInputStream {

    private InputStream inputStream;
    private int         size;
    private ByteBuf     byteBuf;

    public StaticInputStream(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byteBuf = Unpooled.buffer();

        // Fake code simulating the copy
        // You can generally do better with nio if you need...
        // And please, unlike me, do something about the Exceptions :D
        byte[] buffer = new byte[1024];
        int    len;
        while ((len = input.read(buffer)) > -1) {
            size += len;
            baos.write(buffer, 0, len);
        }
        baos.flush();

        // Open new InputStreams using the recorded bytes
        // Can be repeated as many times as you wish
        this.inputStream = new ByteArrayInputStream(baos.toByteArray());
        byteBuf.writeBytes(this.inputStream, size);
    }

    public int size() {
        return this.size;
    }

    public ByteBuf asByteBuf() {
        return this.byteBuf;
    }

}
