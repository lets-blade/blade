package com.hellokaton.blade.mvc.http;

import io.netty.handler.codec.http.FullHttpResponse;

public interface Body {

    FullHttpResponse write(BodyWriter writer);

}