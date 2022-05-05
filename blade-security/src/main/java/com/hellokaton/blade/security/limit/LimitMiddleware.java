package com.hellokaton.blade.security.limit;

import com.hellokaton.blade.mvc.RouteContext;
import com.hellokaton.blade.mvc.hook.WebHook;
import com.hellokaton.blade.mvc.http.Request;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * LimitMiddleware Middleware
 * <p>
 * Created by hellokaton on 2022/5/5.
 */
@Slf4j
public class LimitMiddleware implements WebHook {

    private final LimitOptions limitOptions;

    public LimitMiddleware(LimitOptions limitOptions) {
        this.limitOptions = limitOptions;
        this.initOptions(this.limitOptions);
    }

    private void initOptions(LimitOptions limitOptions) {
        if (null == limitOptions.getHandler()) {
            limitOptions.setHandler(ctx -> {
                ctx.response().status(573);
                throw new LimitException("request too fast.");
            });
        }
        if (null == limitOptions.getKeyFunc()) {
            limitOptions.setKeyFunc(Request::remoteAddress);
        }
        if (null == limitOptions.getMode()) {
            limitOptions.setMode(LimitMode.BURST);
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
        if (null == limit) {
            // global limit

            // limit is triggered
            limitOptions.getHandler().accept(ctx);
            return false;
        } else {
            if (limit.disable()) {
                return true;
            }
            // specific limit

        }
        return true;
    }

}
