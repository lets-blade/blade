/**
 * Copyright (c) 2018, biezhi 王爵 nice (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.mvc.http;

import com.blade.exception.HttpParseException;
import com.blade.exception.InternalErrorException;
import com.blade.kit.PathKit;
import com.blade.kit.StringKit;
import com.blade.mvc.Const;
import com.blade.mvc.LocalContext;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.SessionHandler;
import com.blade.mvc.http.session.SessionManager;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.route.Route;
import com.blade.server.netty.HttpConst;
import com.blade.server.netty.HttpServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Http Request Impl
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
@NoArgsConstructor
public class HttpRequest implements Request {

    private static final HttpDataFactory factory =
            new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed

    private static final ByteBuf EMPTY_BUF = Unpooled.copiedBuffer("", CharsetUtil.UTF_8);

    private static final SessionHandler SESSION_HANDLER = new SessionHandler(WebContext.blade());

    static {
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on
        DiskAttribute.baseDirectory = null;             // system temp directory
    }

    private ByteBuf body = EMPTY_BUF;
    private String  remoteAddress;
    private String  uri;
    private String  url;
    private String  protocol;
    private String  method;
    private boolean keepAlive;
    private Session session;

    private boolean isRequestPart;
    private boolean isChunked;
    private boolean isMultipart;
    private boolean isEnd;

    private Map<String, String>       headers    = null;
    private Map<String, Object>       attributes = null;
    private Map<String, String>       pathParams = null;
    private Map<String, List<String>> parameters = new HashMap<>(8);
    private Map<String, Cookie>       cookies    = new HashMap<>(8);
    private Map<String, FileItem>     fileItems  = new HashMap<>(8);

    public HttpRequest(Request request) {
        this.pathParams = request.pathParams();
        this.cookies = request.cookies();
        this.attributes = request.attributes();
        this.body = request.body();
        this.fileItems = request.fileItems();
        this.headers = request.headers();
        this.keepAlive = request.keepAlive();
        this.method = request.method();
        this.url = request.url();

        if (null != this.url && this.url.length() > 0) {
            var pathEndPos = this.url.indexOf('?');
            this.uri = pathEndPos < 0 ? this.url : this.url.substring(0, pathEndPos);
        }

        this.parameters = request.parameters();
        this.protocol = request.protocol();
    }

    @Override
    public Request initPathParams(@NonNull Route route) {
        if (null != route.getPathParams())
            this.pathParams = route.getPathParams();
        return this;
    }

    @Override
    public String host() {
        return this.header("Host");
    }

    @Override
    public String remoteAddress() {
        return this.remoteAddress;
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
        if (null == url || !url.contains("?")) {
            return "";
        }
        return url.substring(url.indexOf("?") + 1);
    }

    @Override
    public Map<String, List<String>> parameters() {
        return this.parameters;
    }

    @Override
    public Set<String> parameterNames() {
        return this.parameters.keySet();
    }

    @Override
    public List<String> parameterValues(String paramName) {
        return this.parameters.get(paramName);
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
    public boolean useGZIP() {

        boolean useGZIP = WebContext.blade().environment()
                .getBoolean(Const.ENV_KEY_GZIP_ENABLE, false);

        if (!useGZIP) {
            return false;
        }

        String acceptEncoding = this.header(HttpConst.ACCEPT_ENCODING);
        if (StringKit.isEmpty(acceptEncoding)) {
            return false;
        }
        return acceptEncoding.contains("gzip");
    }

    @Override
    public Session session() {
        return this.session;
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
    public Map<String, Cookie> cookies() {
        return this.cookies;
    }

    @Override
    public Cookie cookieRaw(@NonNull String name) {
        return this.cookies().get(name);
    }

    @Override
    public Request cookie(@NonNull Cookie cookie) {
        this.cookies().put(cookie.name(), cookie);
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
        if (null == this.attributes) {
            this.attributes = new HashMap<>(4);
        }
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
    public boolean readChunk() {
        LocalContext localContext = HttpServerHandler.getLocalContext();
        if (null == localContext) {
            throw new InternalErrorException("It is impossible to run here");
        }

        HttpObject msg = localContext.msg();
        localContext.updateMsg(msg);

        if (msg instanceof LastHttpContent) {
            this.isEnd = true;

            if (!localContext.request().isMultipart) {
                this.body = ((HttpContent) msg).copy().content();
            }
        }

        if (localContext.hasDecoder() && msg instanceof HttpContent) {
            // New chunk is received
            HttpContent chunk = (HttpContent) msg;
            localContext.decoder().offer(chunk);
            readHttpDataChunkByChunk(localContext.decoder());
        }

        return this.isEnd;
    }

    @Override
    public boolean chunkIsEnd() {
        return this.isEnd;
    }

    @Override
    public boolean isPart() {
        return isRequestPart;
    }

    @Override
    public boolean isChunked() {
        return isChunked;
    }

    public static HttpRequest build(String remoteAddress, HttpObject msg) {
        boolean isRequestPart = false;

        io.netty.handler.codec.http.HttpRequest nettyRequest = null;
        if (msg instanceof io.netty.handler.codec.http.HttpRequest) {
            isRequestPart = true;
            nettyRequest = (io.netty.handler.codec.http.HttpRequest) msg;
        }

        LocalContext localContext = HttpServerHandler.getLocalContext();
        if (null != localContext) {
            HttpRequest request = localContext.request();
            request.isRequestPart = isRequestPart;

            localContext.updateMsg(msg);
            return request;
        }

        if (!isRequestPart) {
            return null;
        }

        HttpRequest request = new HttpRequest();
        request.isRequestPart = true;
        request.keepAlive = HttpUtil.isKeepAlive(nettyRequest);
        request.remoteAddress = remoteAddress;
        request.url = nettyRequest.uri();
        request.isChunked = HttpUtil.isTransferEncodingChunked(nettyRequest);

        int pathEndPos = request.url().indexOf('?');
        request.uri = pathEndPos < 0 ? request.url() : request.url().substring(0, pathEndPos);
        request.protocol = nettyRequest.protocolVersion().text();
        request.method = nettyRequest.method().name();

        HttpPostRequestDecoder decoder = initRequest(request, nettyRequest);

        String cleanUri = request.uri;
        if (!"/".equals(request.contextPath())) {
            cleanUri = PathKit.cleanPath(cleanUri.replaceFirst(request.contextPath(), "/"));
            request.uri = cleanUri;
        }

        SessionManager sessionManager = WebContext.blade().sessionManager();
        if (null != sessionManager) {
            request.session = SESSION_HANDLER.createSession(request);
        }

        HttpServerHandler.setLocalContext(new LocalContext(msg, request, decoder));
        return request;
    }

    private static HttpPostRequestDecoder initRequest(
            HttpRequest request,
            io.netty.handler.codec.http.HttpRequest nettyRequest) {

        // headers
        var httpHeaders = nettyRequest.headers();
        if (httpHeaders.isEmpty()) {
            request.headers = new HashMap<>();
        } else {
            request.headers = new HashMap<>(httpHeaders.size());

            httpHeaders.forEach(entry ->
                    request.headers.put(entry.getKey(), entry.getValue()));
        }

        // request query parameters
        var parameters = new QueryStringDecoder(request.url(), CharsetUtil.UTF_8)
                .parameters();

        if (null != parameters) {
            request.parameters = new HashMap<>();
            request.parameters.putAll(parameters);
        }

        request.initCookie();

        if ("GET".equals(request.method())) {
            return null;
        }

        try {
            HttpPostRequestDecoder decoder =
                    new HttpPostRequestDecoder(factory, nettyRequest);

            request.isMultipart = decoder.isMultipart();
            return decoder;
        } catch (Exception e) {
            throw new HttpParseException("build decoder fail", e);
        }
    }

    private void initCookie() {
        // cookies
        var cookie = this.header(HttpConst.COOKIE_STRING);

        cookie = cookie.length() > 0 ?
                cookie : this.header(HttpConst.COOKIE_STRING.toLowerCase());

        if (null != cookie && cookie.length() > 0) {
            ServerCookieDecoder.LAX.decode(cookie).forEach(this::parseCookie);
        }
    }

    /**
     * Example of reading request by chunk and getting values from chunk to chunk
     */
    private boolean readHttpDataChunkByChunk(HttpPostRequestDecoder decoder) {
        try {
            boolean read = false;
            while (decoder.hasNext()) {
                read = true;
                InterfaceHttpData data = decoder.next();
                if (data != null) {
                    parseData(data);
                }
            }
            return read;
        } catch (HttpPostRequestDecoder.EndOfDataDecoderException e) {
            // ignore
            return true;
        } catch (Exception e) {
            throw new HttpParseException(e);
        }
    }

    private void parseData(InterfaceHttpData data) {
        try {
            switch (data.getHttpDataType()) {
                case Attribute:
                    this.parseAttribute((Attribute) data);
                    break;
                case FileUpload:
                    this.parseFileUpload((FileUpload) data);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            log.error("Parse request parameter error", e);
        } finally {
            data.release();
        }
    }

    private void parseAttribute(Attribute attribute) throws IOException {
        var name  = attribute.getName();
        var value = attribute.getValue();

        List<String> values;
        if (this.parameters.containsKey(name)) {
            values = this.parameters.get(name);
            values.add(value);
        } else {
            values = new ArrayList<>();
            values.add(value);
            this.parameters.put(name, values);
        }
    }

    /**
     * Parse FileUpload to {@link FileItem}.
     *
     * @param fileUpload netty http file upload
     * @throws IOException
     */
    private void parseFileUpload(FileUpload fileUpload) throws IOException {
        if (!fileUpload.isCompleted()) {
            return;
        }
        FileItem fileItem = new FileItem();
        fileItem.setName(fileUpload.getName());
        fileItem.setFileName(fileUpload.getFilename());

        // Upload the file is moved to the specified temporary file,
        // because FileUpload will be release after completion of the analysis.
        // tmpFile will be deleted automatically if they are used.
        Path tmpFile = Files.createTempFile(
                Paths.get(fileUpload.getFile().getParent()), "blade_", "_upload");

        Path fileUploadPath = Paths.get(fileUpload.getFile().getPath());
        Files.move(fileUploadPath, tmpFile, StandardCopyOption.REPLACE_EXISTING);

        fileItem.setFile(tmpFile.toFile());
        fileItem.setPath(tmpFile.toFile().getPath());
        fileItem.setContentType(fileUpload.getContentType());
        fileItem.setLength(fileUpload.length());

        fileItems.put(fileItem.getName(), fileItem);
    }

    /**
     * Parse netty cookie to {@link Cookie}.
     *
     * @param nettyCookie netty raw cookie instance
     */
    private void parseCookie(io.netty.handler.codec.http.cookie.Cookie nettyCookie) {
        var cookie = new Cookie();
        cookie.name(nettyCookie.name());
        cookie.value(nettyCookie.value());
        cookie.httpOnly(nettyCookie.isHttpOnly());
        cookie.path(nettyCookie.path());
        cookie.domain(nettyCookie.domain());
        cookie.maxAge(nettyCookie.maxAge());
        this.cookies.put(cookie.name(), cookie);
    }

}