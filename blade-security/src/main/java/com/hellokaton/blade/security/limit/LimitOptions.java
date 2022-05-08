package com.hellokaton.blade.security.limit;

import com.hellokaton.blade.kit.PathKit;
import com.hellokaton.blade.mvc.RouteContext;
import com.hellokaton.blade.mvc.http.Request;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.function.Function;

@Getter
@Setter
public class LimitOptions {

    /**
     * Enable request frequency limit, default is enabled by default.
     * <p>
     * after this function is disabled, the middleware does not take effect.
     */
    private boolean enabled = true;

    /**
     * To determine the uniqueness of a Request, pass in a Request object.
     * <p>
     * The default is the md5(remote_host+request_uri+request_method)
     */
    private Function<Request, String> keyFunc;

    /**
     * The processor that triggers the request frequency limit will, by default, prompt you for too many requests
     */
    private Function<RouteContext, Boolean> limitHandler;

    /**
     * Use expressions to control request frequency.
     * <p>
     * for example:
     * <p>
     * 5/s         allow 5 requests per second
     * 5/1s        allow 5 requests per second
     * 5/1m        allow 60 requests per minute
     * 5/3s/warmup allow 5 requests in 3 seconds.
     * after startup, there is a warm-up period to gradually increase the distribution frequency to the configured rate.
     */
    private String expression = "5/s";

    /**
     * A list of urls to exclude, which will not be limited by the frequency of requests.
     * <p>
     * for example:
     * <p>
     * /notify/**
     * /upload/**
     * /admin/roles/**
     */
    private PathKit.TrieRouter router;

    public static LimitOptions create() {
        return new LimitOptions();
    }

    public LimitOptions exclusion(@NonNull String... urls) {
        if (null == this.router) {
            this.router = PathKit.createRoute();
        }
        for (String url : urls) {
            this.router.addRoute(url);
        }
        return this;
    }

    public boolean isExclusion(@NonNull String url) {
        if (null == this.router) {
            return false;
        }
        return router.match(url);
    }

}
