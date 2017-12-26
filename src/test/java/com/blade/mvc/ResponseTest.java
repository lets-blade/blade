package com.blade.mvc;

import com.blade.BaseTestCase;
import com.blade.mvc.http.HttpResponse;
import com.blade.mvc.http.Response;
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
public class ResponseTest extends BaseTestCase {

    private static final String CONTENT_TYPE = "Content-Type";

    @Test
    public void testStatus() throws Exception {
        Response mockResponse = mockResponse(666);
        assertEquals(666, mockResponse.statusCode());
    }

    @Test
    public void testBadRequest() throws Exception {
        Response mockResponse = mockResponse(200);
        Response response = new HttpResponse(mockResponse);
        response.badRequest();

        assertEquals(400, response.statusCode());
    }

    @Test
    public void testUnauthorized() throws Exception {
        Response mockResponse = mockResponse(200);
        Response response = new HttpResponse(mockResponse);
        response.unauthorized();

        assertEquals(401, response.statusCode());
    }

    @Test
    public void testNotFound() throws Exception {
        Response mockResponse = mockResponse(200);
        Response response = new HttpResponse(mockResponse);
        response.notFound();

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testContentType() throws Exception {
        Response mockResponse = mockResponse(200);

        Response response = new HttpResponse(mockResponse);
        response.contentType(Const.CONTENT_TYPE_HTML);

        assertEquals(Const.CONTENT_TYPE_HTML, response.contentType());

        response.contentType("hello.world");
        assertEquals("hello.world", response.contentType());
    }

    @Test
    public void testHeaders() throws Exception {
        Response mockResponse = mockResponse(200);

        when(mockResponse.headers()).thenReturn(new HashMap<>());

        Response response = new HttpResponse(mockResponse);
        assertEquals(0, response.headers().size());

        response.header("a", "123");
        assertEquals(1, response.headers().size());
    }

    @Test
    public void testHeader() throws Exception {

        Response mockResponse = mockResponse(200);

        when(mockResponse.headers()).thenReturn(Collections.singletonMap("Server", "Nginx"));

        Response response = new HttpResponse(mockResponse);
        assertEquals(1, response.headers().size());
        assertEquals("Nginx", response.headers().get("Server"));
    }

    @Test
    public void testCookie() throws Exception {

        Response mockResponse = mockResponse(200);

        when(mockResponse.cookies()).thenReturn(Collections.singletonMap("c1", "value1"));

        Response response = new HttpResponse(mockResponse);

        assertEquals(1, response.cookies().size());
        assertEquals("value1", response.cookies().get("c1"));
    }

}
