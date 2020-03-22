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
import com.blade.kit.CaseInsensitiveHashMap;
import com.blade.kit.PathKit;
import com.blade.kit.StringKit;
import com.blade.mvc.Const;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.SessionHandler;
import com.blade.mvc.http.session.SessionManager;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.route.Route;
import com.blade.server.netty.HttpConst;
import com.blade.server.netty.HttpServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Http Request Impl
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
@NoArgsConstructor
public class HttpRequest implements Request {

    private static final HttpDataFactory HTTP_DATA_FACTORY =
            new DefaultHttpDataFactory(true); // Disk if size exceed

    private static final ByteBuf EMPTY_BUF = Unpooled.copiedBuffer("", CharsetUtil.UTF_8);

    private static final SessionHandler SESSION_HANDLER = new SessionHandler(WebContext.blade());

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
        DiskFileUpload.baseDirectory = null;             // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true;  // should delete file on
        DiskAttribute.baseDirectory = null;              // system temp directory
    }

    private ByteBuf body = EMPTY_BUF;
    private String remoteAddress;
    private String uri;
    private String url;
    private String protocol;
    private String method;
    private boolean keepAlive;
    private Session session;

    private boolean isMultipart;
    private boolean isEnd;
    private boolean initCookie;
    private boolean initQueryParam;

    private HttpData partialContent;

    private HttpHeaders httpHeaders;

    private io.netty.handler.codec.http.HttpRequest nettyRequest;
    private HttpPostRequestDecoder decoder;

    private Queue<HttpContent> contents = new LinkedList<>();

    private Map<String, String> headers = null;
    private Map<String, Object> attributes = null;
    private Map<String, String> pathParams = null;
    private Map<String, List<String>> parameters = new HashMap<>(8);
    private Map<String, Cookie> cookies = new HashMap<>(8);
    private Map<String, FileItem> fileItems = new HashMap<>(8);

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
        if (initQueryParam) {
            return this.parameters;
        }

        initQueryParam = true;
        if (!url.contains("?")) {
            return this.parameters;
        }

        var parameters =
                new QueryStringDecoder(url, CharsetUtil.UTF_8).parameters();

        if (null != parameters) {
            this.parameters.putAll(parameters);
        }
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
        if (!initCookie) {
            initCookie = true;
            String cookie = header(HttpConst.COOKIE_STRING);
            if (StringKit.isNotEmpty(cookie)) {
                ServerCookieDecoder.LAX.decode(cookie).forEach(this::parseCookie);
            }
        }
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
        if (null == headers) {
            headers = new CaseInsensitiveHashMap<>(httpHeaders.size());
            for (Map.Entry<String, String> header : httpHeaders) {
                headers.put(header.getKey(), header.getValue());
            }
        }
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
    public boolean chunkIsEnd() {
        return this.isEnd;
    }

    @Override
    public boolean isMultipart() {
        return isMultipart;
    }

    public void setNettyRequest(io.netty.handler.codec.http.HttpRequest nettyRequest) {
        this.nettyRequest = nettyRequest;
    }

    public void appendContent(HttpContent msg) {
        this.contents.add(msg.retain());
        if (msg instanceof LastHttpContent) {
            this.isEnd = true;
        }
    }

    public void init(String remoteAddress) {
        this.remoteAddress = remoteAddress.substring(1);
        this.keepAlive = HttpUtil.isKeepAlive(nettyRequest);
        this.url = nettyRequest.uri();

        int pathEndPos = this.url().indexOf('?');
        this.uri = pathEndPos < 0 ? this.url() : this.url().substring(0, pathEndPos);
        this.protocol = nettyRequest.protocolVersion().text();
        this.method = nettyRequest.method().name();

        String cleanUri = this.uri;
        if (!"/".equals(this.contextPath())) {
            cleanUri = PathKit.cleanPath(cleanUri.replaceFirst(this.contextPath(), "/"));
            this.uri = cleanUri;
        }

        this.httpHeaders = nettyRequest.headers();

        if (!HttpServerHandler.PERFORMANCE) {
            SessionManager sessionManager = WebContext.blade().sessionManager();
            if (null != sessionManager) {
                this.session = SESSION_HANDLER.createSession(this);
            }
        }

        if ("GET".equals(this.method())) {
            return;
        }

        try {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, nettyRequest);
            this.isMultipart = decoder.isMultipart();

            List<ByteBuf> byteBuffs = new ArrayList<>(this.contents.size());

            for (HttpContent content : this.contents) {
                if (!isMultipart) {
                    byteBuffs.add(content.content().copy());
                }

                decoder.offer(content);
                this.readHttpDataChunkByChunk(decoder);
                content.release();
            }
            if (!byteBuffs.isEmpty()) {
                this.body = Unpooled.copiedBuffer(byteBuffs.toArray(new ByteBuf[0]));
            }
        } catch (Exception e) {
            throw new HttpParseException("build decoder fail", e);
        }
    }

    /**
     * Example of reading request by chunk and getting values from chunk to chunk
     */
    private void readHttpDataChunkByChunk(HttpPostRequestDecoder decoder) {
        try {
            while (decoder.hasNext()) {
                InterfaceHttpData data = decoder.next();
                if (data != null) {
                    // check if current HttpData is a FileUpload and previously set as partial
                    if (partialContent == data) {
                        partialContent = null;
                    }
                    try {
                        // new value
                        writeHttpData(data);
                    } finally {
                        data.release();
                    }
                }
            }
            // Check partial decoding for a FileUpload
            InterfaceHttpData data = decoder.currentPartialHttpData();
            if (data != null) {
                if (partialContent == null) {
                    partialContent = (HttpData) data;
                }
            }
        } catch (HttpPostRequestDecoder.EndOfDataDecoderException e1) {
            // end
        }
    }

    private void writeHttpData(InterfaceHttpData data) {
        try {
            InterfaceHttpData.HttpDataType dataType = data.getHttpDataType();
            if (dataType == InterfaceHttpData.HttpDataType.Attribute) {
                parseAttribute((Attribute) data);
            } else if (dataType == InterfaceHttpData.HttpDataType.FileUpload) {
                parseFileUpload((FileUpload) data);
            }
        } catch (IOException e) {
            log.error("Parse request parameter error", e);
        }
    }

    private void parseAttribute(Attribute attribute) throws IOException {
        var name = attribute.getName();
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