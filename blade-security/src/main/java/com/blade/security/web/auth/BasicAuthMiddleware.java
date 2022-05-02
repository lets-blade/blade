package com.blade.security.web.auth;

import com.blade.kit.StringKit;
import com.blade.mvc.RouteContext;
import com.blade.mvc.hook.WebHook;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

/**
 * BasicAuth Middleware
 * <p>
 * Created by biezhi on 11/07/2017.
 */
@Slf4j
public class BasicAuthMiddleware implements WebHook {

    private String         realm;
    private List<AuthPair> authPairs = new ArrayList<>();
    private Set<String>    urlStartExclusions;

    public BasicAuthMiddleware(AuthOption authOption) {
        this.urlStartExclusions = authOption.getUrlStartExclusions();
        this.realm = "Basic realm=\"" + authOption.getRealm() + "\"";
        authOption.getAccounts().forEach((user, pass) -> this.authPairs.add(new AuthPair(user, authorizationHeader(user, pass))));
    }

    private String authorizationHeader(String user, String password) {
        String base = user + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(base.getBytes(StandardCharsets.UTF_8));
    }

    private String searchCredential(String authValue) {
        if (StringKit.isEmpty(authValue)) {
            return null;
        }
        return authPairs.stream()
                .filter(authPair -> authPair.getValue().equals(authValue))
                .map(AuthPair::getUser)
                .findFirst().orElse(null);
    }

    @Override
    public boolean before(RouteContext context) {
        boolean isAuth = false;

        for (String startExclusion : urlStartExclusions) {
            if ("/".equals(startExclusion) || context.uri().startsWith(startExclusion)) {
                isAuth = true;
                break;
            }
        }

        if (!isAuth) {
            return true;
        }

        String authorization = context.header("Authorization");
        String user          = this.searchCredential(authorization);
        if (null == user) {
            context.header("WWW-Authenticate", this.realm).status(401);
            return false;
        }
        return true;
    }

}
