package com.xxx.hello.controller;

import com.blade.mvc.annotation.Controller;
import com.blade.mvc.annotation.PathParam;
import com.blade.mvc.annotation.QueryParam;
import com.blade.mvc.annotation.Route;

/**
 * Created by biezhi on 2017/1/2.
 */
@Controller
public class IndexController {

    @Route("sayHi")
    public void sayHi(@QueryParam("name") String name){
        System.out.println("name = " + name);
    }

    @Route("sayHi/:name")
    public void sayHi2(@PathParam String name){
        System.out.println("name = " + name);
    }

}
