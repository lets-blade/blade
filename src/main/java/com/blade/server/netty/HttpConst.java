package com.blade.server.netty;

import com.blade.mvc.Const;
import com.blade.mvc.handler.RequestExecution;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.AsciiString;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.SERVICE_UNAVAILABLE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Http headers const
 *
 * @author biezhi
 * @date 2017/10/16
 */
public interface HttpConst {
    String IF_MODIFIED_SINCE   = "IF_MODIFIED_SINCE";
    String USER_AGENT          = "User-Agent";
    String CONTENT_TYPE_STRING = "Content-Type";
    String COOKIE_STRING       = "Cookie";
    String METHOD_GET          = "GET";
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
    CharSequence KEEP_ALIVE     = AsciiString.cached("Keep-Alive");

    String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";

    CharSequence VERSION = AsciiString.cached("blade-" + Const.VERSION);

    Map<CharSequence, CharSequence> contentTypes = new ConcurrentHashMap<>(8);

    static CharSequence getContentType(CharSequence contentType) {
        if (null == contentType) {
            contentType = CONTENT_TYPE_HTML;
        }
        if (contentTypes.containsKey(contentType)) {
            return contentTypes.get(contentType);
        }
        contentTypes.put(contentType, AsciiString.cached(String.valueOf(contentType)));
        return contentTypes.get(contentType);
    }

    /**
     * Minimum number of thread pool maintenance threads
     */
    int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * Maximum number of thread pool maintenance threads
     */
    int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * Thread pool maintenance threads allow free time
     */
    int KEEP_ALIVE_TIME = 0;

    /**
     * The size of the buffer queue used by the thread pool
     */
    int WORK_QUEUE_SIZE = Runtime.getRuntime().availableProcessors() * 4;

    ThreadPoolExecutor BUSINESS_THREAD_POOL = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(WORK_QUEUE_SIZE), (r, executor) -> {
        RequestExecution requestExecution = (RequestExecution) r;
        FullHttpResponse response         = new DefaultFullHttpResponse(HTTP_1_1, SERVICE_UNAVAILABLE, Unpooled.wrappedBuffer(("<center><h1>503</h1></center><br/>" + executor.toString()).getBytes()));
        response.headers().set(CONTENT_TYPE, CONTENT_TYPE_HTML);
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(CONNECTION, KEEP_ALIVE);
        requestExecution.getCtx().write(response);
    });

}
