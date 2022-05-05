package com.hellokaton.blade.security.csrf;

import com.hellokaton.blade.mvc.RouteContext;
import com.hellokaton.blade.mvc.http.HttpMethod;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Csrf Options
 * <p>
 * Created by hellokaton on 2022/5/5
 */
@Getter
@Setter
public class CsrfOptions {

    private static final Set<HttpMethod> DEFAULT_VERIFY_METHODS = new HashSet<>(
            Arrays.asList(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
    );

    /**
     * Enable csrf, default is enabled by default.
     * <p>
     * after this function is disabled, the middleware does not take effect.
     */
    private boolean enabled = true;

    /**
     * The attribute name that puts csrf_token into the request context.
     * <p>
     * you can get this value from the template engine.
     */
    private String attrKeyName = "_csrf_token";

    /**
     * The header name that carries the token in the request header.
     */
    private String headerKeyName = "X-CSRF-TOKEN";

    /**
     * The form input name that carries the token in the request.
     */
    private String formKeyName = "_csrf_token";

    /**
     * To generate a token key, change the value.
     * <p>
     * the token is generated in JWT mode.
     */
    private String secret = "UXOwbPd+P0u8YyBkQbuyXiv7UVc1JmMS061HUuaDRms=";

    /**
     * A list of urls to exclude, which will not be limited by the frequency of requests.
     * <p>
     * for example:
     * <p>
     * /notify/**
     * /upload/**
     * /admin/roles/**
     */
    private Set<String> excludeURLs;

    /**
     * For the following set of request methods, tokens will need to be validated.
     */
    private Set<HttpMethod> verifyMethods = DEFAULT_VERIFY_METHODS;

    /**
     * The processor that triggers the request frequency limit will, by default, prompt you for CSRF token mismatch.
     */
    private Function<RouteContext, Boolean> errorHandler;

    public static CsrfOptions create() {
        return new CsrfOptions();
    }

    public CsrfOptions exclusion(@NonNull String... urls) {
        this.excludeURLs.addAll(Arrays.asList(urls));
        return this;
    }

    public boolean isExclusion(@NonNull String url) {
        for (String excludeURL : this.excludeURLs) {
            if (url.equals(excludeURL)) {
                return true;
            }
        }
        return false;
    }

}
