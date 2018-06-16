package com.blade.security.web.xss;

import com.blade.kit.StringKit;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.Request;
import com.blade.security.web.filter.HTMLFilter;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author biezhi
 * @date 2018/6/11
 */
@NoArgsConstructor
public class XssMiddleware implements WebHook {

    private final static HTMLFilter HTML_FILTER = new HTMLFilter();

    private XssOption xssOption = XssOption.builder().build();

    public XssMiddleware(XssOption xssOption) {
        this.xssOption = xssOption;
    }

    @Override
    public boolean before(Signature signature) {
        Request request = signature.request();
        if (xssOption.isExclusion(request.uri())) {
            return true;
        }

        this.filterHeaders(request.headers());
        this.filterParameters(request.parameters());

        if (request.contentType().toLowerCase().contains("json")) {
            String body = request.bodyToString();
            if (StringKit.isNotEmpty(body)) {
                String filterBody = stripXSS(body);
                request.body().clear().writeBytes(filterBody.getBytes(StandardCharsets.UTF_8));
            }
        }
        return true;
    }

    protected void filterHeaders(Map<String, String> headers) {
        headers.forEach((key, value) -> headers.put(key, this.stripXSS(value)));
    }

    protected void filterParameters(Map<String, List<String>> parameters) {
        parameters.forEach((key, values) -> {
            List<String> snzValues = values.stream().map(this::stripXSS).collect(Collectors.toList());
            parameters.put(key, snzValues);
        });
    }

    /**
     * Removes all the potentially malicious characters from a string
     *
     * @param value the raw string
     * @return the sanitized string
     */
    protected String stripXSS(String value) {
        return HTML_FILTER.filter(value);
    }

}
