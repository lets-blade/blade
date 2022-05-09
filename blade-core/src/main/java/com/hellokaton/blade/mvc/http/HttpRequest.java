/**
 * Copyright (c) 2018, biezhi 王爵 nice (hellokaton@gmail.com)
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
package com.hellokaton.blade.mvc.http;

import com.hellokaton.blade.exception.HttpParseException;
import com.hellokaton.blade.kit.CaseInsensitiveHashMap;
import com.hellokaton.blade.kit.PathKit;
import com.hellokaton.blade.kit.StringKit;
import com.hellokaton.blade.mvc.HttpConst;
import com.hellokaton.blade.mvc.WebContext;
import com.hellokaton.blade.mvc.handler.SessionHandler;
import com.hellokaton.blade.mvc.http.session.SessionManager;
import com.hellokaton.blade.mvc.multipart.FileItem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.*;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.hellokaton.blade.mvc.HttpConst.HEADER_ACCEPT_ENCODING;
import static com.hellokaton.blade.mvc.HttpConst.HEADER_HOST;

/**
 * HttpRequest
 *
 * @author hellokaton
 * 2022/5/9
 */
@Slf4j
@NoArgsConstructor
public class HttpRequest implements Request {

    static final HttpDataFactory HTTP_DATA_FACTORY =
            new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed

