package com.hellokaton.blade.mvc.http;

import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StaticFileBody implements Body {

    private final String path;

    public StaticFileBody(String path) {
        this.path = path;
    }

    public static StaticFileBody of(String path) {
        return new StaticFileBody(path);
    }

    public String path() {
        return path;
    }

    @Override
    public HttpResponse write(BodyWriter writer) {
        return writer.onStatic(this);
    }

}