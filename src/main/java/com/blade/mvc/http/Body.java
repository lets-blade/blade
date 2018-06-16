package com.blade.mvc.http;

public interface Body {

    <T> T write(BodyWriter<T> writer);

}