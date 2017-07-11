package com.blade.security.web.csrf;

import com.blade.kit.StringKit;
import com.blade.kit.UUID;
import com.blade.mvc.WebContext;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Csrf middleware
 *
 * @author biezhi
 *         2017/6/5
 */
@Slf4j
public class CsrfMiddleware implements WebHook {

    private Set<String>        tokens     = new HashSet<>(64);
    private CsrfConfig         csrfConfig = CsrfConfig.builder().build();
    private Consumer<Response> csrfHandle = response -> response.badRequest().text("Bad Request.");

    public CsrfMiddleware() {
    }

    public CsrfMiddleware(Consumer<Response> csrfHandle) {
        this.csrfHandle = csrfHandle;
    }

    public CsrfMiddleware(CsrfConfig csrfConfig, Consumer<Response> csrfHandle) {
        this.csrfConfig = csrfConfig;
        this.csrfHandle = csrfHandle;
    }

    @Override
    public boolean before(Signature signature) {
        Request   request   = signature.request();
        Method    method    = signature.getAction();
        CsrfToken csrfToken = method.getAnnotation(CsrfToken.class);
        if (null == csrfToken) {
            return true;
        }
        if (csrfToken.newToken()) {
            request.attribute(csrfConfig.getParam(), csrfConfig.getKey());
            request.attribute(csrfConfig.getHeader(), csrfConfig.getKey());
            String token = UUID.UU64();
            request.attribute(csrfConfig.getKey(), token);
            log.debug("Generate token [{}]", token);
            tokens.add(token);
        }
        if (csrfToken.valid() || StringKit.equals(Boolean.TRUE.toString(), signature.getRequest().header(csrfConfig.getValidId()))) {
            return validation();
        }
        return true;
    }

    public boolean validation() {
        Request          request       = WebContext.request();
        Response         response      = WebContext.response();
        Optional<String> tokenOptional = request.query(csrfConfig.getKey());

        if (!tokenOptional.isPresent()) {
            tokenOptional = Optional.ofNullable(request.header(csrfConfig.getKey()));
        }
        if (tokenOptional.isPresent()) {
            if (!tokens.contains(tokenOptional.get())) {
                // 不存在token
                csrfHandle.accept(response);
            } else {
                tokens.remove(tokenOptional.get());
                return true;
            }
        } else {
            csrfHandle.accept(response);
        }
        return false;
    }

}