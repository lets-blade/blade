package com.hellokaton.blade.mvc;

import com.hellokaton.blade.BaseTestCase;
import com.hellokaton.blade.kit.CaseInsensitiveHashMap;
import com.hellokaton.blade.mvc.http.Cookie;
import com.hellokaton.blade.mvc.http.HttpMethod;
import com.hellokaton.blade.mvc.http.HttpRequest;
import com.hellokaton.blade.mvc.http.Request;
import com.hellokaton.blade.mvc.multipart.FileItem;
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
public class HttpRequestTest extends BaseTestCase {

    @Test
    public void testMethod() {
        Assert.assertEquals(mockHttpRequest("GET").method(), "GET");
        Assert.assertEquals(mockHttpRequest("GET").httpMethod(), HttpMethod.GET);

        Assert.assertEquals(mockHttpRequest("POST").method(), "POST");
        Assert.assertEquals(mockHttpRequest("POST").httpMethod(), HttpMethod.POST);

        Assert.assertEquals(mockHttpRequest("PUT").method(), "PUT");
        Assert.assertEquals(mockHttpRequest("PUT").httpMethod(), HttpMethod.PUT);

        Assert.assertEquals(mockHttpRequest("DELETE").method(), "DELETE");
        Assert.assertEquals(mockHttpRequest("DELETE").httpMethod(), HttpMethod.DELETE);

        Assert.assertEquals(mockHttpRequest("BEFORE").method(), "BEFORE");
        Assert.assertEquals(mockHttpRequest("BEFORE").httpMethod(), HttpMethod.BEFORE);

        Assert.assertEquals(mockHttpRequest("AFTER").method(), "AFTER");
        Assert.assertEquals(mockHttpRequest("AFTER").httpMethod(), HttpMethod.AFTER);
    }

    @Test
    public void testHost() {
        HttpRequest request = mockHttpRequest("GET");
        when(request.host()).thenReturn("127.0.0.1");
        Assert.assertEquals(request.host(), "127.0.0.1");

        when(request.host()).thenReturn("localhost");
        Assert.assertEquals(request.host(), "localhost");
    }

    @Test
    public void testCookie() {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie c1 = new Cookie();
        c1.name("c1");
        c1.value("hello1");
        cookieMap.put("c1", c1);

        Cookie c2 = new Cookie();
        c2.name("c1");
        c2.value("hello1");
        c2.httpOnly(true);
        cookieMap.put("c2", c2);

        Cookie c3 = new Cookie();
        c3.name("c3");
        c3.value("hello3");
        c3.secure(false);
        cookieMap.put("c3", c3);

        Cookie c4 = new Cookie();
        c4.name("c4");
        c4.value("hello4");
        c4.domain("www.github.com");
        c4.path("/github");
        cookieMap.put("c4", c4);

        HttpRequest request = mockHttpRequest("GET");
        when(request.cookies()).thenReturn(cookieMap);
        when(request.cookie("c1")).thenReturn(cookieMap.get("c1").value());
        when(request.cookieRaw("c2")).thenReturn(cookieMap.get("c2"));
        when(request.cookieRaw("c3")).thenReturn(cookieMap.get("c3"));
        when(request.cookieRaw("c4")).thenReturn(cookieMap.get("c4"));

        Assert.assertEquals(request.cookies(), cookieMap);
        Assert.assertEquals(request.cookies().size(), cookieMap.size());

        Assert.assertEquals(request.cookie("c1"), "hello1");
        Assert.assertTrue(request.cookieRaw("c2").httpOnly());
        Assert.assertFalse(request.cookieRaw("c3").secure());
        Assert.assertEquals(request.cookieRaw("c3").path(), "/");
        Assert.assertEquals(request.cookieRaw("c4").domain(), "www.github.com");
        Assert.assertEquals(request.cookieRaw("c4").path(), "/github");
    }

    @Test
    public void testPathParam() {
        Request mockRequest = mockHttpRequest("GET");

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
        Request mockRequest = mockHttpRequest("GET");
        when(mockRequest.url()).thenReturn("/a");

        Request request = new HttpRequest(mockRequest);

        assertEquals("/a", request.uri());

        when(mockRequest.url()).thenReturn("/a/b?username=jack");
        request = new HttpRequest(mockRequest);

        assertEquals("/a/b", request.uri());
    }

    @Test
    public void testUrl() {
        Request mockRequest = mockHttpRequest("GET");

        when(mockRequest.url()).thenReturn("/hello?name=q1");

        assertEquals("/hello?name=q1", mockRequest.url());
    }

    @Test
    public void testUserAgent() {
        Map<String, List<String>> headers = Collections.singletonMap("User-Agent", Collections.singletonList(firefoxUA));

        Request mockRequest = mockHttpRequest("GET");
        when(mockRequest.headers()).thenReturn(headers);

        Request request = new HttpRequest(mockRequest);
        assertEquals(firefoxUA, request.userAgent());
    }

    @Test
    public void testProtocol() {
        Request mockRequest = mockHttpRequest("GET");
        when(mockRequest.protocol()).thenReturn("HTTP/1.1");
        assertEquals("HTTP/1.1", mockRequest.protocol());
    }

    @Test
    public void testQueryString() {
        Request mockRequest = mockHttpRequest("GET");
        when(mockRequest.url()).thenReturn("/hello?name=q1");

        Request request = new HttpRequest(mockRequest);
        assertEquals("name=q1", request.queryString());
    }

