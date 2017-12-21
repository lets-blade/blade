package com.blade.server.netty;

/**
 * Thread model
 *
 * @author biezhi
 * @date 2017/12/21
 */
public enum ThreadModel {

    SIGNLE("single thread model"), MULTI("multi threading model"),
    MASTER_SLAVE("master slave multi thread model"), CUSTOM("custom thread count");

    private String description;

    ThreadModel(String description) {
        this.description = description;
    }

}