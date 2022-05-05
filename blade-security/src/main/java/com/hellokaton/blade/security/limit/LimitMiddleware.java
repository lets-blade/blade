package com.hellokaton.blade.security.limit;

import com.google.common.util.concurrent.RateLimiter;
import com.hellokaton.blade.exception.InternalErrorException;
import com.hellokaton.blade.kit.EncryptKit;
import com.hellokaton.blade.mvc.RouteContext;
import com.hellokaton.blade.mvc.hook.WebHook;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * LimitMiddleware Middleware
 * <p>
 * Created by hellokaton on 2022/5/5.
 */
@Slf4j
public class LimitMiddleware implements WebHook {

    private final LimitOptions limitOptions;
    @SuppressWarnings("UnstableApiUsage")
    private final Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    public LimitMiddleware() {
        this(LimitOptions.create());
    }

    public LimitMiddleware(LimitOptions limitOptions) {
        this.limitOptions = limitOptions;
        this.initOptions(this.limitOptions);
    }

    private void initOptions(LimitOptions limitOptions) {
        if (null == limitOptions.getLimitHandler()) {
            limitOptions.setLimitHandler(ctx -> {
                throw new LimitException("Too Many Request :(");
            });
        }
        if (null == limitOptions.getKeyFunc()) {
            limitOptions.setKeyFunc(req -> EncryptKit.md5(req.remoteAddress() + req.uri() + req.method()));
        }
    }

    @Override
    public boolean before(RouteContext ctx) {
        if (!limitOptions.isEnabled()) {
            return true;
        }
        Method action = ctx.routeAction();
        Class<?> controller = action.getDeclaringClass();
        Limit limit = action.getAnnotation(Limit.class);
        if (null == limit) {
            limit = controller.getAnnotation(Limit.class);
        }
        String key = limitOptions.getKeyFunc().apply(ctx.request());
        if (null == limit) {
            // global limit
            if (!rateLimiterMap.containsKey(key)) {
                rateLimiterMap.put(key, createLimiter(limitOptions.getExpression()));
            }
            // limit is triggered
            if (!rateLimiterMap.get(key).tryAcquire()) {
                return limitOptions.getLimitHandler().apply(ctx);
            }
        } else {
            if (limit.disable()) {
                return true;
            }
            // specific limit
            if (!rateLimiterMap.containsKey(key)) {
                rateLimiterMap.put(key, createLimiter(limit.value()));
            }
            // limit is triggered
            if (!rateLimiterMap.get(key).tryAcquire()) {
                return limitOptions.getLimitHandler().apply(ctx);
            }
        }
        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    protected RateLimiter createLimiter(String expression) {
        LimitExpression.Limiter limiter = LimitExpression.match(expression);
        if (null == limiter) {
            throw new InternalErrorException("invalid limit mode :(");
        }
        if (limiter.warmupPeriod > 0) {
            return RateLimiter.create(limiter.permitsPerSecond, limiter.warmupPeriod, TimeUnit.SECONDS);
        }
        return RateLimiter.create(limiter.permitsPerSecond);
    }

}
