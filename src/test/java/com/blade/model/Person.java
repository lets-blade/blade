package com.blade.model;

import lombok.Data;

@Data
public class Person {

    private String name;
    private String text;
    private int age;

    public Person() {
    }

    public Person(String name, String text, int age) {
        this.name = name;
        this.text = text;
        this.age = age;
    }

}