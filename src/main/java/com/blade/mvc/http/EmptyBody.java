package com.blade.mvc.http;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmptyBody implements Body {

    private static final EmptyBody INSTANCE = new EmptyBody();

    public static EmptyBody empty() {
        return INSTANCE;
    }

    @Override
    public <T> T write(BodyWriter<T> writer) {
        return writer.onEmpty(INSTANCE);
    }

}