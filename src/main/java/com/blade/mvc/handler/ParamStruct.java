package com.blade.mvc.handler;

import com.blade.mvc.annotation.*;
import com.blade.mvc.http.Request;
import lombok.Builder;

import java.lang.reflect.Type;

@Builder
class ParamStruct {

    Param          param;
    PathParam      pathParam;
    BodyParam      bodyParam;
    HeaderParam    headerParam;
    CookieParam    cookieParam;
    MultipartParam multipartParam;
    Type           argType;
    String         paramName;
    Request        request;
}