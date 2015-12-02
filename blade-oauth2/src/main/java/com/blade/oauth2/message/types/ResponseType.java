/*
 * 
 */
package com.blade.oauth2.message.types;

/**
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public enum ResponseType {

    CODE("code"),
    TOKEN("token");

    private String code;

    ResponseType(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
