package com.hellokaton.blade.mvc;

public interface HttpConst {

    String HEADER_SERVER_VALUE = "blade-" + BladeConst.VERSION;

    String HEADER_COOKIE = "Cookie";
    String HEADER_CONTENT_TYPE = "Content-Type";
    String HEADER_HOST = "Host";

    String HEADER_LOCATION = "Location";

    String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

    String HEADER_IF_MODIFIED_SINCE ="If-Modified-Since";

    String HEADER_USER_AGENT = "User-Agent";

    String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
    String CONTENT_TYPE_XML = "text/xml; charset=UTF-8";
    String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
    String CONTENT_TYPE_TEXT = "text/plain; charset=UTF-8";
    String CONTENT_TYPE_STREAM = "application/octet-stream";

}
