package com.hellokaton.blade.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtTest {

    /**
     * SECRET 是签名密钥，只生成一次即可，生成方法：
     * Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
     * String secretString = Encoders.BASE64.encode(key.getEncoded()); # 本文使用 BASE64 编码
     */
    private static final String SECRET = "lx5jo7ZAu2KrZNAzt04JnG5Z6kYLL1cZHOQDo7jrgIM=";

    /**
     * SecretKey 根据 SECRET 的编码方式解码后得到：
     * Base64 编码：SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
     * Base64URL 编码：SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretString));
     * 未编码：SecretKey key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
     */
    private SecretKey getSecretKey() {
        byte[] encodeKey = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(encodeKey);
    }

    @Test
    public void testBuildJwtToken() {
        // We need a signing key, so we'll create one just for this example. Usually
        // the key would be read from your application configuration instead.
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        System.out.println(Encoders.BASE64.encode(key.getEncoded()));

        String jwsStr = Jwts.builder().setSubject("Joe").signWith(key)
                .compact();
        System.out.println(jwsStr);

        jwsStr = Jwts.builder().setSubject("Joe").signWith(getSecretKey())
                .compact();
        System.out.println(jwsStr);

        Map<String, Object> claims = new HashMap<>();
        claims.put("sid", "sessionId");
        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(claims);

        long now = System.currentTimeMillis();

        jwtBuilder.setExpiration(new Date(now + 60 * 1000));

        SecretKey secretKey = getSecretKey();
        jwtBuilder.signWith(secretKey);

        jwsStr = jwtBuilder.compact();
        System.out.println(jwsStr);

        jwsStr = "a";
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwsStr);
            Claims body = jws.getBody();
            System.out.println(body.get("sid", String.class));
            System.out.println(body.get("exp", Double.class).longValue());
        } catch (JwtException e) {
            System.out.println("parse failed");
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
