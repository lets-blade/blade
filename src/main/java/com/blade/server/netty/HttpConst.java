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
    String IF_MODIFIED_SINCE   = "If-Modified-Since";
    String USER_AGENT          = "User-Agent";
    String CONTENT_TYPE_STRING = "Content-Type";
    String COOKIE_STRING       = "Cookie";
    String METHOD_GET          = "GET";
    String METHOD_POST         = "POST";
    String IE_UA               = "MSIE";
    String DEFAULT_SESSION_KEY = "SESSION";
    String SLASH               = "/";
    char   CHAR_SLASH          = '/';
    char   CHAR_POINT          = '.';

    CharSequence CONNECTION     = AsciiString.cached("Connection");
    CharSequence CONTENT_LENGTH = AsciiString.cached("Content-Length");
    CharSequence CONTENT_TYPE   = AsciiString.cached("Content-Type");
    CharSequence DATE           = AsciiString.cached("Date");
    CharSequence LOCATION       = AsciiString.cached("Location");
    CharSequence X_POWER_BY     = AsciiString.cached("X-Powered-By");
    CharSequence EXPIRES        = AsciiString.cached("Expires");
    CharSequence CACHE_CONTROL  = AsciiString.cached("Cache-Control");
    CharSequence LAST_MODIFIED  = AsciiString.cached("Last-Modified");
    CharSequence SERVER         = AsciiString.cached("Server");
    CharSequence SET_COOKIE     = AsciiString.cached("Set-Cookie");
    CharSequence KEEP_ALIVE     = AsciiString.cached("keep-alive");

    String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";

    CharSequence VERSION = AsciiString.cached("blade-" + Const.VERSION);

}
