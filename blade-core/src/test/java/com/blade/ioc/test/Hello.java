package com.blade.ioc.test;

import com.blade.ioc.annotation.Inject;

public class Hello {
	
    @Inject
    private User user;

    public void says() {
        System.out.println("Hello " + user.getName());
    }
}