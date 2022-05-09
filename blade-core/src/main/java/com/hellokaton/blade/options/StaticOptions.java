package com.hellokaton.blade.options;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class StaticOptions {

    public static final int DEFAULT_CACHE_SECONDS = 86400 * 30;
    public static final Set<String> DEFAULT_STATICS = new HashSet<>(
            Arrays.asList("/favicon.ico", "/robots.txt", "/static", "/webjars/"));

    private boolean showList;
    private Set<String> paths = DEFAULT_STATICS;
    private int cacheSeconds = DEFAULT_CACHE_SECONDS;

    public static StaticOptions create() {
        return new StaticOptions();
    }

    public StaticOptions showList() {
        this.showList = true;
        return this;
    }

    public StaticOptions addStatic(String staticPath) {
        this.paths.add(staticPath);
        return this;
    }

    public StaticOptions removeStatic(String staticPath) {
        this.paths.remove(staticPath);
        return this;
    }

    public StaticOptions cacheSeconds(int cacheSeconds) {
        this.cacheSeconds = cacheSeconds;
        return this;
    }

}
