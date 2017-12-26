package com.blade.model;

import com.blade.ioc.annotation.Bean;
import com.blade.ioc.annotation.Value;

import java.util.List;
import java.util.Map;

/**
 * @author : ccqy66
 * Date: 2017/12/25
 */
@Bean
public class ValueBean {
    @Value(name = "list")
    private List<String> list;
    @Value(name = "app.version")
    private String appversion;
    @Value(name = "map")
    private Map<String,String> map;

    @Override
    public String toString() {
        return "ValueBean{" +
                "list=" + list +
                ", appversion='" + appversion + '\'' +
                ", map=" + map +
                '}';
    }
}
