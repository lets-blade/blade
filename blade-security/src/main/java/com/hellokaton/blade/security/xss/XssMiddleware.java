package com.hellokaton.blade.security.xss;

import com.hellokaton.blade.kit.StringKit;
import com.hellokaton.blade.mvc.RouteContext;
import com.hellokaton.blade.mvc.hook.WebHook;
import com.hellokaton.blade.mvc.http.StringBody;
import com.hellokaton.blade.security.filter.HTMLFilter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * XSS Middleware
 *
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
    public boolean before(RouteContext context) {
        if (xssOption.isExclusion(context.uri())) {
            return true;
        }

        this.filterHeaders(context.headers());
        this.filterParameters(context.parameters());

        if (context.contentType().toLowerCase().contains("json")) {
            String body = context.bodyToString();
            if (StringKit.isNotEmpty(body)) {
                String filterBody = stripXSS(body);
                context.body(new StringBody(filterBody));
            }
        }
        return true;
    }

    protected void filterHeaders(Map<String, List<String>> headers) {
        headers.forEach((key, values) -> {
            List<String> newHeader = new ArrayList<>();
            for (String value : values) {
                newHeader.add(this.stripXSS(value));
            }
            headers.put(key, newHeader);
        });
    }

    protected void filterParameters(Map<String, List<String>> parameters) {
        Set<Map.Entry<String, List<String>>> entries = parameters.entrySet();

        for (Map.Entry<String, List<String>> entry : entries) {
            List<String> snzValues = entry.getValue().stream().map(this::stripXSS).collect(Collectors.toList());
            parameters.put(entry.getKey(), snzValues);
        }
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
