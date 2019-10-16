package com.blade.mvc.http;

import com.blade.kit.JsonKit;
import com.blade.kit.StringKit;
import com.blade.kit.WebKit;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.RouteActionArguments;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.route.Route;
import com.blade.server.netty.HttpConst;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.blade.kit.WebKit.UNKNOWN_MAGIC;

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
     * Get client host.
     *
     * @return Return client request host
     */
    String host();

    /**
     * Get client remote address. e.g: 102.331.234.11:38227
     *
     * @return Return client ip and port
     */
    String remoteAddress();

    /**
     * Get request uri
     *
     * @return Return request uri
     */
    String uri();

    /**
     * Get request url
     *
     * @return request url
     */
    String url();

    /**
     * Get request user-agent
     *
     * @return return user-agent
     */
    default String userAgent() {
        return header(HttpConst.USER_AGENT);
    }

    /**
     * Get request http protocol
     *
     * @return Return protocol
     */
    String protocol();

    /**
     * Get current application contextPath, default is "/"
     *
     * @return Return contextPath
     */
    default String contextPath() {
        return WebContext.contextPath();
    }

    /**
     * Get current request Path params, like /users/:uid
     *
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
        return StringKit.isNotEmpty(val) ? Integer.parseInt(val) : null;
    }

    /**
     * Return a URL parameter for a Long type
     *
     * @param name Parameter name
     * @return Return Long parameter value
     */
    default Long pathLong(@NonNull String name) {
        String val = pathString(name);
        return StringKit.isNotEmpty(val) ? Long.parseLong(val) : null;
    }

    /**
     * Get queryString. e.g: http://xxx.com/hello?name=a&age=23
     *
     * @return Return query string
     */
    String queryString();

    /**
     * Get current request query parameters
     *
     * @return Return request query Map
     */
    Map<String, List<String>> parameters();

    /**
     * Get current request query parameter names
     *
     * @return Return request query names
     * @since 2.0.8-RELEASE
     */
    Set<String> parameterNames();

    /**
     * Get current request query parameter values
     *
     * @param paramName param name
     * @return Return request query values
     * @since 2.0.8-RELEASE
     */
    List<String> parameterValues(String paramName);

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
        return value.orElse(defaultValue);
    }

    /**
     * Returns a request parameter for a Int type
     *
     * @param name Parameter name
     * @return Return Int parameter values
     */
    default Optional<Integer> queryInt(@NonNull String name) {
        Optional<String> value = query(name);
        return value.map(Integer::parseInt);
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
        return value.map(Integer::parseInt).orElse(defaultValue);
    }

    /**
     * Returns a request parameter for a Long type
     *
     * @param name Parameter name
     * @return Return Long parameter values
     */
    default Optional<Long> queryLong(@NonNull String name) {
        Optional<String> value = query(name);
        return value.map(Long::parseLong);
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
        return value.map(Long::parseLong).orElse(defaultValue);
    }

    /**
     * Returns a request parameter for a Double type
     *
     * @param name Parameter name
     * @return Return Double parameter values
     */
    default Optional<Double> queryDouble(@NonNull String name) {
        Optional<String> value = query(name);
        return value.map(Double::parseDouble);
    }

    /**
     * Returns a request parameter for a Double type
     *
     * @param name         Parameter name
     * @param defaultValue default double value
     * @return Return Double parameter values
     */
    default double queryDouble(@NonNull String name, Double defaultValue) {
        Optional<String> value = query(name);
        return value.map(Double::parseDouble).orElse(defaultValue);
    }

    default Optional<Boolean> queryBoolean(@NonNull String name) {
        Optional<String> value = query(name);
        return value.map(Boolean::valueOf);
    }

    default boolean queryBoolean(@NonNull String name, Boolean defaultValue) {
        Optional<String> value = query(name);
        return value.map(Boolean::valueOf).orElse(defaultValue);
    }

    /**
     * Get current request http method. e.g: GET
     *
     * @return Return request method
     */
    String method();

    /**
     * Get current request HttpMethod. e.g: HttpMethod.GET
     *
     * @return Return HttpMethod
     */
    HttpMethod httpMethod();

    /**
     * @return whether the current request is a compressed request of GZIP
     * @since 2.0.9.BETA1
     */
    boolean useGZIP();

    /**
     * Get client ip address
     *
     * @return Return server remote address
     */
    default String address() {
        String address = WebKit.ipAddress(this);
        if (StringKit.isBlank(address) || UNKNOWN_MAGIC.equalsIgnoreCase(address)) {
            address = remoteAddress().split(":")[0];
        }
        if (StringKit.isBlank(address)) {
            address = "Unknown";
        }
        return address;
    }

    /**
     * Get current request session, if null then create
     *
     * @return Return current session
     */
    Session session();

    /**
     * Get current request contentType. e.g: "text/html; charset=utf-8"
     *
     * @return Return contentType
     */
    default String contentType() {
        String contentType = header(HttpConst.CONTENT_TYPE_STRING);
        return null != contentType ? contentType : "Unknown";
    }

    /**
     * Get current request is https.
     *
     * @return Return whether to use the SSL connection
     */
    boolean isSecure();

    /**
     * Get current request is ajax. According to the header "x-requested-with"
     *
     * @return Return current request is a AJAX request
     */
    default boolean isAjax() {
        return "XMLHttpRequest".equals(header("X-Requested-With")) || "XMLHttpRequest".equals(header("x-requested-with"));
    }

    /**
     * Determine if this request is a FORM form request
     * <p>
     * According to header content-type contains "form"
     *
     * @return is form request
     */
    default boolean isFormRequest() {
        return this.header(HttpConst.CONTENT_TYPE_STRING).toLowerCase().contains("form");
    }

    /**
     * Determine if this request is a json request
     * <p>
     * According to header content-type contains "json"
     *
     * @return is json request
     */
    default boolean isJsonRequest() {
        return this.header(HttpConst.CONTENT_TYPE_STRING).toLowerCase().contains("json");
    }

    /**
     * Gets the current request is the head of the IE browser
     *
     * @return return current request is IE browser
     */
    default boolean isIE() {
        String ua = userAgent();
        return ua.contains("MSIE") || ua.contains("TRIDENT");
    }

    /**
     * Get current request cookies
     *
     * @return return cookies
     */
    Map<String, Cookie> cookies();

    /**
     * Get String Cookie Value
     *
     * @param name cookie name
     * @return Return Cookie Value
     */
    default String cookie(@NonNull String name) {
        Cookie cookie = cookies().get(name);
        return null != cookie ? cookie.value() : null;
    }

    /**
     * Get raw cookie by cookie name
     *
     * @param name cookie name
     * @return return Optional<Cookie>
     */
    Cookie cookieRaw(String name);

    /**
     * Get String Cookie Value
     *
     * @param name         cookie name
     * @param defaultValue default cookie value
     * @return Return Cookie Value
     */
    default String cookie(@NonNull String name, @NonNull String defaultValue) {
        String cookie = cookie(name);
        return null != cookie ? cookie : defaultValue;
    }

    /**
     * Add a cookie to the request
     *
     * @param cookie cookie raw
     * @return return Request instance
     */
    Request cookie(Cookie cookie);

    /**
     * Get current request headers.
     *
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
        return headers().getOrDefault(name,"");
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
     * Get current request is KeepAlive, HTTP1.1 is true.
     *
     * @return return current request connection keepAlive
     */
    boolean keepAlive();

    /**
     * Bind form parameter to model
     *
     * @param modelClass model class type
     * @param <T>
     */
    default <T> T bindWithForm(Class<T> modelClass) {
        return RouteActionArguments.parseModel(modelClass, this, null);
    }

    /**
     * Bind body parameter to model
     *
     * @param modelClass model class type
     * @param <T>
     */
    default <T> T bindWithBody(Class<T> modelClass) {
        String json = this.bodyToString();
        return StringKit.isNotEmpty(json) ? JsonKit.formJson(json, modelClass) : null;
    }

    /**
     * Get current request attributes
     *
     * @return Return all Attribute in Request
     */
    Map<String, Object> attributes();

    /**
     * Setting Request Attribute
     *
     * @param name  attribute name
     * @param value attribute Value
     * @return set attribute value and return current request instance
     */
    default Request attribute(@NonNull String name, Object value) {
        this.attributes().put(name, value);
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
     * Get current request all fileItems
     *
     * @return return request file items
     */
    Map<String, FileItem> fileItems();

    /**
     * get file item by request part name
     *
     * @param name
     * @return return Optional<FileItem>
     */
    default Optional<FileItem> fileItem(@NonNull String name) {
        return Optional.ofNullable(fileItems().get(name));
    }

    /**
     * @return return whether Chunk content has been read
     * @since 2.0.11
     */
    boolean chunkIsEnd();

    boolean isMultipart();

    /**
     * Get current request body as ByteBuf
     *
     * @return Return request body
     */
    ByteBuf body();

    /**
     * Get current request body as string
     *
     * @return return request body to string
     */
    default String bodyToString() {
        return this.body().toString(CharsetUtil.UTF_8);
    }

}
