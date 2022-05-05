package com.hellokaton.blade.security.csrf;

import com.hellokaton.blade.kit.StringKit;
import com.hellokaton.blade.mvc.RouteContext;
import com.hellokaton.blade.mvc.hook.WebHook;
import com.hellokaton.blade.mvc.http.HttpMethod;
import com.hellokaton.blade.mvc.http.Request;
import com.hellokaton.blade.mvc.http.Session;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Collections;

/**
 * Csrf middleware
 *
 * <p>
 * <meta name="csrf-token" content="{{ _csrf_token }}">
 * </p>
 * $.ajaxSetup({
 * headers: {
 * 'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
 * }
 * });
 *
 * @author hellokaton
 * 2022/5/5
 */
@Slf4j
public class CsrfMiddleware implements WebHook {

    private static final String JWT_SID_KEY = "sid";

    private final CsrfOptions csrfOptions;
    private final SecretKey secretKey;

    public CsrfMiddleware() {
        this(new CsrfOptions());
    }

    public CsrfMiddleware(CsrfOptions csrfOptions) {
        this.csrfOptions = csrfOptions;

        byte[] encodeKey = Decoders.BASE64.decode(csrfOptions.getSecret());
        this.secretKey = Keys.hmacShaKeyFor(encodeKey);
    }

    @Override
    public boolean before(RouteContext context) {
        if (!csrfOptions.isEnabled()) {
            return true;
        }
        if (null == context.session()) {
            return true;
        }
        if (csrfOptions.isExclusion(context.uri())) {
            return true;
        }

        HttpMethod httpMethod = context.request().httpMethod();
        // create token
        if (HttpMethod.GET.equals(context.request().httpMethod())) {
            String token = this.genToken(context.request());
            context.attribute(csrfOptions.getAttrKeyName(), token);
            return true;
        }
        // verify token
        if (csrfOptions.getVerifyMethods().contains(httpMethod)) {
            String token = this.parseToken(context.request());
            boolean verified = verifyToken(context.request(), token);
            if (verified) {
                return true;
            }
            if (null != csrfOptions.getErrorHandler()) {
                return csrfOptions.getErrorHandler().apply(context);
            } else {
                context.badRequest().text("CSRF token mismatch.");
                return false;
            }
        }
        return true;
    }

    private boolean verifyToken(Request request, String token) {
        if (StringKit.isEmpty(token)) {
            return false;
        }
        Session session = request.session();
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            Claims body = jws.getBody();

            String sid = (String) body.getOrDefault(JWT_SID_KEY, "");
            return session.id().equals(sid);
        } catch (Exception e) {
            log.error("Request IP: {}, UA: {}, Token: {} parse error",
                    request.remoteAddress(), request.userAgent(), token, e);
            return false;
        }
    }

    protected String parseToken(Request request) {
        String headerToken = request.header(csrfOptions.getHeaderKeyName());
        if (StringKit.isEmpty(headerToken)) {
            headerToken = request.form(csrfOptions.getFormKeyName(), "");
        }
        return headerToken;
    }

    protected String genToken(Request request) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(Collections.singletonMap(JWT_SID_KEY, request.session().id()))
                .signWith(secretKey);

        return jwtBuilder.compact();
    }

}