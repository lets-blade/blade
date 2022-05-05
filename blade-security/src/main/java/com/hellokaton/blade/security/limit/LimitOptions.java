package com.hellokaton.blade.security.limit;

import com.hellokaton.blade.mvc.RouteContext;
import com.hellokaton.blade.mvc.http.Request;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@Setter
public class LimitOptions {

    private boolean enabled = true;
    private Function<Request, String> keyFunc;
    private Consumer<RouteContext> handler;
    private LimitMode mode;
    private Set<String> excludeURLs;

}
