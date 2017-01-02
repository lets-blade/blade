package com.xxx.hello;

import com.blade.Const;
import com.blade.kit.json.JSONObject;
import com.blade.mvc.view.RestResponse;
import com.blade.mvc.view.ViewSettings;
import com.blade.mvc.view.template.VelocityTemplateEngine;

import static com.blade.Blade.$;

/**
 * Created by biezhi on 2016/12/17.
 */
public class Application {

    public static void main(String[] args) {

        // setting default template engine is velocity :)
        ViewSettings.$().templateEngine(new VelocityTemplateEngine());

        $().get("/", (request, response) -> {

            RestResponse<JSONObject> restResponse = new RestResponse<>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "blade");
            jsonObject.put("version", Const.VERSION);
            restResponse.setPayload(jsonObject);
            response.json(restResponse);

        }).get("/hello", (request, response)-> {

            String name = request.query("name", "boy");

            request.attribute("name", name);
            response.render("hello.vm");

        }).start(Application.class);
    }

}
