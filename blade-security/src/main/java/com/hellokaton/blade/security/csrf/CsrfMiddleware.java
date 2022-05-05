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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Csrf middleware
 *
 * @author hellokaton
 * 2022/5/5
 */
@Slf4j
public class CsrfMiddleware implements WebHook {

    private final CsrfOptions csrfOptions;
    private final SecretKey secretKey;
    private static final String JWT_EXP_KEY = "exp";
    private static final String JWT_SID_KEY = "sid";

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
        if (csrfOptions.isExclusion(context.uri())) {
            return true;
        }

        HttpMethod httpMethod = context.request().httpMethod();
        // create token
        if (HttpMethod.GET.equals(context.request().httpMethod())) {
            String token = genToken(context.request());
            context.attribute(csrfOptions.getAttrKeyName(), token);
            return true;
        }
        // verify token
        if (csrfOptions.getVerifyMethods().contains(httpMethod)) {
            String token = parseRequestToken(context.request());
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
            long exp = body.get(JWT_EXP_KEY, Double.class).longValue();
            long now = System.currentTimeMillis() / 1000;
            // overdue
            if (now >= exp) {
                return false;
            }
            if (null == session) {
                return true;
            }
            String sid = (String) body.getOrDefault(JWT_SID_KEY, "");
            return session.id().equals(sid);
        } catch (Exception e) {
            log.error("Request IP: {}, UA: {}, Token: {} parse error",
                    request.remoteAddress(), request.userAgent(), token, e);
            return false;
        }
    }

    private String parseRequestToken(Request request) {
        String headerToken = request.header(csrfOptions.getHeaderKeyName());
        if (StringKit.isEmpty(headerToken)) {
            headerToken = request.form(csrfOptions.getFormKeyName(), "");
        }
        return headerToken;
    }

    public String genToken(Request request) {
        Session session = request.session();
        JwtBuilder jwtBuilder = Jwts.builder();
        long now = System.currentTimeMillis();
        jwtBuilder.setExpiration(new Date(now + csrfOptions.getTokenExpiredSeconds() * 1000));
        jwtBuilder.signWith(secretKey);
        if (null != session) {
            Map<String, Object> claims = new HashMap<>(1);
            claims.put("sid", session.id());
            jwtBuilder.setClaims(claims);
        }
        return jwtBuilder.compact();
    }

}