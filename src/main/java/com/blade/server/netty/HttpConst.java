package com.blade.server.netty;

import com.blade.mvc.Const;
import io.netty.util.AsciiString;

/**
 * Http headers const
 *
 * @author biezhi
 * @date 2017/10/16
 */
public interface HttpConst {

    String VERSION = "blade-" + Const.VERSION;

    String IF_MODIFIED_SINCE   = "If-Modified-Since";
    String USER_AGENT          = "User-Agent";
    String CONTENT_TYPE_STRING = "Content-Type";
    String ACCEPT_ENCODING     = "Accept-Encoding";
    String COOKIE_STRING       = "Cookie";
    String METHOD_GET          = "GET";
    String METHOD_POST         = "POST";
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

    String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";

}
