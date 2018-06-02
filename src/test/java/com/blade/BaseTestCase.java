package com.blade;

import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Response;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import lombok.extern.slf4j.Slf4j;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Base Test case
 *
 * @author biezhi
 * 2017/6/3
 */
@Slf4j
public class BaseTestCase {

    private   String origin    = "http://127.0.0.1:9000";
    protected String firefoxUA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:53.0) Gecko/20100101 Firefox/53.0";

    protected com.blade.mvc.http.HttpRequest mockHttpRequest(String methodName) {
        com.blade.mvc.http.HttpRequest request = mock(com.blade.mvc.http.HttpRequest.class);
        when(request.method()).thenReturn(methodName);
        when(request.httpMethod()).thenReturn(HttpMethod.valueOf(methodName));
        return request;
    }

    protected Response mockHttpResponse(int code) {
        Response response = mock(Response.class);
        when(response.statusCode()).thenReturn(code);
        return response;
    }

    protected HttpRequest get(String path) {
        log.info("[GET] {}", (origin + path));
        return Unirest.get(origin + path);
    }

    protected String getBodyString(String path) throws Exception {
        log.info("[GET] {}", (origin + path));
        return Unirest.get(origin + path).asString().getBody();
    }

    protected HttpRequestWithBody post(String path) {
        log.info("[POST] {}", (origin + path));
        return Unirest.post(origin + path);
    }

    protected String postBodyString(String path) throws Exception {
        log.info("[POST] {}", (origin + path));
        return Unirest.post(origin + path).asString().getBody();
    }

    protected HttpRequest put(String path) {
        log.info("[PUT] {}", (origin + path));
        return Unirest.put(origin + path);
    }

    protected String putBodyString(String path) throws Exception {
        log.info("[PUT] {}", (origin + path));
        return Unirest.put(origin + path).asString().getBody();
    }

    protected HttpRequest delete(String path) {
        log.info("[DELETE] {}", (origin + path));
        return Unirest.delete(origin + path);
    }

    protected String deleteBodyString(String path) throws Exception {
        log.info("[DELETE] {}", (origin + path));
        return Unirest.delete(origin + path).asString().getBody();
    }

    protected String bodyToString(String path) throws Exception {
        return get(path).asString().getBody();
    }

}
