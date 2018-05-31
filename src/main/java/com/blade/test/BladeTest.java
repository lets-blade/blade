package com.blade.test;

import com.blade.Blade;

/**
 * @author biezhi
 * @date 2018/5/31
 */
public class BladeTest {

    private Blade blade;

    public BladeTest(Blade blade) {
        this.blade = blade;
    }

    public MockRequest get(String path) {
        return this.service("GET", path);
    }

    private MockRequest service(String httpMethod, String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        String url = "http://127.0.0.1:9000" + path;
        return new MockRequest(httpMethod, url);
    }

}