    static SessionHandler SESSION_HANDLER = null;

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
        DiskFileUpload.baseDirectory = null;             // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true;  // should delete file on
        DiskAttribute.baseDirectory = null;              // system temp directory
    }

    private ByteBuf body = Unpooled.EMPTY_BUFFER;
    private String remoteAddress;
    private String uri;
    private String url;
    private String protocol;
    private String method;
    private String contentType;
    private boolean keepAlive;
    private Session session;
    private boolean isMultipart;

    private Map<String, Object> attributes = Collections.emptyMap();
    private Map<String, String> pathParams = Collections.emptyMap();
    private Map<String, List<String>> queryParams = Collections.emptyMap();
    private Map<String, List<String>> formParams = Collections.emptyMap();
    private Map<String, List<String>> headers = Collections.emptyMap();
    private Map<String, Cookie> cookies = Collections.emptyMap();
    private Map<String, FileItem> fileItems = Collections.emptyMap();

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
            var queryPost = this.url.indexOf('?');
            this.uri = queryPost < 0 ? this.url : this.url.substring(0, queryPost);
        }

        this.formParams = request.formParams();
        this.protocol = request.protocol();
    }

    @Override
    public Request initPathParams(Map<String, String> pathParams) {
        if (null != pathParams)
            this.pathParams = pathParams;
        return this;
    }

    @Override
    public String host() {
        return this.header(HEADER_HOST);
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
    public Map<String, List<String>> queryParams() {
        return this.queryParams;
    }

    @Override
    public Map<String, List<String>> formParams() {
        return this.formParams;
    }

    @Override
    public Set<String> parameterNames() {
        return this.formParams.keySet();
    }

    @Override
    public List<String> formValue(String paramName) {
        return this.formParams.get(paramName);
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
        boolean useGZIP = WebContext.blade().httpOptions().isEnableGzip();
        if (!useGZIP) {
            return false;
        }

        String acceptEncoding = this.header(HEADER_ACCEPT_ENCODING);
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
    public Map<String, List<String>> headers() {
        return this.headers;
    }

    @Override
    public boolean keepAlive() {
        return this.keepAlive;
    }

    @Override
    public Map<String, Object> attributes() {
        if (null == this.attributes || this.attributes.isEmpty()) {
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
    public boolean isMultipart() {
        return isMultipart;
    }

    public HttpRequest(String remoteAddress, FullHttpRequest fullHttpRequest) {
        this.remoteAddress = remoteAddress.substring(1);
        this.keepAlive = HttpUtil.isKeepAlive(fullHttpRequest);
        this.url = fullHttpRequest.uri();

        int queryPos = this.url().indexOf('?');
        this.uri = queryPos < 0 ? this.url() : this.url().substring(0, queryPos);
        this.protocol = fullHttpRequest.protocolVersion().text();
        this.method = fullHttpRequest.method().name();

        String cleanUri = this.uri;
        if (!"/".equals(this.contextPath())) {
            cleanUri = PathKit.cleanPath(cleanUri.replaceFirst(this.contextPath(), "/"));
            this.uri = cleanUri;
        }

        HttpHeaders httpHeaders = fullHttpRequest.headers();
        if (null == this.headers || this.headers.isEmpty()) {
            this.headers = new CaseInsensitiveHashMap<>(httpHeaders.size());
            httpHeaders.forEach(this::putHeaderValues);
        }

        this.parseContentType();
        this.parseCookie();

        if (WebContext.blade().httpOptions().isEnableSession()) {
            if (null == SESSION_HANDLER) {
                SESSION_HANDLER = new SessionHandler(WebContext.blade());
            }
            SessionManager sessionManager = WebContext.blade().sessionManager();
            if (null != sessionManager) {
                this.session = SESSION_HANDLER.createSession(this);
            }
        }

        if (queryPos > 0) {
            QueryStringDecoder queryDecoder = new QueryStringDecoder(this.url, StandardCharsets.UTF_8);
            Map<String, List<String>> query = queryDecoder.parameters();
            if (null != query) {
                this.queryParams = query;
            }
        }

        if (HttpMethod.GET.name().equals(this.method())) {
            return;
        }

        try {
            this.body = fullHttpRequest.content().copy();

            // if request is multipart/form-data
            if (HttpConst.CONTENT_TYPE_MULTIPART.equals(this.contentType)) {
                this.isMultipart = true;
                this.formParams = new HashMap<>(8);
                this.fileItems = new HashMap<>(8);
                HttpPostMultipartRequestDecoder decoder = new HttpPostMultipartRequestDecoder(HTTP_DATA_FACTORY, fullHttpRequest);
                while (decoder.hasNext()) {
                    InterfaceHttpData httpData = decoder.next();
                    this.writeHttpData(httpData);
                }
            } else {
                String paramString = fullHttpRequest.content().toString(StandardCharsets.UTF_8);
                QueryStringDecoder queryDecoder = new QueryStringDecoder(paramString, false);
                Map<String, List<String>> uriAttributes = queryDecoder.parameters();
                if (null != uriAttributes && !uriAttributes.isEmpty()) {
                    this.formParams = uriAttributes;
                }
            }
        } catch (Exception e) {
            throw new HttpParseException("build decoder fail", e);
        }
    }

    private void putHeaderValues(Map.Entry<String, String> header) {
        String headerName = header.getKey();
        String headerValue = header.getValue();
        if (!this.headers.containsKey(headerName)) {
            List<String> values = new ArrayList<>(2);
            values.add(headerValue);
            this.headers.put(headerName, values);
            return;
        }
        List<String> values = this.headers.get(headerName);
        if (null != values && !values.isEmpty()) {
            values.add(headerValue);
            this.headers.put(headerName, values);
        }
    }

    private void parseCookie() {
        List<String> cookies = this.getHeader(HttpConst.HEADER_COOKIE);
        if (null == cookies || cookies.isEmpty()) {
            return;
        }
        this.cookies = new HashMap<>(8);
        for (String cookie : cookies) {
            ServerCookieDecoder.LAX.decode(cookie).forEach(this::parseCookie);
        }
    }

    private void parseContentType() {
        String contentType = this.header(HttpConst.HEADER_CONTENT_TYPE);
        if (null != contentType) {
            contentType = contentType.toLowerCase();
        }
        if (null != contentType && contentType.contains(";")) {
            contentType = contentType.split(";")[0];
        }
        this.contentType = contentType;
    }

    private void writeHttpData(InterfaceHttpData data) {
        boolean canRelease = false;
        try {
            if (null == data) {
                return;
            }
            InterfaceHttpData.HttpDataType dataType = data.getHttpDataType();
            if (dataType == InterfaceHttpData.HttpDataType.Attribute) {
                parseAttribute((Attribute) data);
                canRelease = true;
            } else if (dataType == InterfaceHttpData.HttpDataType.FileUpload) {
                parseFileUpload((FileUpload) data);
            }
        } catch (IOException e) {
            log.error("Parse request parameter error", e);
        } finally {
            if (canRelease) {
                data.release();
            }
        }
    }

    private void parseAttribute(Attribute attribute) throws IOException {
        var name = attribute.getName();
        var value = attribute.getValue();

        if (StringKit.isEmpty(name)) {
            return;
        }
        List<String> values;
        if (this.formParams.containsKey(name)) {
            values = this.formParams.get(name);
            values.add(value);
        } else {
            values = new ArrayList<>(4);
            values.add(value);
            this.formParams.put(name, values);
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
        fileItem.setContentType(fileUpload.getContentType());
        fileItem.setLength(fileUpload.length());

        // Upload the file is moved to the specified temporary file,
        // because FileUpload will be release after completion of the analysis.
        // tmpFile will be deleted automatically if they are used.
        if (fileUpload.isInMemory()) {
            fileItem.setInMemory(true);
            fileItem.setData(fileUpload.get());
        } else {
            fileItem.setFile(fileUpload.getFile());
            fileItem.setPath(fileUpload.getFile().getPath());
            fileItem.setInMemory(false);
        }
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