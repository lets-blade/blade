package com.blade.mvc.http;

import com.blade.kit.StringKit;
import com.blade.kit.WebKit;
import com.blade.mvc.WebContext;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.route.Route;
import io.netty.buffer.ByteBuf;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.USER_AGENT;

/**
 * Http Request
 *
 * @author biezhi
 * 2017/5/31
 */
public interface Request {

    /**
     * init request path parameters
     *
     * @param route route object
     * @return Return request
     */
    Request initPathParams(Route route);

    /**
     * @return Return client request host
     */
    String host();

    /**
     * @return Return request uri
     */
    String uri();

    /**
     * @return request url
     */
    String url();

    /**
     * @return return user-agent
     */
    default String userAgent() {
        return header(USER_AGENT);
    }

    /**
     * @return Return protocol
     */
    String protocol();

    /**
     * @return Return contextPath
     */
    default String contextPath() {
        return WebContext.contextPath();
    }

    /**
     * @return Return parameters on the path Map
     */
    Map<String, String> pathParams();

    /**
     * Get a URL parameter
     *
     * @param name Parameter name
     * @return Return parameter value
     */
    default String pathString(@NonNull String name) {
        return pathParams().get(name);
    }

    /**
     * Return a URL parameter for a Int type
     *
     * @param name Parameter name
     * @return Return Int parameter value
     */
    default Integer pathInt(@NonNull String name) {
        String val = pathString(name);
        return StringKit.isNotBlank(val) ? Integer.parseInt(val) : null;
    }

    /**
     * Return a URL parameter for a Long type
     *
     * @param name Parameter name
     * @return Return Long parameter value
     */
    default Long pathLong(@NonNull String name) {
        String val = pathString(name);
        return StringKit.isNotBlank(val) ? Long.parseLong(val) : null;
    }

    /**
     * @return Return query string
     */
    String queryString();

    /**
     * @return Return request query Map
     */
    Map<String, List<String>> parameters();

    /**
     * Get a request parameter
     *
     * @param name Parameter name
     * @return Return request parameter value
     */
    default Optional<String> query(@NonNull String name) {
        List<String> values = parameters().get(name);
        if (null != values && values.size() > 0)
            return Optional.of(values.get(0));
        return Optional.empty();
    }

    /**
     * Get a request parameter, if NULL is returned to defaultValue
     *
     * @param name         parameter name
     * @param defaultValue default String value
     * @return Return request parameter values
     */
    default String query(@NonNull String name, @NonNull String defaultValue) {
        Optional<String> value = query(name);
        if (value.isPresent())
            return value.get();
        return defaultValue;
    }

    /**
     * Returns a request parameter for a Int type
     *
     * @param name Parameter name
     * @return Return Int parameter values
     */
    default Optional<Integer> queryInt(@NonNull String name) {
        Optional<String> value = query(name);
        if (value.isPresent())
            return Optional.of(Integer.parseInt(value.get()));
        return Optional.empty();
    }

    /**
     * Returns a request parameter for a Int type
     *
     * @param name         Parameter name
     * @param defaultValue default int value
     * @return Return Int parameter values
     */
    default int queryInt(@NonNull String name, int defaultValue) {
        Optional<String> value = query(name);
        if (value.isPresent())
            return Integer.parseInt(value.get());
        return defaultValue;
    }

    /**
     * Returns a request parameter for a Long type
     *
     * @param name Parameter name
     * @return Return Long parameter values
     */
    default Optional<Long> queryLong(@NonNull String name) {
        Optional<String> value = query(name);
        if (value.isPresent())
            return Optional.of(Long.parseLong(value.get()));
        return Optional.empty();
    }

    /**
     * Returns a request parameter for a Long type
     *
     * @param name         Parameter name
     * @param defaultValue default long value
     * @return Return Long parameter values
     */
    default long queryLong(@NonNull String name, long defaultValue) {
        Optional<String> value = query(name);
        if (value.isPresent())
            return Long.parseLong(value.get());
        return defaultValue;
    }