    @Test
    public void testQueryParam() {

        Request mockRequest = mockHttpRequest("GET");

        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("name", Collections.singletonList("jack"));
        queryParams.put("price", Collections.singletonList("22.1"));
        queryParams.put("age", Collections.singletonList("25"));
        queryParams.put("id", Collections.singletonList("220291"));

        when(mockRequest.queryParams()).thenReturn(queryParams);

        Request request = new HttpRequest(mockRequest);

        assertEquals("jack", request.query("name").get());
        assertEquals(Double.valueOf(22.1), request.queryDouble("price").get());
        assertEquals(Long.valueOf(220291), request.queryLong("id").get());
        assertEquals(Integer.valueOf(25), request.queryInt("age").get());
    }

    @Test
    public void testFormParam() {

        Request mockRequest = mockHttpRequest("GET");

        Map<String, List<String>> formParams = new HashMap<>();
        formParams.put("name", Collections.singletonList("jack"));
        formParams.put("price", Collections.singletonList("22.1"));
        formParams.put("age", Collections.singletonList("25"));
        formParams.put("id", Collections.singletonList("220291"));

        when(mockRequest.formParams()).thenReturn(formParams);

        Request request = new HttpRequest(mockRequest);

        assertEquals("jack", request.form("name").get());
        assertEquals(Double.valueOf(22.1), request.formDouble("price").get());
        assertEquals(Long.valueOf(220291), request.formLong("id").get());
        assertEquals(Integer.valueOf(25), request.formInt("age").get());
    }

    @Test
    public void testAddress() {
        Request mockRequest = mockHttpRequest("GET");
        when(mockRequest.address()).thenReturn("127.0.0.1");

        assertEquals("127.0.0.1", mockRequest.address());
    }

    @Test
    public void testContentType() {

        Request mockRequest = mockHttpRequest("GET");
        when(mockRequest.contentType()).thenReturn(HttpConst.CONTENT_TYPE_HTML);

        assertEquals(HttpConst.CONTENT_TYPE_HTML, mockRequest.contentType());

        when(mockRequest.contentType()).thenReturn(HttpConst.CONTENT_TYPE_JSON);
        assertEquals(HttpConst.CONTENT_TYPE_JSON, mockRequest.contentType());
    }

    @Test
    public void testIsSecure() {
        Request mockRequest = mockHttpRequest("GET");
        when(mockRequest.isSecure()).thenReturn(false);

        assertEquals(Boolean.FALSE, mockRequest.isSecure());
    }

    @Test
    public void testIsAjax() {

        Request mockRequest = mockHttpRequest("GET");
        Map<String, List<String>> headers = Collections.singletonMap("x-requested-with", Collections.singletonList("XMLHttpRequest"));
        when(mockRequest.headers()).thenReturn(headers);

        Request request = new HttpRequest(mockRequest);

        assertEquals(Boolean.TRUE, request.isAjax());

        when(mockRequest.headers()).thenReturn(Collections.EMPTY_MAP);
        request = new HttpRequest(mockRequest);

        assertEquals(Boolean.FALSE, request.isAjax());

    }

    @Test
    public void testIsIE() {
        Request mockRequest = mockHttpRequest("GET");
        List<String> uaList = Collections.singletonList("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
        Map<String, List<String>> headers = Collections.singletonMap("User-Agent", uaList);
        when(mockRequest.headers()).thenReturn(headers);

        Request request = new HttpRequest(mockRequest);

        assertEquals(Boolean.TRUE, request.isIE());

        when(mockRequest.headers()).thenReturn(Collections.EMPTY_MAP);
        request = new HttpRequest(mockRequest);

        assertEquals(Boolean.FALSE, request.isIE());
    }

    @Test
    public void testHeaders() {
        Request mockRequest = mockHttpRequest("GET");
        Map<String, List<String>> headers = new CaseInsensitiveHashMap<>();
        headers.put("h1", Collections.singletonList("a1"));
        headers.put("H2", Collections.singletonList("a2"));

        when(mockRequest.headers()).thenReturn(headers);

        Request request = new HttpRequest(mockRequest);

        assertEquals("a1", request.header("h1"));
        assertEquals("a1", request.header("H1"));
        assertEquals("a2", request.header("h2"));
        assertEquals("a2", request.header("H2"));
        request.headers().forEach((key, val) -> System.out.println(key + "\t=\t" + val));
    }

    @Test
    public void testKeepAlive() {
        Request mockRequest = mockHttpRequest("GET");
        when(mockRequest.keepAlive()).thenReturn(true);

        assertEquals(Boolean.TRUE, mockRequest.keepAlive());
    }

    @Test
    public void testAttribute() {
        Request mockRequest = mockHttpRequest("GET");

        Map<String, Object> attr = new HashMap<>();
        attr.put("name", "biezhi");

        when(mockRequest.attributes()).thenReturn(attr);

        Request request = new HttpRequest(mockRequest);
        assertEquals("biezhi", request.attribute("name"));
    }

    @Test
    public void testFileItems() {

        Request mockRequest = mockHttpRequest("GET");

        Map<String, FileItem> attr = new HashMap<>();
        FileItem fileItem = new FileItem();
        fileItem.setName("file");
        fileItem.setFileName("hello.png");
        fileItem.setPath("/usr/hello.png");
        fileItem.setContentType("image/png");
        fileItem.setLength(20445L);

        attr.put("img", fileItem);

        when(mockRequest.fileItems()).thenReturn(attr);

        Request request = new HttpRequest(mockRequest);

        FileItem img = request.fileItem("img").get();

        assertNotNull(img);

        assertNull(img.getFile());

        assertEquals("file", img.getName());
        assertEquals("hello.png", img.getFileName());
        assertEquals("/usr/hello.png", img.getPath());
        assertEquals(Long.valueOf(20445), Optional.of(img.getLength()).get());
        assertEquals("image/png", img.getContentType());

    }

}
