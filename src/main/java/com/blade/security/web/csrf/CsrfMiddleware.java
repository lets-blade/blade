package com.blade.security.web.csrf;

import com.blade.kit.EncryptKit;
import com.blade.kit.StringKit;
import com.blade.kit.UUID;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.http.Session;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Csrf middleware
 *
 * @author biezhi
 * 2017/6/5
 */
@Slf4j
public class CsrfMiddleware implements WebHook {

    static final List<String> DEFAULT_IGNORE_METHODS = Arrays.asList("GET", "HEAD", "OPTIONS");

    static final Consumer<Response> DEFAULT_ERROR_HANDLER = response -> response.badRequest().text("CSRF token mismatch.");

    static final Function<Request, String> DEFAULT_TOKEN_GETTER = request -> request.query("_token").orElseGet(() -> {
        if (StringKit.isNotBlank(request.header("X-CSRF-TOKEN"))) {
            return request.header("X-CSRF-TOKEN");
        }
        if (StringKit.isNotBlank(request.header("X-XSRF-TOKEN"))) {
            return request.header("X-XSRF-TOKEN");
        }
        return "";
    });

    private CsrfOption csrfOption = CsrfOption.builder().build();

    private final String csrfSecret = "csrfSecret";
    private final String csrfSalt   = "csrfSalt";

    public CsrfMiddleware() {
    }

    public CsrfMiddleware(CsrfOption csrfOption) {
        this.csrfOption = csrfOption;
    }

    @Override
    public boolean before(Signature signature) {
        Request request = signature.request();
        Session session = request.session();
        session.attribute(csrfSecret, csrfOption.getSecret());

        this.getToken(request);

        if (csrfOption.isIgnoreMethod(request.method())) {
            return true;
        }

        String salt = session.attribute(csrfSalt);
        if (StringKit.isEmpty(salt)) {
            csrfOption.getErrorHandler().accept(signature.response());
            return false;
        }

        String token = csrfOption.getTokenGetter().apply(request);
        if (!token.equals(tokenize(csrfOption.getSecret(), salt))) {
            csrfOption.getErrorHandler().accept(signature.response());
            return false;
        }

        return true;
    }

    public String getToken(Request request) {
        String secret = request.attribute(csrfSecret);
        if (StringKit.isNotBlank(secret)) {
            return secret;
        }
        String salt = request.session().attribute(csrfSalt);
        if (StringKit.isEmpty(salt)) {
            salt = UUID.UU64();
            request.session().attribute(csrfSalt, salt);
        }
        String token = tokenize(secret, salt);
        request.attribute("_csrf_token", token);
        request.attribute("_csrf_token_input", "<input type='hidden' name='_token' value='" + token + "'/>");
        return token;
    }

    private String tokenize(String secret, String salt) {
        String hash = EncryptKit.SHA1(salt + "-" + secret);
        return Base64.getEncoder().encodeToString(hash.getBytes(StandardCharsets.UTF_8));
    }

}