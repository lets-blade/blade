package com.blade.mvc;

import com.blade.BaseTestCase;
import com.blade.mvc.http.Cookie;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.Request;
import com.blade.mvc.multipart.FileItem;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * HttpRequest TestCase
 *
 * @author biezhi
 * 2017/6/3
 */
public class RequestTest extends BaseTestCase {

    @Test
    public void testGetHost() {
        Request request = mockRequest("GET");
        when(request.host()).thenReturn("localhost");

        Assert.assertEquals(request.host(), "localhost");
    }

    @Test
    public void testCookie() throws Exception {
        Map<String, Cookie> cookies = new HashMap<>();
        Cookie              c1      = new Cookie();
        c1.httpOnly(true);
        c1.maxAge(-1);
        c1.value("1");
        cookies.put("uid", c1);

        Request mockRequest = mockRequest("GET");
        when(mockRequest.cookies()).thenReturn(cookies);

        Request request = new HttpRequest(mockRequest);

        Map<String, Cookie> reqCookies = request.cookies();
        Cookie              cookie     = reqCookies.get("uid");

        Assert.assertNotNull(cookie);
        Assert.assertEquals(cookie.value(), "1");
        Assert.assertEquals(cookie.maxAge(), -1);
        assertNull(cookie.domain());
        Assert.assertEquals(cookie.path(), "/");

        Assert.assertNotNull(request.cookieRaw("uid"));
        assertNull(request.cookieRaw("not-existent"));
    }

    @Test
    public void testPathParam() throws Exception {
        Request mockRequest = mockRequest("GET");

        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", "6");
        pathParams.put("age", "24");
        pathParams.put("name", "jack");
        when(mockRequest.pathParams()).thenReturn(pathParams);

        Request request = new HttpRequest(mockRequest);
        assertEquals(Long.valueOf(6), request.pathLong("id"));
        assertEquals(Integer.valueOf(24), request.pathInt("age"));
        assertEquals("jack", request.pathString("name"));
    }

    @Test
    public void testUri() throws Exception {
        Request mockRequest = mockRequest("GET");
        when(mockRequest.url()).thenReturn("/a");

        Request request = new HttpRequest(mockRequest);

        assertEquals("/a", request.uri());

        when(mockRequest.url()).thenReturn("/a/b?username=jack");
        request = new HttpRequest(mockRequest);

        assertEquals("/a/b", request.uri());
    }

    @Test
    public void testUrl() throws Exception {
        Request mockRequest = mockRequest("GET");

        when(mockRequest.url()).thenReturn("/hello?name=q1");

        assertEquals("/hello?name=q1", mockRequest.url());
    }

    @Test
    public void testUserAgent() throws Exception {
        Map<String, String> headers = Collections.singletonMap("User-Agent", firefoxUA);

        Request mockRequest = mockRequest("GET");
        when(mockRequest.headers()).thenReturn(headers);

        Request request = new HttpRequest(mockRequest);
        assertEquals(firefoxUA, request.userAgent());
    }

    @Test
    public void testProtocol() throws Exception {
        Request mockRequest = mockRequest("GET");
        when(mockRequest.protocol()).thenReturn("HTTP/1.1");
        assertEquals("HTTP/1.1", mockRequest.protocol());
    }

    @Test
    public void testQueryString() throws Exception {
        Request mockRequest = mockRequest("GET");
        when(mockRequest.url()).thenReturn("/hello?name=q1");

        Request request = new HttpRequest(mockRequest);
        assertEquals("name=q1", request.queryString());
    }

    @Test
    public void testQueryParam() throws Exception {

        Request mockRequest = mockRequest("GET");

        Map<String, List<String>> parameters = new HashMap<>();
        parameters.put("name", Arrays.asList("jack"));
        parameters.put("price", Arrays.asList("22.1"));
        parameters.put("age", Arrays.asList("25"));
        parameters.put("id", Arrays.asList("220291"));

        when(mockRequest.parameters()).thenReturn(parameters);

        Request request = new HttpRequest(mockRequest);

        assertEquals("jack", request.query("name").get());
        assertEquals(Double.valueOf(22.1), request.queryDouble("price").get());
        assertEquals(Long.valueOf(220291), request.queryLong("id").get());
        assertEquals(Integer.valueOf(25), request.queryInt("age").get());
    }

