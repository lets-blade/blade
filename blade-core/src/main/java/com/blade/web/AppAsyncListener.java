package com.blade.web;

import java.io.IOException;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
 
//@WebListener
public class AppAsyncListener implements AsyncListener {
	
    @Override
    public void onComplete(AsyncEvent asyncEvent) throws IOException {
//        System.out.println("AppAsyncListener onComplete");
    }
 
    @Override
    public void onError(AsyncEvent asyncEvent) throws IOException {
//        System.out.println("AppAsyncListener onError");
    }
 
    @Override
    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
//        System.out.println("AppAsyncListener onStartAsync");
    }
 
    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
//        System.out.println("AppAsyncListener onTimeout");
//        ServletResponse response = asyncEvent.getAsyncContext().getResponse();
//        PrintWriter out = response.getWriter();
//        out.write("TimeOut Error in Processing");
    }
 
}