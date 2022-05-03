package com.blade.options;

import com.blade.mvc.http.HttpMethod;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public class CorsOptions {

    private Set<String> origins;
    private boolean disable;
    private Set<String> exposeHeaders;
    private boolean allowCredentials;
    private long maxAge;
    private Set<HttpMethod> allowedMethods;
    private Set<String> allowedHeaders;
    private boolean allowNullOrigin;
    private boolean anyOrigin;

    /**
     * Creates a new Builder instance allowing any origin, "*" which is the
     * wildcard origin.
     */
    CorsOptions() {
        this.anyOrigin = true;
        this.origins = Collections.emptySet();
    }

    /**
     * Creates a Builder instance with it's origin set to '*'.
     *
     * @return Builder to support method chaining.
     */
    public static CorsOptions forAnyOrigin() {
        return new CorsOptions();
    }

    public static CorsOptions create(final String... origins) {
        if (null != origins && origins.length > 0) {
            CorsOptions options = new CorsOptions();
            options.origins = new LinkedHashSet<>(Arrays.asList(origins));
            return options;
        } else {
            return forAnyOrigin();
        }
    }

    public CorsOptions allowedOrigins(final String... origins) {
        if (null != this.origins) {
            this.origins.addAll(new LinkedHashSet<>(Arrays.asList(origins)));
        } else {
            this.origins = new LinkedHashSet<>(Arrays.asList(origins));
        }
        return this;
    }

    /**
     * Web browsers may set the 'Origin' request header to 'null' if a resource is loaded
     * from the local file system. Calling this method will enable a successful CORS response
     * with a {@code "null"} value for the CORS response header 'Access-Control-Allow-Origin'.
     *
     * @return {@link CorsOptions} to support method chaining.
     */
    public CorsOptions allowNullOrigin() {
        this.allowNullOrigin = true;
        return this;
    }

    /**
     * Disables CORS support.
     *
     * @return {@link CorsOptions} to support method chaining.
     */
    public CorsOptions disable() {
        this.disable = true;
        return this;
    }

    /**
     * Specifies the headers to be exposed to calling clients.
     * <p>
     * During a simple CORS request, only certain response headers are made available by the
     * browser, for example using:
     * <pre>
     * xhr.getResponseHeader("Content-Type");
     * </pre>
     * <p>
     * The headers that are available by default are:
     * <ul>
     * <li>Cache-Control</li>
     * <li>Content-Language</li>
     * <li>Content-Type</li>
     * <li>Expires</li>
     * <li>Last-Modified</li>
     * <li>Pragma</li>
     * </ul>
     * <p>
     * To expose other headers they need to be specified which is what this method enables by
     * adding the headers to the CORS 'Access-Control-Expose-Headers' response header.
     *
     * @param headers the values to be added to the 'Access-Control-Expose-Headers' response header
     * @return {@link CorsOptions} to support method chaining.
     */
    public CorsOptions exposeHeaders(final String... headers) {
        if (null != this.exposeHeaders) {
            this.exposeHeaders.addAll(Arrays.asList(headers));
        } else {
            this.exposeHeaders = new LinkedHashSet<>(Arrays.asList(headers));
        }
        return this;
    }

    /**
     * By default cookies are not included in CORS requests, but this method will enable cookies to
     * be added to CORS requests. Calling this method will set the CORS 'Access-Control-Allow-Credentials'
     * response header to true.
     * <p>
     * Please note, that cookie support needs to be enabled on the client side as well.
     * The client needs to opt-in to send cookies by calling:
     * <pre>
     * xhr.withCredentials = true;
     * </pre>
     * The default value for 'withCredentials' is false in which case no cookies are sent.
     * Setting this to true will included cookies in cross origin requests.
     *
     * @return {@link CorsOptions} to support method chaining.
     */
    public CorsOptions allowCredentials() {
        allowCredentials = true;
        return this;
    }

    /**
     * When making a preflight request the client has to perform two request with can be inefficient.
     * This setting will set the CORS 'Access-Control-Max-Age' response header and enables the
     * caching of the preflight response for the specified time. During this time no preflight
     * request will be made.
     *
     * @param max the maximum time, in seconds, that the preflight response may be cached.
     * @return {@link CorsOptions} to support method chaining.
     */
    public CorsOptions maxAge(final long max) {
        maxAge = max;
        return this;
    }

    /**
     * Specifies the allowed set of HTTP Request Methods that should be returned in the
     * CORS 'Access-Control-Request-Method' response header.
     *
     * @param methods the {@link HttpMethod}s that should be allowed.
     * @return {@link CorsOptions} to support method chaining.
     */
    public CorsOptions allowedMethods(final HttpMethod... methods) {
        if (null != this.allowedMethods) {
            this.allowedMethods.addAll(Arrays.asList(methods));
        } else {
            this.allowedMethods = new LinkedHashSet<>(Arrays.asList(methods));
        }
        return this;
    }

    /**
     * Specifies the if headers that should be returned in the CORS 'Access-Control-Allow-Headers'
     * response header.
     * <p>
     * If a client specifies headers on the request, for example by calling:
     * <pre>
     * xhr.setRequestHeader('My-Custom-Header', "SomeValue");
     * </pre>
     * the server will receive the above header name in the 'Access-Control-Request-Headers' of the
     * preflight request. The server will then decide if it allows this header to be sent for the
     * real request (remember that a preflight is not the real request but a request asking the server
     * if it allow a request).
     *
     * @param headers the headers to be added to the preflight 'Access-Control-Allow-Headers' response header.
     * @return {@link CorsConfigBuilder} to support method chaining.
     */
    public CorsOptions allowedHeaders(final String... headers) {
        if (null != this.allowedHeaders) {
            this.allowedHeaders.addAll(Arrays.asList(headers));
        } else {
            this.allowedHeaders = new LinkedHashSet<>(Arrays.asList(headers));
        }
        return this;
    }

}
