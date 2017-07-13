package com.blade.security.web.csrf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Csrf config
 * <p>
 * Created by biezhi on 11/07/2017.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CsrfConfig {

    @Builder.Default
    private String token   = "_csrf.token";
    @Builder.Default
    private String param   = "_csrf.param";
    @Builder.Default
    private String header  = "_csrf.header";
    @Builder.Default
    private String validId = "_csrf.valid";
    @Builder.Default
    private String key     = "csrf_token";

}
