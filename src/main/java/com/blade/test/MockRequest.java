package com.blade.test;

import com.blade.mvc.http.Cookie;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Session;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.route.Route;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author biezhi
 * @date 2018/5/31
 */
public class MockRequest implements Request {

    private String  httpMethod;
    private String  url;
    private String  uri;
    private String  host;
    private String  queryString;
    private boolean secure;

    private Map<String, Cookie>       cookies    = new HashMap<>();
    private Map<String, String>       headers    = new HashMap<>();
    private Map<String, FileItem>     files      = new HashMap<>();
    private Map<String, List<String>> parameters = new HashMap<>();
    private Map<String, String>       pathParams = null;
    private Map<String, Object>       attributes = null;


    public MockRequest(String httpMethod, String url) {
        this.httpMethod = httpMethod;
        this.url = url;
        URI uri;
        try {
            uri = new URI(url);
            this.host = uri.getHost();
            this.uri = uri.getPath();
            this.queryString = uri.getQuery();
            if (uri.getScheme().equals("https")) {
                this.secure = true;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Request initPathParams(Route route) {
        if (null != route.getPathParams())
            this.pathParams = route.getPathParams();
        return this;
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public String remoteAddress() {
        return null;
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String protocol() {
        return "http";
    }

    public MockRequest addParameter(String name, String value) {
        parameters.put(name, Arrays.asList(value));
        return this;
    }

    @Override
    public Map<String, String> pathParams() {
        return this.pathParams;
    }

    @Override
    public String queryString() {
        return this.queryString;
    }

    @Override
    public Map<String, List<String>> parameters() {
        return this.parameters;
    }

    @Override
    public String method() {
        return this.httpMethod;
    }

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.valueOf(this.httpMethod);
    }

    @Override
    public Session session() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public Map<String, Cookie> cookies() {
        return this.cookies;
    }

    @Override
    public Cookie cookieRaw(String name) {
        return this.cookies.get(name);
    }

    @Override
    public Request cookie(Cookie cookie) {
        return this;
    }

    @Override
    public Map<String, String> headers() {
        return this.headers;
    }

    @Override
    public boolean keepAlive() {
        return true;
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
        return this.files;
    }

    @Override
    public ByteBuf body() {
        return Unpooled.copiedBuffer("", CharsetUtil.UTF_8);
    }

}
