package com.blade.security.web.csrf;

import com.blade.kit.PasswordKit;
import com.blade.kit.StringKit;
import com.blade.kit.UUID;
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
    public boolean before(Signature signature) {
        Request request = signature.request();
        Session session = request.session();

        if (csrfOption.isIgnoreMethod(request.method())) {
            if (csrfOption.isStartExclusion(request.uri())) {
                return true;
            }
            this.genToken(request);
            return true;
        }

        if (csrfOption.isExclusion(request.uri())) {
            return true;
        }

        String tokenUUID = session.attribute(sessionToken);
        if (StringKit.isEmpty(tokenUUID)) {
            csrfOption.getErrorHandler().accept(signature.response());
            return false;
        }

        String token = csrfOption.getTokenGetter().apply(request);
        if (StringKit.isEmpty(token)) {
            csrfOption.getErrorHandler().accept(signature.response());
            return false;
        }
        String hash = new String(Base64.getDecoder().decode(token));
        if (!PasswordKit.checkPassword(tokenUUID, hash)) {
            csrfOption.getErrorHandler().accept(signature.response());
            return false;
        }

        return true;
    }

    public String genToken(Request request) {
        String tokenUUID = request.session().attribute(sessionToken);
        if (StringKit.isEmpty(tokenUUID)) {
            tokenUUID = UUID.UU64();
            request.session().attribute(sessionToken, tokenUUID);
        }
        String token = Base64.getEncoder().encodeToString(PasswordKit.hashPassword(tokenUUID).getBytes());
        request.attribute("_csrf_token", token);
        request.attribute("_csrf_token_input", "<input type='hidden' name='_token' value='" + token + "'/>");
        return token;
    }

}