package com.blade.security.web.xss;

import lombok.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author biezhi
 * @date 2018/6/11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XssOption {

    @Builder.Default
    private Set<String> urlExclusions = new HashSet<>();

    public XssOption exclusion(@NonNull String... urls) {
        this.urlExclusions.addAll(Arrays.asList(urls));
        return this;
    }

    public boolean isExclusion(@NonNull String url) {
        for (String excludeURL : this.urlExclusions) {
            if (url.equals(excludeURL)) {
                return true;
            }
        }
        return false;
    }

}
