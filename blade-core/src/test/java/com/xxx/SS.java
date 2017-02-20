package com.xxx;

import com.blade.Blade;

/**
 * Created by biezhi on 2017/2/20.
 */
public class SS {

    public static void main(String[] args) {
        Blade.$().get("/", (request, response) -> {
            response.text("Hello World");
        }).start(SS.class);
    }
}
