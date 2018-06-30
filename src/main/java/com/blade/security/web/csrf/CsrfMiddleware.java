package com.blade.security.web.csrf;

import com.blade.kit.PasswordKit;
import com.blade.kit.StringKit;
import com.blade.kit.UUID;
import com.blade.mvc.RouteContext;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Session;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

/**
 * Csrf middleware
 *
 * @author biezhi
 * 2017/6/5
 */
@Slf4j
@NoArgsConstructor
public class CsrfMiddleware implements WebHook {

    private CsrfOption csrfOption = CsrfOption.builder().build();

    /**
     * Token stored in session
     */
    private final String sessionToken = "_csrf_token_session";

    public CsrfMiddleware(CsrfOption csrfOption) {
        this.csrfOption = csrfOption;
    }

    @Override
    public boolean before(RouteContext context) {
        if (csrfOption.isIgnoreMethod(context.method())) {
            if (csrfOption.isStartExclusion(context.uri())) {
                return true;
            }
            this.genToken(context);
            return true;
        }

        if (csrfOption.isExclusion(context.uri())) {
            return true;
        }

        String tokenUUID = context.session().attribute(sessionToken);
        if (StringKit.isEmpty(tokenUUID)) {
            csrfOption.getErrorHandler().accept(context);
            return false;
        }

        String token = csrfOption.getTokenGetter().apply(context.request());
        if (StringKit.isEmpty(token)) {
            csrfOption.getErrorHandler().accept(context);
            return false;
        }
        String hash = new String(Base64.getDecoder().decode(token));
        if (!PasswordKit.checkPassword(tokenUUID, hash)) {
            csrfOption.getErrorHandler().accept(context);
            return false;
        }

        return true;
    }

    public String genToken(RouteContext context) {
        String tokenUUID = context.session().attribute(sessionToken);
        if (StringKit.isEmpty(tokenUUID)) {
            tokenUUID = UUID.UU64();
            context.session().attribute(sessionToken, tokenUUID);
        }
        String token = Base64.getEncoder().encodeToString(PasswordKit.hashPassword(tokenUUID).getBytes());
        context.attribute("_csrf_token", token);
        context.attribute("_csrf_token_input", "<input type='hidden' name='_token' value='" + token + "'/>");
        return token;
    }

}