/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
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
package com.blade.mvc.http.wrapper;

import com.blade.kit.CollectionKit;
import com.blade.kit.IOKit;
import com.blade.kit.ObjectKit;
import com.blade.kit.StringKit;
import com.blade.mvc.handler.MultipartHandler;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Path;
import com.blade.mvc.http.Request;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.multipart.Multipart;
import com.blade.mvc.multipart.MultipartException;
import com.blade.mvc.route.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ServletRequest
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.7.0-beta
 */
public class ServletRequest implements Request {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletRequest.class);

    private static final String USER_AGENT = "user-agent";
    protected Route route;
    private HttpServletRequest request;

    /**
     * path parameter eg: /user/12
     */
    private Map<String, String> pathParams = null;

    /**
     * query parameter eg: /user?name=jack
     */
    private Map<String, String> queryParams = null;

    private Map<String, FileItem> fileItems = null;
    private Session session = null;
    private boolean isAbort = false;

    public ServletRequest(HttpServletRequest request) throws MultipartException, IOException {
        this.request = request;
        this.pathParams = CollectionKit.newHashMap(8);
        this.queryParams = CollectionKit.newHashMap(16);
        this.fileItems = CollectionKit.newHashMap(8);
        this.init();
    }

    public void init() throws IOException, MultipartException {

        // retrieve multipart/form-data parameters
        if (Multipart.isMultipartContent(request)) {
            Multipart multipart = new Multipart();
            multipart.parse(request, new MultipartHandler() {
                @Override
                public void handleFormItem(String name, String value) {
                    queryParams.put(name, value);
                }

                @Override
                public void handleFileItem(String name, FileItem fileItem) {
                    fileItems.put(name, fileItem);
                }
            });
        }
    }

    private String join(String[] arr) {
        StringBuilder ret = new StringBuilder();
        for (String item : arr) {
            ret.append(',').append(item);
        }
        if (ret.length() > 0) {
            return ret.substring(1);
        }
        return ret.toString();
    }

    @Override
    public void initPathParams(String routePath) {
        this.pathParams.clear();

        List<String> variables = getPathParam(routePath);
        String regexPath = routePath.replaceAll(Path.VAR_REGEXP, Path.VAR_REPLACE);

        String uri = Path.getRelativePath(uri(), contextPath());

        Matcher matcher = Pattern.compile("(?i)" + regexPath).matcher(uri);

        if (matcher.matches()) {
            // start index at 1 as group(0) always stands for the entire expression
            for (int i = 1, len = variables.size(); i <= len; i++) {
                String value = matcher.group(i);
                this.pathParams.put(variables.get(i - 1), value);
            }
        }
    }

    private List<String> getPathParam(String routePath) {
        List<String> variables = CollectionKit.newArrayList(8);
        Matcher matcher = Pattern.compile(Path.VAR_REGEXP).matcher(routePath);
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        return variables;
    }

    @Override
    public HttpServletRequest raw() {
        return request;
    }

    @Override
    public String host() {
        return request.getServerName();
    }

    @Override
    public String url() {
        return request.getRequestURL().toString();
    }

    @Override
    public String uri() {
        return Path.fixPath(request.getRequestURI());
    }

    @Override
    public String userAgent() {
        return request.getHeader(USER_AGENT);
    }

    @Override
    public String pathInfo() {
        return request.getPathInfo();
    }

    @Override
    public String protocol() {
        return request.getProtocol();
    }

    @Override
    public String servletPath() {
        return request.getServletPath();
    }

    @Override
    public String contextPath() {
        return request.getContextPath();
    }

    @Override
    public ServletContext context() {
        return request.getServletContext();
    }

    @Override
    public Map<String, String> pathParams() {
        return pathParams;
    }

    @Override
    public String pathParam(String name) {
        return pathParams.get(name);
    }

    @Override
    public String pathString(String name) {
        return pathParams.get(name);
    }

    @Override
    public String pathParam(String name, String defaultValue) {
        String val = pathParams.get(name);
        if (null == val) {
            val = defaultValue;
        }
        return val;
    }

    @Override
    public String pathString(String name, String defaultValue) {
        String val = pathParams.get(name);
        if (null == val) {
            val = defaultValue;
        }
        return val;
    }

    @Override
    public int pathParamAsInt(String name) {
        String value = pathString(name);
        if (StringKit.isNotBlank(value)) {
            return Integer.parseInt(value);
        }
        return 0;
    }

    @Override
    public int pathInt(String name) {
        String value = pathString(name);
        if (StringKit.isNotBlank(value)) {
            return Integer.parseInt(value);
        }
        return 0;
    }

    @Override
    public long pathParamAsLong(String name) {
        String value = pathString(name);
        if (StringKit.isNotBlank(value)) {
            return Long.parseLong(value);
        }
        return 0;
    }

    @Override
    public long pathLong(String name) {
        String value = pathString(name);
        if (StringKit.isNotBlank(value)) {
            return Long.parseLong(value);
        }
        return 0;
    }

    @Override
    public String queryString() {
        return request.getQueryString();
    }

    @Override
    public Map<String, String> querys() {
        Map<String, String> params = CollectionKit.newHashMap(8);
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            params.put(entry.getKey(), join(entry.getValue()));
        }
        params.putAll(queryParams);
        return Collections.unmodifiableMap(params);
    }

    @Override
    public String query(String name) {
        return request.getParameter(name);
    }

    @Override
    public String query(String name, String defaultValue) {
        String val = this.query(name);
        if (null == val) {
            val = defaultValue;
        }
        return val;
    }

    @Override
    public int queryAsInt(String name) {
        return queryInt(name);
    }

    @Override
    public int queryInt(String name) {
        return queryInt(name, 0);
    }

    @Override
    public int queryInt(String name, int defaultValue) {
        String value = query(name);
        if (StringKit.isBlank(value)) {
            return defaultValue;
        }
        return Integer.valueOf(value);
    }

    @Override
    public long queryAsLong(String name) {
        return queryLong(name);
    }

    @Override
    public long queryLong(String name) {
        return queryLong(name, 0);
    }

    @Override
    public long queryLong(String name, long defaultValue) {
        String value = query(name);
        if (StringKit.isBlank(value)) {
            return defaultValue;
        }
        return Long.valueOf(value);
    }

    @Override
    public double queryAsDouble(String name) {
        String value = query(name);
        if (StringKit.isBlank(value)) {
            return 0;
        }
        return Double.valueOf(value);
    }

    @Override
    public double queryDouble(String name) {
        return queryDouble(name, 0);
    }

    @Override
    public double queryDouble(String name, double defaultValue) {
        String value = query(name);
        if (StringKit.isBlank(value)) {
            return defaultValue;
        }
        return Double.valueOf(value);
    }

    @Override
    public String method() {
        return request.getMethod();
    }

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.valueOf(request.getMethod().toUpperCase());
    }

    @Override
    public String address() {
        return request.getRemoteAddr();
    }

    @Override
    public Session session() {
        if (session == null) {
            session = new Session(request.getSession());
        }
        return session;
    }

    @Override
    public Session session(boolean create) {
        if (session == null) {
            HttpSession httpSession = request.getSession(create);
            if (httpSession != null) {
                session = new Session(httpSession);
            }
        }
        return session;
    }

    @Override
    public void attribute(String name, Object value) {
        request.setAttribute(name, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T attribute(String name) {
        Object object = request.getAttribute(name);
        if (null != object) {
            return (T) object;
        }
        return null;
    }

    @Override
    public Set<String> attributes() {
        Set<String> attrList = CollectionKit.newHashSet(8);
        Enumeration<String> attributes = request.getAttributeNames();
        while (attributes.hasMoreElements()) {
            attrList.add(attributes.nextElement());
        }
        return attrList;
    }

    @Override
    public String contentType() {
        return request.getContentType();
    }

    @Override
    public int port() {
        return request.getServerPort();
    }

    @Override
    public boolean isSecure() {
        return request.isSecure();
    }

    @Override
    public boolean isAjax() {
        return null != header("x-requested-with") && "XMLHttpRequest".equals(header("x-requested-with"));
    }

    @Override
    public Map<String, Cookie> cookies() {
        javax.servlet.http.Cookie[] servletCookies = request.getCookies();
        Map<String, Cookie> cookies = CollectionKit.newHashMap(8);
        for (javax.servlet.http.Cookie c : servletCookies) {
            cookies.put(c.getName(), map(c));
        }
        return Collections.unmodifiableMap(cookies);
    }

    private Cookie map(Cookie servletCookie) {
        Cookie cookie = new Cookie(servletCookie.getName(), servletCookie.getValue());
        cookie.setMaxAge(servletCookie.getMaxAge());
        cookie.setHttpOnly(servletCookie.isHttpOnly());
        String path = servletCookie.getPath();
        if (null != path) {
            cookie.setPath(path);
        }
        String domain = servletCookie.getDomain();
        if (null != domain) {
            cookie.setDomain(domain);
        }
        cookie.setSecure(servletCookie.getSecure());
        return cookie;
    }

    @Override
    public String cookie(String name) {
        return this.cookie(name, null);
    }

    @Override
    public String cookie(String name, String defaultValue) {
        Cookie cookie = cookieRaw(name);
        if (null != cookie) {
            return cookie.getValue();
        }
        return defaultValue;
    }

    @Override
    public Cookie cookieRaw(String name) {
        javax.servlet.http.Cookie[] servletCookies = request.getCookies();
        if (servletCookies == null) {
            return null;
        }
        for (javax.servlet.http.Cookie c : servletCookies) {
            if (c.getName().equals(name)) {
                return map(c);
            }
        }
        return null;
    }

    @Override
    public Map<String, String> headers() {
        Enumeration<String> servletHeaders = request.getHeaderNames();
        Map<String, String> headers = CollectionKit.newHashMap(16);
        while (servletHeaders.hasMoreElements()) {
            String headerName = servletHeaders.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    @Override
    public String header(String name) {
        return request.getHeader(name);
    }

    @Override
    public String header(String name, String defaultValue) {
        String value = request.getHeader(name);
        if (StringKit.isBlank(value)) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public void encoding(String encoding) {
        try {
            request.setCharacterEncoding(encoding);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void setRoute(Route route) {
        this.route = route;
        initPathParams(route.getPath());
    }

    @Override
    public Route route() {
        return this.route;
    }

    @Override
    public void abort() {
        this.isAbort = true;
    }

    @Override
    public boolean isAbort() {
        return this.isAbort;
    }

    @Override
    public <T> T model(String slug, Class<? extends Serializable> clazz) {
        if (StringKit.isNotBlank(slug) && null != clazz) {
            return ObjectKit.model(slug, clazz, querys());
        }
        return null;
    }

    @Override
    public FileItem[] files() {
        return this.fileItems.values().toArray(new FileItem[fileItems.size()]);
    }

    @Override
    public Map<String, FileItem> fileItems() {
        return this.fileItems;
    }

    @Override
    public FileItem fileItem(String name) {
        return this.fileItems.get(name);
    }

    @Override
    public BodyParser body() {
        return new BodyParser() {
            @Override
            public String asString() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = reader.readLine();
                    while (null != line) {
                        sb.append(line).append("\r\n");
                        line = reader.readLine();
                    }
                    reader.close();
                    return sb.toString();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return null;
            }

            @Override
            public InputStream asInputStream() {
                try {
                    return request.getInputStream();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return null;
            }

            @Override
            public byte[] asByte() {
                try {
                    return IOKit.toByteArray(request.getInputStream());
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return null;
            }
        };
    }

}