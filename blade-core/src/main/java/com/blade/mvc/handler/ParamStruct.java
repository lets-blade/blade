package com.blade.mvc.handler;

import com.blade.annotation.request.*;
import com.blade.mvc.http.Request;
import lombok.Builder;

import java.lang.reflect.Type;

@Builder
class ParamStruct {

    Query query;
    PathParam pathParam;
    Body body;
    Header header;
    Cookie cookie;
    Multipart multipart;
    Type argType;
    String paramName;
    Request request;
}