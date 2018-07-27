package com.blade.security.web.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * Auth Option
 *
 * @author biezhi
 * @date 2018/7/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthOption {

    @Builder.Default
    private String realm = "Authorization Required";

    @Builder.Default
    private Set<String> urlStartExclusions = new HashSet<>(Arrays.asList("/"));

    @Builder.Default
    private Map<String, String> accounts = new HashMap<>();

    public AuthOption addUser(String username, String password) {
        this.accounts.put(username, password);
        return this;
    }

    public AuthOption urlStartExclusions(String... urls) {
        this.urlStartExclusions.clear();
        this.urlStartExclusions.addAll(Arrays.asList(urls));
        return this;
    }

    public AuthOption addUrlStartExclusion(String url) {
        this.urlStartExclusions.add(url);
        return this;
    }

}
