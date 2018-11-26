package com.blade.security.web.cors;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author PSH
 * Date: 2018/10/29
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CorsConfiger {

    public static final String ALL                        = "*";

    public static final String DEFAULT_ALLOWED_HEADERS    = "Origin, X-Requested-With, Content-Type,"
        + " Accept, Connection, User-Agent, Cookie, Cache-Control, token";

    public static final String DEFAULT_ALLOWED_METHODS    = "GET, OPTIONS, HEAD, PUT, POST, DELETE";

    public static final String DEFAULT_ALLOW_CREDENTIALS  = "true";

    public static final Long DEFAULT_MAX_AGE              = 1800L;


    private List<String> allowedMethods;

    private List<String> allowedHeaders;

    private Long maxAge;

    private Boolean allowCredentials;

}
