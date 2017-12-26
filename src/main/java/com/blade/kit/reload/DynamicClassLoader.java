package com.blade.kit.reload;

import java.util.HashSet;

/**
 * Created by Eddie on 12/25/17.
 */
public class DynamicClassLoader extends ClassLoader {

    HashSet<String> loadedClass = new HashSet<>();
    HashSet<String> unavailableClass = new HashSet<>();
//
//    public DynamicClassLoader(String classPath){
//
//    }
//
//    public Class load(String filePath){
//
//    }


}
