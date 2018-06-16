package com.blade.mvc.http;

import java.io.InputStream;

public class StreamBody implements Body {

    private final InputStream content;
    private final String      fileName;

    public StreamBody(String fileName, InputStream content) {
        this.fileName = fileName;
        this.content = content;
    }

    public StreamBody(final InputStream content) {
        this.fileName = null;
        this.content = content;
    }

    public InputStream content() {
        return content;
    }

    public String fileName() {
        return this.fileName;
    }

    @Override
    public <T> T write(BodyWriter<T> writer) {
        return writer.onStream(this);
    }
}