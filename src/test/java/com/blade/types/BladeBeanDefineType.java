package com.blade.types;

import lombok.Data;

@Data
public class BladeBeanDefineType {
    private String name;

    public String hello(String name){
        return name;
    }

}