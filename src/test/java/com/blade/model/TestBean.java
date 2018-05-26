package com.blade.model;

import com.blade.kit.json.JsonIgnore;
import com.blade.kit.json.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class TestBean {

    @JsonProperty(value = "Name")
    private String name;

    @JsonProperty(value = "Sex")
    private boolean sex;

    private int age;

    @JsonProperty(value = "Friend")
    private List<TestBean> friend = new ArrayList<>();

    @JsonProperty(value = "Others")
    private Object[] otherList;

    @JsonIgnore
    private Double price;

    private LocalDateTime dateTime;

}