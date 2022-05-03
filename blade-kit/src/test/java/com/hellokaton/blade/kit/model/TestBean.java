package com.hellokaton.blade.kit.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TestBean {

    @SerializedName(value = "Name")
    private String name;

    @SerializedName(value = "Sex")
    private boolean sex;

    private int age;

    @SerializedName(value = "Friend")
    private List<TestBean> friend = new ArrayList<>();

    @SerializedName(value = "Others")
    private Object[] otherList;

    private transient Double price;

    private LocalDateTime dateTime;

}