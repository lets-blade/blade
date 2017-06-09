package com.blade.model;

public class Person {

    private String name;
    private String text;
    private int age;

    public Person(String name, String text, int age) {
        this.name = name;
        this.text = text;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}