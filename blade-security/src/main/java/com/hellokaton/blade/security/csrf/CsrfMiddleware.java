package com.hellokaton.blade.security.csrf;

import com.hellokaton.blade.kit.Pair;
import com.hellokaton.blade.kit.StringKit;
import com.hellokaton.blade.kit.UUID;
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
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    private static final String JWT_EXP_KEY = "exp";
    private static final String JWT_SID_KEY = "sid";

    private final CsrfOptions csrfOptions;
    private final SecretKey secretKey;

    private final ExpiringMap<String, Boolean> verifiedToken;

    public CsrfMiddleware() {
        this(new CsrfOptions());
    }

    public CsrfMiddleware(CsrfOptions csrfOptions) {
        this.csrfOptions = csrfOptions;

        byte[] encodeKey = Decoders.BASE64.decode(csrfOptions.getSecret());
        this.secretKey = Keys.hmacShaKeyFor(encodeKey);

        this.verifiedToken = ExpiringMap.builder()
                .maxSize(1024)
                .variableExpiration()
                .build();
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
            Pair<Boolean, Long> verified = verifyToken(context.request(), token);
            if (Boolean.TRUE.equals(verified.getKey())) {
                verifiedToken.put(token, Boolean.TRUE, ExpirationPolicy.ACCESSED, verified.getValue(), TimeUnit.SECONDS);
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

    private Pair<Boolean, Long> verifyToken(Request request, String token) {
        if (StringKit.isEmpty(token)) {
            return Pair.of(false, 0L);
        }
        if (verifiedToken.containsKey(token)) {
            return Pair.of(false, 0L);
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
                return Pair.of(false, 0L);
            }
            if (null == session) {
                return Pair.of(true, now - exp);
            }
            String sid = (String) body.getOrDefault(JWT_SID_KEY, "");
            return session.id().equals(sid) ? Pair.of(true, now - exp) : Pair.of(false, 0L);
        } catch (Exception e) {
            log.error("Request IP: {}, UA: {}, Token: {} parse error",
                    request.remoteAddress(), request.userAgent(), token, e);
            return Pair.of(false, 0L);
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
        Map<String, Object> claims = new HashMap<>(1);
        if (null != session) {
            claims.put(JWT_SID_KEY, session.id());
        } else {
            claims.put(JWT_SID_KEY, UUID.UU64());
        }
        jwtBuilder.setClaims(claims);
        return jwtBuilder.compact();
    }

}