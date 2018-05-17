package com.blade.security.web.csrf;

import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Csrf config
 * <p>
 * Created by biezhi on 11/07/2017.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CsrfOption {

    @Builder.Default
    private String                    secret;
    @Builder.Default
    private List<String>              ignoreMethods = CsrfMiddleware.DEFAULT_IGNORE_METHODS;
    @Builder.Default
    private Consumer<Response>        errorHandler  = CsrfMiddleware.DEFAULT_ERROR_HANDLER;
    @Builder.Default
    private Function<Request, String> tokenGetter   = CsrfMiddleware.DEFAULT_TOKEN_GETTER;

    public boolean isIgnoreMethod(String method) {
        return ignoreMethods.contains(method);
    }

}
