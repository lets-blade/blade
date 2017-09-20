package com.blade.mvc.ui;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author biezhi
 * @date 2017/9/20
 */
public class ModelAndViewTest {

    @Test
    public void testModelAndView(){
        ModelAndView modelAndView = new ModelAndView();
        Assert.assertEquals(0, modelAndView.getModel().size());
    }

    @Test
    public void testModelAndView2(){
        ModelAndView modelAndView = new ModelAndView("index.html");
        Assert.assertEquals("index.html", modelAndView.getView());
    }

    @Test
    public void testModelAndView3(){
        Map<String, Object> model = new HashMap<>();
        model.put("name", "jack");
        ModelAndView modelAndView = new ModelAndView(model, "index.html");
        Assert.assertEquals("index.html", modelAndView.getView());
        Assert.assertEquals(1, modelAndView.getModel().size());
        Assert.assertEquals("jack", modelAndView.getModel().get("name"));

        modelAndView.setView("users.html");
        Assert.assertEquals("users.html", modelAndView.getView());

        Map<String, Object> model2 = new HashMap<>();
        modelAndView.setModel(model2);
        Assert.assertEquals(0, modelAndView.getModel().size());
    }

    @Test
    public void testModelAndView4(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.add("age", 20);
        Assert.assertEquals(1, modelAndView.getModel().size());
        Assert.assertEquals(20, modelAndView.getModel().get("age"));

        modelAndView.remove("age");
        Assert.assertEquals(0, modelAndView.getModel().size());
    }

}
