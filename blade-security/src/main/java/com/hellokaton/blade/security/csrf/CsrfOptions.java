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
 * Csrf config
 * <p>
 * Created by hellokaton on 11/07/2017.
 */
@Getter
@Setter
public class CsrfOptions {

    private static final Set<HttpMethod> DEFAULT_VERIFY_METHODS = new HashSet<>(
            Arrays.asList(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
    );

    private boolean enabled = true;
    private int tokenExpiredSeconds = 600;
    private String attrKeyName = "_csrf_token";
    private String headerKeyName = "X-CSRF-TOKEN";
    private String formKeyName = "_csrf_token";
    private String secret = "UXOwbPd+P0u8YyBkQbuyXiv7UVc1JmMS061HUuaDRms=";

    private Set<String> urlExclusions = new HashSet<>();
    private Set<HttpMethod> verifyMethods = DEFAULT_VERIFY_METHODS;

    private Function<RouteContext, Boolean> errorHandler;

    public CsrfOptions exclusion(@NonNull String... urls) {
        this.urlExclusions.addAll(Arrays.asList(urls));
        return this;
    }

    public boolean isExclusion(@NonNull String url) {
        for (String excludeURL : this.urlExclusions) {
            if (url.equals(excludeURL)) {
                return true;
            }
        }
        return false;
    }

}
