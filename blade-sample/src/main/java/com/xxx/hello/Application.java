package com.xxx.hello;

import com.blade.Blade;
import com.blade.Const;
import com.blade.kit.json.JSONObject;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.view.RestResponse;
import com.blade.mvc.view.ViewSettings;
import com.blade.mvc.view.template.VelocityTemplateEngine;
import com.xxx.hello.controller.MsgController;

import java.beans.Introspector;

import static com.blade.Blade.$;

/**
 * Created by biezhi on 2016/12/17.
 */
public class Application {

    public static void main(String[] args) {

        // setting default template engine is velocity :)
        ViewSettings.$().templateEngine(new VelocityTemplateEngine());


        $().route("/msg", MsgController.class, "msg", HttpMethod.GET);

        $().get("/", (request, response) -> {

            RestResponse<JSONObject> restResponse = new RestResponse<>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "blade");
            jsonObject.put("version", Const.VERSION);
            restResponse.setPayload(jsonObject);
            response.json(restResponse);

        }).get("/hello", (request, response)-> {
            request.attribute("name", request.query("name", "boy"));
            response.render("hello.vm");
        }).delete("/user/:id", (request, response)-> {
            int id = request.pathParamAsInt("id");
            System.out.println("userid is " + id);
        }).start(Application.class);
    }

}
