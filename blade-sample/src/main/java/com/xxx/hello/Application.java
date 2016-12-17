package com.xxx.hello;

import com.blade.Const;
import com.blade.kit.json.JSONObject;
import com.blade.mvc.view.RestResponse;

import static com.blade.Blade.$;

/**
 * Created by biezhi on 2016/12/17.
 */
public class Application {

    public static void main(String[] args) {

        $().get("/", (request, response) -> {

            RestResponse<JSONObject> restResponse = new RestResponse<>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "blade");
            jsonObject.put("version", Const.VERSION);
            restResponse.setPayload(jsonObject);
            response.json(restResponse);

        }).get("/hello", (req, res)->{
            System.out.println("");
        }).start(Application.class);
    }

}
