package com.hellokaton.blade.mvc.http;

import io.netty.handler.codec.http.HttpResponse;

public interface Body {

    HttpResponse write(BodyWriter writer);

}