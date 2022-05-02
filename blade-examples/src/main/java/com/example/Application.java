package com.example;

import com.blade.Blade;
import com.blade.annotation.Path;
import com.blade.annotation.route.GET;

import java.util.HashMap;
import java.util.Map;

@Path(responseJson = true)
public class Application {

    @GET("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "hellokaton");
        return result;
    }

    public static void main(String[] args) {
        Blade.of().listen().start(Application.class);
    }

}
