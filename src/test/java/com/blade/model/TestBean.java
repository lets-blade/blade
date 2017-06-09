package com.blade.model;

import com.blade.kit.json.JsonIgnore;
import com.blade.kit.json.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public TestBean() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<TestBean> getFriend() {
        return friend;
    }

    public void setFriend(List<TestBean> friend) {
        this.friend = friend;
    }

    public Object[] getOtherList() {
        return otherList;
    }

    public void setOtherList(Object[] otherList) {
        this.otherList = otherList;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "TestBean(" +
                "name='" + name + '\'' +
                ", sex=" + sex +
                ", age=" + age +
                ", friend=" + friend +
                ", otherList=" + Arrays.toString(otherList) +
                ", price=" + price +
                ')';
    }
}