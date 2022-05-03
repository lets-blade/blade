package com.hellokaton.blade.server;

import com.hellokaton.blade.mvc.BladeConst;
import io.netty.util.AsciiString;

import java.util.HashMap;
import java.util.Map;

/**
 * Http headers const
 *
 * @author biezhi
 * @date 2017/10/16
 */
public interface NettyHttpConst {

    String VERSION = "blade-" + BladeConst.VERSION;

    String IF_MODIFIED_SINCE   = "If-Modified-Since";
    String USER_AGENT          = "User-Agent";
    String CONTENT_TYPE_STRING = "Content-Type";
    String ACCEPT_ENCODING     = "Accept-Encoding";
    String METHOD_GET          = "GET";
    String DEFAULT_SESSION_KEY = "SESSION";
    String SLASH               = "/";
    char   CHAR_SLASH          = '/';
    char   CHAR_POINT          = '.';

    AsciiString CONNECTION       = AsciiString.cached("Connection");
    AsciiString CONTENT_LENGTH   = AsciiString.cached("Content-Length");
    AsciiString CONTENT_TYPE     = AsciiString.cached("Content-Type");
    AsciiString CONTENT_ENCODING = AsciiString.cached("Content-Encoding");
    AsciiString DATE             = AsciiString.cached("Date");
    AsciiString LOCATION         = AsciiString.cached("Location");
    AsciiString EXPIRES          = AsciiString.cached("Expires");
    AsciiString CACHE_CONTROL    = AsciiString.cached("Cache-Control");
    AsciiString LAST_MODIFIED    = AsciiString.cached("Last-Modified");
    AsciiString SERVER           = AsciiString.cached("Server");
    AsciiString SET_COOKIE       = AsciiString.cached("Set-Cookie");
    AsciiString KEEP_ALIVE       = AsciiString.cached("keep-alive");
    AsciiString X_POWER_BY       = AsciiString.cached("X-Powered-By");
    AsciiString HEADER_VERSION   = AsciiString.cached(VERSION);

    Map<String, AsciiString> CACHE = new HashMap<>(16);

    static AsciiString getAsciiString(String name) {
        return CACHE.computeIfAbsent(name, AsciiString::cached);
    }

}
