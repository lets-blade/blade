package com.blade.security.web.auth;

import com.blade.kit.StringKit;
import com.blade.mvc.WebContext;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

import static com.blade.mvc.Const.ENV_KEY_AUTH_PASSWORD;
import static com.blade.mvc.Const.ENV_KEY_AUTH_USERNAME;

/**
 * BasicAuth Middleware
 * <p>
 * Created by biezhi on 11/07/2017.
 */
@Slf4j
public class BasicAuthMiddleware implements WebHook {

    private String username;
    private String password;

    @Override
    public boolean before(Signature signature) {
        if (null == username) {
            this.username = WebContext.blade().environment().get(ENV_KEY_AUTH_USERNAME, "blade");
            this.password = WebContext.blade().environment().get(ENV_KEY_AUTH_PASSWORD, "blade");
        }
        Request request   = signature.request();
        Object  basicAuth = request.session().attribute("basic_auth");
        if (null != basicAuth) {
            return true;
        }
        Response response = signature.response();
        if (!checkHeaderAuth(request)) {
            response.unauthorized();
            response.header("Cache-Control", "no-store");
            response.header("Expires", "0");
            response.header("WWW-authenticate", "Basic Realm=\"Blade\"");
            return false;
        }
        return true;
    }

    private boolean checkHeaderAuth(Request request) {
        String auth = request.header("Authorization");
        log.debug("Authorization: {}", auth);

        if (StringKit.isNotBlank(auth) && auth.length() > 6) {
            auth = auth.substring(6, auth.length());
            String decodedAuth = getFromBASE64(auth);
            log.debug("Authorization decode: {}", decodedAuth);

            String[] arr = decodedAuth.split(":");
            if (arr.length == 2) {
                if (username.equals(arr[0]) && password.equals(arr[1])) {
                    request.session().attribute("basic_auth", decodedAuth);
                    return true;
                }
            }
        }

        return false;
    }

    private String getFromBASE64(String s) {
        if (s == null)
            return null;
        try {
            byte[] b = Base64.getDecoder().decode(s);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }

}
