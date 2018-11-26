package com.blade.security.web.cors;

import com.blade.mvc.RouteContext;
import com.blade.mvc.hook.WebHook;
import java.util.StringJoiner;
import java.util.stream.Collector;
import lombok.extern.slf4j.Slf4j;

/**
 * CorsMiddleware
 *
 * @author biezhi
 * @date 2018/7/11
 */
@Slf4j
public class CorsMiddleware implements WebHook {

    private CorsConfiger corsConfig;

    public CorsMiddleware() {
    }

    public CorsMiddleware(CorsConfiger corsConfiger) {
        this.corsConfig = corsConfiger;
    }

    @Override
    public boolean before(RouteContext context) {
        this.allowCredentials(context)
            .allowMethods(context)
            .allowHeads(context)
            .setMaxAge(context)
            .allowCredentials(context);
        if ("OPTIONS".equals(context.method())) {
            context.status(202);
        }
        return true;
    }

    private CorsMiddleware allowHeads(RouteContext context) {
        boolean isDefaultAllowHeads = corsConfig == null || corsConfig.getAllowedHeaders() == null
            || corsConfig.getAllowedHeaders().size() == 0;

        if (isDefaultAllowHeads) {
            context.response().header("Access-Control-Allow-Headers", CorsConfiger.ALL);
            return this;
        }

        String heads = corsConfig.getAllowedHeaders().stream().collect(Collector.of(
            () -> new StringJoiner(","),
            (j, head) -> j.add(head),
            StringJoiner::merge,
            StringJoiner::toString
        ));
        context.response().header("Access-Control-Allow-Headers", heads);
        return this;
    }

    private CorsMiddleware allowMethods(RouteContext context) {
        boolean isDefaultAllowMethods = corsConfig == null || corsConfig.getAllowedMethods() == null
            || corsConfig.getAllowedMethods().size() == 0;

        if (isDefaultAllowMethods) {
            context.header("Access-Control-Allow-Methods",
                CorsConfiger.DEFAULT_ALLOWED_METHODS);
            return this;
        }

        String methods = corsConfig.getAllowedMethods().stream().collect(Collector.of(
            () -> new StringJoiner(", "),
            (j, method) -> j.add(method.toUpperCase()),
            StringJoiner::merge,
            StringJoiner::toString
        ));

        context.response().header("Access-Control-Allow-Methods", methods);
        return this;
    }

    private CorsMiddleware allowCredentials(RouteContext context) {
        boolean isDefaultAllowCredentials = corsConfig == null || corsConfig.getAllowCredentials() == null;

        if (isDefaultAllowCredentials) {
            context.header("Access-Control-Allow-Credentials",
                CorsConfiger.DEFAULT_ALLOW_CREDENTIALS);
            return this;
        }
        context.response().header("Access-Control-Allow-Credentials",
            corsConfig.getAllowCredentials().toString());
        return this;
    }

    private CorsMiddleware setMaxAge(RouteContext context) {
        boolean isDefaultMaxAge = corsConfig == null || corsConfig.getMaxAge() == null;
        if (isDefaultMaxAge) {
            context.response().header("Access-Control-Max-Age",
                CorsConfiger.DEFAULT_MAX_AGE.toString());
            return this;
        }
        context.header("Access-Control-Max-Age", corsConfig.getMaxAge().toString());
        return this;
    }

}
