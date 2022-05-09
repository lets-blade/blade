package com.hellokaton.blade.mvc.ui;

import com.hellokaton.blade.mvc.HttpConst;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseType {
    EMPTY(""),
    JSON(HttpConst.CONTENT_TYPE_JSON),
    XML(HttpConst.CONTENT_TYPE_XML),
    TEXT(HttpConst.CONTENT_TYPE_TEXT),
    HTML(HttpConst.CONTENT_TYPE_HTML),
    VIEW(HttpConst.CONTENT_TYPE_HTML),
    STREAM(HttpConst.CONTENT_TYPE_STREAM),
    PREVIEW(""),
    ;

    private final String contentType;

    public String contentType() {
        return this.contentType;
    }

}
