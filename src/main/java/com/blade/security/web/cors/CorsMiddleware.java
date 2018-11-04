package com.blade.security.web.cors;

import com.blade.mvc.RouteContext;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.Request;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.internal.StringUtil;
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
            .allowOrigin(context)
            .setMaxAge(context)
            .allowCredentials(context);
        if ("OPTIONS".equals(context.method())) {
            context.status(202);
        }
        return true;
    }

    private CorsMiddleware allowOrigin(RouteContext context) {
        Request request = context.request();
        String originUrl = request.header(HttpHeaderNames.ORIGIN.toString());
        if (StringUtil.isNullOrEmpty(originUrl)) {
            originUrl = CorsConfiger.ALL;
        }
        context.header("Access-Control-Allow-Headers", originUrl);
        return this;
    }

    private CorsMiddleware allowMethods(RouteContext context) {
        if (corsConfig == null || corsConfig.getAllowedMethods() == null
            || corsConfig.getAllowedMethods().size() == 0) {

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
        if (corsConfig == null || corsConfig.getAllowCredentials() == null) {
            context.header("Access-Control-Allow-Credentials",
                CorsConfiger.DEFAULT_ALLOW_CREDENTIALS);
            return this;
        }
        context.response().header("Access-Control-Allow-Credentials",
            corsConfig.getAllowCredentials().toString());
        return this;
    }

    private CorsMiddleware setMaxAge(RouteContext context) {

        if (corsConfig == null || corsConfig.getMaxAge() == null) {
            context.response().header("Access-Control-Max-Age",
                CorsConfiger.DEFAULT_MAX_AGE.toString());
            return this;
        }
        context.header(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE.toString(), corsConfig.getMaxAge().toString());
        return this;
    }

}