    @Test
    public void testHttpMethod() throws Exception {
        assertEquals("GET", mockRequest("GET").method());
        assertEquals("POST", mockRequest("POST").method());
        assertEquals("PUT", mockRequest("PUT").method());
        assertEquals("DELETE", mockRequest("DELETE").method());
    }

    @Test
    public void testAddress() throws Exception {
        Request mockRequest = mockRequest("GET");
        when(mockRequest.address()).thenReturn("127.0.0.1");

        assertEquals("127.0.0.1", mockRequest.address());
    }

    @Test
    public void testContentType() throws Exception {

        Request mockRequest = mockRequest("GET");
        when(mockRequest.contentType()).thenReturn(Const.CONTENT_TYPE_HTML);

        assertEquals(Const.CONTENT_TYPE_HTML, mockRequest.contentType());

        when(mockRequest.contentType()).thenReturn(Const.CONTENT_TYPE_JSON);
        assertEquals(Const.CONTENT_TYPE_JSON, mockRequest.contentType());
    }

    @Test
    public void testIsSecure() throws Exception {
        Request mockRequest = mockRequest("GET");
        when(mockRequest.isSecure()).thenReturn(false);

        assertEquals(Boolean.FALSE, mockRequest.isSecure());
    }

    @Test
    public void testIsAjax() throws Exception {

        Request             mockRequest = mockRequest("GET");
        Map<String, String> headers     = Collections.singletonMap("x-requested-with", "XMLHttpRequest");
        when(mockRequest.headers()).thenReturn(headers);

        Request request = new HttpRequest(mockRequest);

        assertEquals(Boolean.TRUE, request.isAjax());

        when(mockRequest.headers()).thenReturn(Collections.EMPTY_MAP);
        request = new HttpRequest(mockRequest);

        assertEquals(Boolean.FALSE, request.isAjax());

    }

    @Test
    public void testIsIE() throws Exception {
        Request             mockRequest = mockRequest("GET");
        Map<String, String> headers     = Collections.singletonMap("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
        when(mockRequest.headers()).thenReturn(headers);

        Request request = new HttpRequest(mockRequest);

        assertEquals(Boolean.TRUE, request.isIE());

        when(mockRequest.headers()).thenReturn(Collections.EMPTY_MAP);
        request = new HttpRequest(mockRequest);

        assertEquals(Boolean.FALSE, request.isIE());
    }

    @Test
    public void testHeaders() throws Exception {
        Request             mockRequest = mockRequest("GET");
        Map<String, String> headers     = new HashMap<>();
        headers.put("h1", "a1");
        headers.put("h2", "a2");

        when(mockRequest.headers()).thenReturn(headers);

        Request request = new HttpRequest(mockRequest);

        assertEquals("a1", request.header("h1"));
        assertEquals("a2", request.header("h2"));
    }

    @Test
    public void testKeepAlive() throws Exception {
        Request mockRequest = mockRequest("GET");
        when(mockRequest.keepAlive()).thenReturn(true);

        assertEquals(Boolean.TRUE, mockRequest.keepAlive());
    }

    @Test
    public void testAttribute() throws Exception {
        Request mockRequest = mockRequest("GET");

        Map<String, Object> attr = new HashMap<>();
        attr.put("name", "biezhi");

        when(mockRequest.attributes()).thenReturn(attr);

        Request request = new HttpRequest(mockRequest);
        assertEquals("biezhi", request.attribute("name"));
    }

    @Test
    public void testFileItems() throws Exception {

        Request mockRequest = mockRequest("GET");

        Map<String, FileItem> attr = new HashMap<>();
        FileItem fileItem = new FileItem("hello.png", "/usr/hello.png", "image/png", 20445L);
        attr.put("img", fileItem);

        when(mockRequest.fileItems()).thenReturn(attr);

        Request request = new HttpRequest(mockRequest);
        FileItem img = request.fileItem("img").get();

        assertNotNull(img);

        assertNull(img.getData());

        assertEquals("hello.png", img.getName());
        assertEquals("/usr/hello.png", img.getFileName());
        assertEquals(Long.valueOf(20445), Optional.of(img.getLength()).get());
        assertEquals("image/png", img.getContentType());

    }

}
