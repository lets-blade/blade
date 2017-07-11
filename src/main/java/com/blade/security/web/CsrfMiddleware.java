package com.blade.security.web;

import com.blade.kit.StringKit;
import com.blade.kit.UUID;
import com.blade.mvc.WebContext;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.RouteHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Csrf middleware
 *
 * @author biezhi
 *         2017/6/5
 */
@Slf4j
public class CsrfMiddleware implements WebHook {

    private final static Set<String> tokens = new HashSet<>();

    public static final String CSRF_TOKEN = "_csrf.token";
    public static final String CSRF_PARAM_NAME = "_csrf.param";
    public static final String CSRF_HEADER_NAME = "_csrf.header";

    private static String TOKEN_KEY = "csrf_token";

    public CsrfMiddleware() {
    }

    public CsrfMiddleware(String token_key) {
        TOKEN_KEY = token_key;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ValidToken {
        Class<? extends RouteHandler> value() default RouteHandler.class;
    }

    @Override
    public boolean before(Signature signature) {
        Request request = signature.request();
        if ("GET".equals(request.method())) {
            request.attribute(CSRF_PARAM_NAME, TOKEN_KEY);
            request.attribute(CSRF_HEADER_NAME, TOKEN_KEY);
            String token = UUID.UU64();
            request.attribute(CSRF_TOKEN, token);
            log.debug("gen token [{}]", token);
            tokens.add(token);
        } else {
            Route route = request.route();
            Method method = route.getAction();
            ValidToken validToken = method.getAnnotation(ValidToken.class);
            if (null != validToken) {
                return validation();
            }
        }
        return true;
    }

    public static boolean validation() {
        Request request = WebContext.request();
        Response response = WebContext.response();
        Optional<String> tokenOptional = request.query(TOKEN_KEY);
        String token = null;
        if (tokenOptional.isPresent()) {
            token = tokenOptional.get();
        } else {
            token = request.header(TOKEN_KEY);
        }
        if (StringKit.isBlank(token) || !tokens.contains(token)) {
            // 不存在token
            response.badRequest().text("Bad Request.");
        } else {
            tokens.remove(token);
            return true;
        }
        return false;
    }

}