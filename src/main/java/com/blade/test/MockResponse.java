package com.blade.test;

import com.blade.mvc.http.Cookie;
import com.blade.mvc.http.Response;
import com.blade.mvc.ui.ModelAndView;
import com.blade.mvc.wrapper.OutputStreamWrapper;
import io.netty.handler.codec.http.FullHttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author biezhi
 * @date 2018/5/31
 */
public class MockResponse implements Response {

    @Override
    public int statusCode() {
        return 0;
    }

    @Override
    public Response status(int status) {
        return null;
    }

    @Override
    public Response contentType(CharSequence contentType) {
        return null;
    }

    @Override
    public String contentType() {
        return null;
    }

    @Override
    public Map<String, String> headers() {
        return null;
    }

    @Override
    public Response header(CharSequence name, CharSequence value) {
        return null;
    }

    @Override
    public Map<String, String> cookies() {
        return null;
    }

    @Override
    public Response cookie(Cookie cookie) {
        return null;
    }

    @Override
    public Response cookie(String name, String value) {
        return null;
    }

    @Override
    public Response cookie(String name, String value, int maxAge) {
        return null;
    }

    @Override
    public Response cookie(String name, String value, int maxAge, boolean secured) {
        return null;
    }

    @Override
    public Response cookie(String path, String name, String value, int maxAge, boolean secured) {
        return null;
    }

    @Override
    public Response removeCookie(String name) {
        return null;
    }

    @Override
    public void download(String fileName, File file) throws Exception {

    }

    @Override
    public OutputStreamWrapper outputStream() throws IOException {
        return null;
    }

    @Override
    public void render(ModelAndView modelAndView) {

    }

    @Override
    public void redirect(String newUri) {

    }

    @Override
    public boolean isCommit() {
        return false;
    }

    @Override
    public void send(FullHttpResponse response) {

    }

    @Override
    public ModelAndView modelAndView() {
        return null;
    }
}
