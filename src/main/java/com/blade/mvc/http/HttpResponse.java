package com.blade.mvc.http;

import com.blade.kit.Assert;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.mvc.Const;
import com.blade.mvc.WebContext;
import com.blade.mvc.ui.ModelAndView;
import com.blade.mvc.ui.template.TemplateEngine;
import com.blade.server.netty.ProgressiveFutureListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.*;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * HttpResponse
 *
 * @author biezhi
 *         2017/5/31
 */
@Slf4j
public class HttpResponse implements Response {

    private String                contentType    = Const.CONTENT_TYPE_HTML;
    private HttpHeaders           headers        = new DefaultHttpHeaders();
    private Set<Cookie>           cookies        = new HashSet<>();
    private int                   statusCode     = 200;
    private boolean               isCommit       = false;
    private ChannelHandlerContext ctx            = null;
    private TemplateEngine        templateEngine = null;

    @Override
    public int statusCode() {
        return this.statusCode;
    }

    @Override
    public Response status(int status) {
        this.statusCode = status;
        return this;
    }

    @Override
    public Response contentType(@NonNull String contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public String contentType() {
        return this.contentType;
    }

    @Override
    public Map<String, String> headers() {
        Map<String, String> map = new HashMap<>(this.headers.size());
        this.headers.forEach(header -> map.put(header.getKey(), header.getValue()));
        return map;
    }

    @Override
    public Response header(@NonNull String name, @NonNull String value) {
        this.headers.set(name, value);
        return this;
    }

    @Override
    public Response cookie(@NonNull com.blade.mvc.http.Cookie cookie) {
        Cookie nettyCookie = new DefaultCookie(cookie.name(), cookie.value());
        if (cookie.domain() != null) {
            nettyCookie.setDomain(cookie.domain());
        }
        if (cookie.maxAge() > 0) {
            nettyCookie.setMaxAge(cookie.maxAge());
        }
        nettyCookie.setPath(cookie.path());
        nettyCookie.setHttpOnly(cookie.httpOnly());
        this.cookies.add(nettyCookie);
        return this;
    }

    @Override
    public Response cookie(String name, String value) {
        this.cookies.add(new io.netty.handler.codec.http.cookie.DefaultCookie(name, value));
        return this;
    }

    @Override
    public Response cookie(@NonNull String name, @NonNull String value, int maxAge) {
        Cookie nettyCookie = new DefaultCookie(name, value);
        nettyCookie.setPath("/");
        nettyCookie.setMaxAge(maxAge);
        this.cookies.add(nettyCookie);
        return this;
    }

    @Override
    public Response cookie(@NonNull String name, @NonNull String value, int maxAge, boolean secured) {
        Cookie nettyCookie = new DefaultCookie(name, value);
        nettyCookie.setPath("/");
        nettyCookie.setMaxAge(maxAge);
        nettyCookie.setSecure(secured);
        this.cookies.add(nettyCookie);
        return this;
    }

    @Override
    public Response cookie(@NonNull String path, @NonNull String name, @NonNull String value, int maxAge, boolean secured) {
        Cookie nettyCookie = new DefaultCookie(name, value);
        nettyCookie.setMaxAge(maxAge);
        nettyCookie.setSecure(secured);
        nettyCookie.setPath(path);
        this.cookies.add(nettyCookie);
        return this;
    }

    @Override
    public Response removeCookie(@NonNull String name) {
        Optional<Cookie> cookieOpt = this.cookies.stream().filter(cookie -> cookie.name().equals(name)).findFirst();
        cookieOpt.ifPresent(cookie -> {
            cookie.setValue("");
            cookie.setMaxAge(-1);
        });
        return this;
    }

    @Override
    public Map<String, String> cookies() {
        Map<String, String> map = new HashMap<>();
        this.cookies.forEach(cookie -> map.put(cookie.name(), cookie.value()));
        return map;
    }

    @Override
    public void download(@NonNull String fileName, @NonNull File file) throws Exception {
        try {
            if (null == file || !file.exists() || !file.isFile()) {
                Assert.throwException("please check the file is effective!");
            }

            RandomAccessFile raf        = new RandomAccessFile(file, "r");
            long             fileLength = raf.length();
            this.contentType = StringKit.mimeType(file.getName());

            io.netty.handler.codec.http.HttpResponse httpResponse = new DefaultHttpResponse(HTTP_1_1, OK);
            HttpHeaders                              httpHeaders  = httpResponse.headers().add(getDefaultHeader());

            boolean keepAlive = WebContext.request().keepAlive();
            if (keepAlive) {
                httpResponse.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            httpHeaders.set(CONTENT_TYPE, this.contentType);
            httpHeaders.set("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859_1"));
            httpHeaders.set(CONTENT_LENGTH, fileLength);

            // Write the initial line and the header.
            ctx.write(httpResponse);

            ChannelFuture sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
            // Write the end marker.
            ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            sendFileFuture.addListener(ProgressiveFutureListener.build(raf));
            // Decide whether to close the connection or not.
            if (!keepAlive) {
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);
            }
            isCommit = true;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void render(@NonNull ModelAndView modelAndView) {
        StringWriter sw = new StringWriter();
        try {
            templateEngine.render(modelAndView, sw);
            ByteBuf          buffer   = Unpooled.wrappedBuffer(sw.toString().getBytes());
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(statusCode), buffer);
            this.send(response);
        } catch (Exception e) {
            log.error("render error", e);
        }
    }

    @Override
    public void redirect(@NonNull String newUri) {
        headers.set(HttpHeaders.Names.LOCATION, newUri);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        this.send(response);
    }

    @Override
    public boolean isCommit() {
        return isCommit;
    }

    @Override
    public void send(@NonNull FullHttpResponse response) {
        response.headers().add(getDefaultHeader());
        boolean keepAlive = WebContext.request().keepAlive();
        // Add 'Content-Length' header only for a keep-alive connection.
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.write(response);
        }
        isCommit = true;
    }

    private HttpHeaders getDefaultHeader() {
        headers.set(DATE, DateKit.gmtDate());
        headers.set(CONTENT_TYPE, this.contentType);
        headers.set(SERVER, "blade/" + Const.VERSION);
        this.cookies.forEach(cookie -> headers.add(SET_COOKIE, ServerCookieEncoder.LAX.encode(cookie)));
        return headers;
    }

    public static HttpResponse build(ChannelHandlerContext ctx, TemplateEngine templateEngine) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.templateEngine = templateEngine;
        httpResponse.ctx = ctx;
        return httpResponse;
    }

}