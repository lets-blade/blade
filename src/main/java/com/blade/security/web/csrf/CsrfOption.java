package com.blade.security.web.csrf;

import com.blade.kit.StringKit;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import lombok.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Csrf config
 * <p>
 * Created by biezhi on 11/07/2017.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsrfOption {

    static final Set<String> DEFAULT_IGNORE_METHODS = new HashSet<>(Arrays.asList("GET", "HEAD", "OPTIONS", "PUT", "DELETE"));

    static final Consumer<Response> DEFAULT_ERROR_HANDLER = response -> response.badRequest().text("CSRF token mismatch.");

    static final Function<Request, String> DEFAULT_TOKEN_GETTER = request -> request.query("_token").orElseGet(() -> {
        if (StringKit.isNotBlank(request.header("X-CSRF-TOKEN"))) {
            return request.header("X-CSRF-TOKEN");
        }
        if (StringKit.isNotBlank(request.header("X-XSRF-TOKEN"))) {
            return request.header("X-XSRF-TOKEN");
        }
        return "";
    });

    @Builder.Default
    private Set<String>               excludeURLs      = new HashSet<>();
    @Builder.Default
    private Set<String>               excludeStartURLs = new HashSet<>();
    @Builder.Default
    private Set<String>               ignoreMethods    = DEFAULT_IGNORE_METHODS;
    @Builder.Default
    private Consumer<Response>        errorHandler     = DEFAULT_ERROR_HANDLER;
    @Builder.Default
    private Function<Request, String> tokenGetter      = DEFAULT_TOKEN_GETTER;

    public boolean isIgnoreMethod(String method) {
        return ignoreMethods.contains(method);
    }

    public CsrfOption excludeStartWith(@NonNull String... urls) {
        this.excludeStartURLs.addAll(Arrays.asList(urls));
        return this;
    }

    public CsrfOption excludeWith(@NonNull String... urls) {
        this.excludeURLs.addAll(Arrays.asList(urls));
        return this;
    }

    public boolean isExcludeStartWith(@NonNull String url) {
        for (String excludeURL : excludeStartURLs) {
            if (url.startsWith(excludeURL)) {
                return true;
            }
        }
        return false;
    }

    public boolean isExcludeWith(@NonNull String url) {
        for (String excludeURL : excludeURLs) {
            if (url.equals(excludeURL)) {
                return true;
            }
        }
        return false;
    }

}
