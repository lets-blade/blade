package com.hellokaton.blade.mvc.http;

import io.netty.handler.codec.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

public class ChannelBody implements Body {

    private final String fileName;
    private final FileChannel content;

    public ChannelBody(final String fileName, final FileChannel content) {
        this.fileName = fileName;
        this.content = content;
    }

    public static ChannelBody of(File file) throws IOException {
        FileChannel fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
        return new ChannelBody(file.getName(), fileChannel);
    }

    @Override
    public HttpResponse write(BodyWriter writer) {
        return writer.onByteBuf(fileName, content);
    }

}