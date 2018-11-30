package com.blade.security.web.cors;

import com.blade.mvc.RouteContext;
import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.http.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.StringJoiner;
import java.util.stream.Collector;

/**
 * CorsMiddleware
 * <p>
 * This is a simple CORS policy,
 * you can also implement the {@link CorsMiddleware#handle} method of the class to perform custom filtering.
 *
 * @author biezhi
 * @date 2018/7/11
 */
@Slf4j
public class CorsMiddleware implements RouteHandler  {

    private CorsConfiger corsConfig;

    public CorsMiddleware() {
    }

    public CorsMiddleware(CorsConfiger corsConfiger) {
        this.corsConfig = corsConfiger;
    }

    @Override
    public void handle(RouteContext context) {
        context.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        context.header("Access-Control-Allow-Origin", "*");
        context.header("Access-Control-Allow-Headers", CorsConfiger.ALL);
        context.status(204);
    }

    private CorsMiddleware allowHeads(Response response) {
        boolean isDefaultAllowHeads = corsConfig == null || corsConfig.getAllowedHeaders() == null
                || corsConfig.getAllowedHeaders().size() == 0;

        if (isDefaultAllowHeads) {
            response.header("Access-Control-Allow-Headers", CorsConfiger.ALL);
            return this;
        }

        String heads = corsConfig.getAllowedHeaders().stream()
                .collect(Collector.of(
                        () -> new StringJoiner(","),
                        StringJoiner::add,
                        StringJoiner::merge,
                        StringJoiner::toString
                ));

        response.header("Access-Control-Allow-Headers", heads);
        return this;
    }

    private CorsMiddleware allowMethods(Response response) {
        boolean isDefaultAllowMethods = corsConfig == null || corsConfig.getAllowedMethods() == null
                || corsConfig.getAllowedMethods().size() == 0;

        if (isDefaultAllowMethods) {
            response.header("Access-Control-Allow-Methods",
                    CorsConfiger.DEFAULT_ALLOWED_METHODS);
            return this;
        }

        String methods = corsConfig.getAllowedMethods().stream().collect(Collector.of(
                () -> new StringJoiner(", "),
                (j, method) -> j.add(method.toUpperCase()),
                StringJoiner::merge,
                StringJoiner::toString
        ));

        response.header("Access-Control-Allow-Methods", methods);
        return this;
    }

    private CorsMiddleware allowCredentials(Response response) {
        boolean isDefaultAllowCredentials = corsConfig == null || corsConfig.getAllowCredentials() == null;

        if (isDefaultAllowCredentials) {
            response.header("Access-Control-Allow-Credentials",
                    CorsConfiger.DEFAULT_ALLOW_CREDENTIALS);
            return this;
        }
        response.header("Access-Control-Allow-Credentials",
                corsConfig.getAllowCredentials().toString());
        return this;
    }

    private CorsMiddleware setMaxAge(Response response) {
        boolean isDefaultMaxAge = corsConfig == null || corsConfig.getMaxAge() == null;
        if (isDefaultMaxAge) {
            response.header("Access-Control-Max-Age",
                    CorsConfiger.DEFAULT_MAX_AGE.toString());
            return this;
        }
        response.header("Access-Control-Max-Age", corsConfig.getMaxAge().toString());
        return this;
    }

}
