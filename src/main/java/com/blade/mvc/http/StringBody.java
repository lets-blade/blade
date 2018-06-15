package com.blade.mvc.http;

public class StringBody implements Body {

    private final String content;

    public StringBody(final String content) {
        this.content = content;
    }

    public String content() {
        return content;
    }

    @Override
    public <T> T write(BodyWriter<T> writer) {
        return writer.onText(this);
    }

}