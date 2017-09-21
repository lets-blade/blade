package com.blade.mvc.http;

import com.blade.kit.StringKit;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.route.Route;
import com.blade.server.netty.SessionHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;

/**
 * Http Request Impl
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
public class HttpRequest implements Request {

    private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = null;
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = null;
    }

    private SessionHandler sessionHandler;
    private ByteBuf body = Unpooled.copiedBuffer("", CharsetUtil.UTF_8);
    private String  host;
    private String  uri;
    private String  url;
    private String  protocol;
    private String  method;
    private boolean keepAlive;

    private Map<String, String>       headers    = new HashMap<>();
    private Map<String, Object>       attributes = new HashMap<>();
    private Map<String, List<String>> parameters = new HashMap<>();
    private Map<String, String>       pathParams = new HashMap<>();
    private Map<String, Cookie>       cookies    = new HashMap<>();
    private Map<String, FileItem>     fileItems  = new HashMap<>();

    private void init(FullHttpRequest fullHttpRequest) {
        // headers
        fullHttpRequest.headers().forEach((header) -> headers.put(header.getKey(), header.getValue()));

        // body content
        this.body = fullHttpRequest.content().copy();

        // request query parameters
        this.parameters.putAll(new QueryStringDecoder(fullHttpRequest.uri(), CharsetUtil.UTF_8).parameters());

        if (!fullHttpRequest.method().name().equals("GET")) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, fullHttpRequest);
            decoder.getBodyHttpDatas().stream().forEach(this::parseData);
        }

        // cookies
        if (StringKit.isNotBlank(header(COOKIE))) {
            ServerCookieDecoder.LAX.decode(header(COOKIE)).forEach(this::parseCookie);
        }
    }

    private void parseData(InterfaceHttpData data) {
        try {
            switch (data.getHttpDataType()) {
                case Attribute:
                    Attribute attribute = (Attribute) data;
                    String name = attribute.getName();
                    String value = attribute.getValue();
                    this.parameters.put(name, Arrays.asList(value));
                    break;
                case FileUpload:
                    FileUpload fileUpload = (FileUpload) data;
                    parseFileUpload(fileUpload);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            log.error("parse request parameter error", e);
        } finally {
            data.release();
        }
    }

    private void parseFileUpload(FileUpload fileUpload) throws IOException {
        if (fileUpload.isCompleted()) {
            String contentType = StringKit.mimeType(fileUpload.getFilename());
            if (null == contentType) {
                contentType = URLConnection.guessContentTypeFromName(fileUpload.getFilename());
            }
            if (fileUpload.isInMemory()) {
                FileItem fileItem = new FileItem(fileUpload.getName(), fileUpload.getFilename(),
                        contentType, fileUpload.length());
                fileItem.setData(fileUpload.getByteBuf().array());
                fileItems.put(fileItem.getName(), fileItem);
            } else {
                FileItem fileItem = new FileItem(fileUpload.getName(), fileUpload.getFilename(),
                        contentType, fileUpload.length());
                byte[] bytes = Files.readAllBytes(fileUpload.getFile().toPath());
                fileItem.setData(bytes);
                fileItems.put(fileItem.getName(), fileItem);
            }
        }
    }

    /**
     * parse netty cookie to {@link Cookie}.
     *
     * @param nettyCookie
     */
    private void parseCookie(io.netty.handler.codec.http.cookie.Cookie nettyCookie) {
        Cookie cookie = new Cookie();
        cookie.name(nettyCookie.name());
        cookie.value(nettyCookie.value());
        cookie.httpOnly(nettyCookie.isHttpOnly());
        cookie.path(nettyCookie.path());
        cookie.domain(nettyCookie.domain());
        cookie.maxAge(nettyCookie.maxAge());
        this.cookies.put(cookie.name(), cookie);
    }

    @Override
    public Request initPathParams(@NonNull Route route) {
        if (null != route.getPathParams())
            this.pathParams = route.getPathParams();
        return this;
    }

    @Override
    public String host() {
        return this.host;
    }

    @Override
    public String uri() {
        return this.uri;
    }

    @Override
    public String url() {
        return this.url;
    }

    @Override
    public String protocol() {
        return this.protocol;
    }

    @Override
    public Map<String, String> pathParams() {
        return this.pathParams;
    }

    @Override
    public String queryString() {
        return this.url;
    }

    @Override
    public Map<String, List<String>> parameters() {
        return parameters;
    }

    @Override
    public String method() {
        return this.method;
    }

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.valueOf(method());
    }

    @Override
    public Session session() {
        return sessionHandler.createSession(this);
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public boolean isIE() {
        String ua = userAgent();
        return ua.contains("MSIE") || ua.contains("TRIDENT");
    }

    @Override
    public Map<String, String> cookies() {
        Map<String, String> map = new HashMap<>(cookies.size());
        this.cookies.forEach((name, cookie) -> map.put(name, cookie.value()));
        return map;
    }

    @Override
    public Optional<Cookie> cookieRaw(@NonNull String name) {
        return Optional.ofNullable(this.cookies.get(name));
    }

    @Override
    public Request cookie(@NonNull Cookie cookie) {
        this.cookies.put(cookie.name(), cookie);
        return this;
    }

    @Override
    public Map<String, String> headers() {
        return this.headers;
    }

    @Override
    public boolean keepAlive() {
        return this.keepAlive;
    }

    @Override
    public Map<String, Object> attributes() {
        return this.attributes;
    }

    @Override
    public Map<String, FileItem> fileItems() {
        return fileItems;
    }

    @Override
    public ByteBuf body() {
        return this.body;
    }

    @Override
    public String bodyToString() {
        return this.body.toString(CharsetUtil.UTF_8);
    }

    public static HttpRequest build(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest, SessionHandler sessionHandler) {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.sessionHandler = sessionHandler;
        httpRequest.keepAlive = HttpUtil.isKeepAlive(fullHttpRequest);
        String remoteAddress = ctx.channel().remoteAddress().toString();
        httpRequest.host = StringKit.isNotBlank(remoteAddress) ? remoteAddress.substring(1) : "Unknown";
        httpRequest.url = fullHttpRequest.uri();
        int pathEndPos = httpRequest.url.indexOf('?');
        httpRequest.uri = pathEndPos < 0 ? httpRequest.url : httpRequest.url.substring(0, pathEndPos);
        httpRequest.protocol = fullHttpRequest.protocolVersion().text();
        httpRequest.method = fullHttpRequest.method().name();
        httpRequest.init(fullHttpRequest);
        return httpRequest;
    }

}