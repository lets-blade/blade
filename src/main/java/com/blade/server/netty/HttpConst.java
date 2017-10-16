package com.blade.server.netty;

import io.netty.util.AsciiString;

/**
 * Http headers const
 *
 * @author biezhi
 * @date 2017/10/16
 */
public interface HttpConst {
    String      IF_MODIFIED_SINCE = "IF_MODIFIED_SINCE";
    AsciiString CONNECTION        = AsciiString.cached("Connection");
    AsciiString CONTENT_LENGTH    = AsciiString.cached("Content-Length");
    AsciiString CONTENT_TYPE      = AsciiString.cached("Content-Type");
    AsciiString DATE              = AsciiString.cached("Date");
    AsciiString LOCATION          = AsciiString.cached("Location");
    AsciiString X_POWER_BY        = AsciiString.cached("X-Powered-By");
    AsciiString EXPIRES           = AsciiString.cached("Expires");
    AsciiString CACHE_CONTROL     = AsciiString.cached("Cache-Control");
    AsciiString LAST_MODIFIED     = AsciiString.cached("Last-Modified");
    AsciiString SERVER            = AsciiString.cached("Server");
    AsciiString SET_COOKIE        = AsciiString.cached("Set-Cookie");
    AsciiString KEEP_ALIVE        = AsciiString.cached("Keep-Alive");
}
