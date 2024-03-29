package com.hellokaton.blade.mvc;

import com.hellokaton.blade.BaseTestCase;
import com.hellokaton.blade.mvc.http.HttpResponse;
import com.hellokaton.blade.mvc.http.Response;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * HttpResponse TestCase
 *
 * @author biezhi
 * 2017/6/3
 */
public class HttpResponseTest extends BaseTestCase {

    private static final String CONTENT_TYPE = "Content-Type";

    @Test
    public void testStatus() {
        Response mockResponse = mockHttpResponse(HttpResponseStatus.OK.code());
        assertEquals(HttpResponseStatus.OK.code(), mockResponse.statusCode());
    }

    @Test
    public void testBadRequest() {
        Response mockResponse = mockHttpResponse(HttpResponseStatus.OK.code());
        Response response = new HttpResponse(mockResponse);
        response.badRequest();

        assertEquals(HttpResponseStatus.BAD_REQUEST.code(), response.statusCode());
    }

    @Test
    public void testUnauthorized() {
        Response mockResponse = mockHttpResponse(HttpResponseStatus.OK.code());
        Response response = new HttpResponse(mockResponse);
        response.unauthorized();

        assertEquals(HttpResponseStatus.UNAUTHORIZED.code(), response.statusCode());
    }

    @Test
    public void testNotFound() {
        Response mockResponse = mockHttpResponse(HttpResponseStatus.OK.code());
        Response response = new HttpResponse(mockResponse);
        response.notFound();

        assertEquals(HttpResponseStatus.NOT_FOUND.code(), response.statusCode());
    }

    @Test
    public void testContentType() {
        Response mockResponse = mockHttpResponse(HttpResponseStatus.OK.code());

        Response response = new HttpResponse(mockResponse);
        response.contentType(HttpConst.CONTENT_TYPE_HTML);

        assertEquals(HttpConst.CONTENT_TYPE_HTML, response.contentType());

        response.contentType("hello.world");
        assertEquals("hello.world", response.contentType());
    }

    @Test
    public void testHeaders() {
        Response mockResponse = mockHttpResponse(HttpResponseStatus.OK.code());

        when(mockResponse.headers()).thenReturn(new HashMap<>());

        Response response = new HttpResponse(mockResponse);
        assertEquals(0, response.headers().size());

        response.header("a", "123");
        assertEquals(1, response.headers().size());
    }

    @Test
    public void testHeader() {

        Response mockResponse = mockHttpResponse(HttpResponseStatus.OK.code());

        when(mockResponse.headers()).thenReturn(Collections.singletonMap("Server", "Nginx"));

        Response response = new HttpResponse(mockResponse);
        assertEquals(1, response.headers().size());
        assertEquals("Nginx", response.headers().get("Server"));
    }

    @Test
    public void testCookie() {

        Response mockResponse = mockHttpResponse(HttpResponseStatus.OK.code());

        when(mockResponse.cookies()).thenReturn(Collections.singletonMap("c1", "value1"));

        Response response = new HttpResponse(mockResponse);

        assertEquals(1, response.cookies().size());
        assertEquals("value1", response.cookies().get("c1"));
    }

}