    /**
     * Returns a request parameter for a Double type
     *
     * @param name Parameter name
     * @return Return Double parameter values
     */
    default Optional<Double> queryDouble(@NonNull String name) {
        Optional<String> value = query(name);
        if (value.isPresent())
            return Optional.of(Double.parseDouble(value.get()));
        return Optional.empty();
    }

    /**
     * Returns a request parameter for a Double type
     *
     * @param name         Parameter name
     * @param defaultValue default double value
     * @return Return Double parameter values
     */
    default double queryDouble(@NonNull String name, double defaultValue) {
        Optional<String> value = query(name);
        if (value.isPresent())
            return Double.parseDouble(value.get());
        return defaultValue;
    }

    /**
     * @return Return request method
     */
    String method();

    /**
     * @return Return HttpMethod
     */
    HttpMethod httpMethod();

    /**
     * @return Return server remote address
     */
    default String address() {
        return WebKit.ipAddr(this);
    }

    /**
     * @return Return current session
     */
    Session session();

    /**
     * @return Return contentType
     */
    default String contentType() {
        String contentType = header(CONTENT_TYPE);
        return null != contentType ? contentType : "Unknown";
    }

    /**
     * @return Return whether to use the SSL connection
     */
    boolean isSecure();

    /**
     * @return Return current request is a AJAX request
     */
    default boolean isAjax() {
        return "XMLHttpRequest".equals(header("x-requested-with"));
    }

    /**
     * @return return current request is IE browser
     */
    default boolean isIE() {
        String ua = userAgent();
        return ua.contains("MSIE") || ua.contains("TRIDENT");
    }

    Map<String, String> cookies();

    /**
     * Get String Cookie Value
     *
     * @param name cookie name
     * @return Return Cookie Value
     */
    default Optional<String> cookie(@NonNull String name) {
        String value = cookies().getOrDefault(name, "");
        if (value.length() > 0) {
            return Optional.of(value);
        }
        return Optional.empty();
    }

    Optional<Cookie> cookieRaw(String name);

    /**
     * Get String Cookie Value
     *
     * @param name         cookie name
     * @param defaultValue default cookie value
     * @return Return Cookie Value
     */
    default String cookie(@NonNull String name, @NonNull String defaultValue) {
        return cookie(name).isPresent() ? cookie(name).get() : defaultValue;
    }

    /**
     * Add a cookie to the request
     *
     * @param cookie
     * @return
     */
    Request cookie(Cookie cookie);

    /**
     * @return Return header information Map
     */
    Map<String, String> headers();

    /**
     * Get header information
     *
     * @param name Parameter name
     * @return Return header information
     */
    default String header(@NonNull String name) {
        return headers().getOrDefault(name, "");
    }

    /**
     * Get header information
     *
     * @param name         Parameter name
     * @param defaultValue default header value
     * @return Return header information
     */
    default String header(@NonNull String name, @NonNull String defaultValue) {
        String value = header(name);
        return value.length() > 0 ? value : defaultValue;
    }

    /**
     * @return return current request connection keepAlive
     */
    boolean keepAlive();

    /**
     * @return Return all Attribute in Request
     */
    Map<String, Object> attributes();

    /**
     * Setting Request Attribute
     *
     * @param name  Parameter name
     * @param value Parameter Value
     */
    default Request attribute(@NonNull String name, Object value) {
        if (null != value) attributes().put(name, value);
        return this;
    }

    /**
     * Get a Request Attribute
     *
     * @param name Parameter name
     * @return Return parameter value
     */
    default <T> T attribute(String name) {
        if (null == name) return null;
        Object object = attributes().get(name);
        return null != object ? (T) object : null;
    }

    /**
     * @return return request file items
     */
    Map<String, FileItem> fileItems();

    /**
     * get file item by request part name
     *
     * @param name
     * @return
     */
    default Optional<FileItem> fileItem(@NonNull String name) {
        return Optional.ofNullable(fileItems().get(name));
    }

    /**
     * @return Return request body
     */
    ByteBuf body();

    /**
     * @return return request body to string
     */
    String bodyToString();

}
