package com.hellokaton.blade.mvc.handler;

import com.hellokaton.blade.annotation.request.*;
import com.hellokaton.blade.mvc.http.Request;
import lombok.Builder;

import java.lang.reflect.Type;

@Builder
class ParamStruct {

    Query query;
    Form form;
    PathParam pathParam;
    Body body;
    Header header;
    Cookie cookie;
    Multipart multipart;
    Type argType;
    String paramName;
    Request request;
}