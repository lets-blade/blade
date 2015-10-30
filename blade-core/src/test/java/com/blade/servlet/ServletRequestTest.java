package com.blade.servlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;

import com.blade.MockController;
import com.blade.http.HttpMethod;
import com.blade.http.Request;
import com.blade.http.Response;
import com.blade.route.Route;

public class ServletRequestTest {

    @Test
    public void testRetrieveHost() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getServerName()).thenReturn("localhost");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.host(), "localhost");
    }

    @Test
    public void testRetrievePath() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getContextPath()).thenReturn("/context");
        when(servletRequest.getRequestURI()).thenReturn("/users/edit/1");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.path(), "/users/edit/1");
    }

    @Test
    public void testRetrievePathVariables() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getContextPath()).thenReturn("");
        when(servletRequest.getRequestURI()).thenReturn("/users/1/edit/th1s1s4hAsh/");

        Route route = new Route(HttpMethod.GET, "/users/:userId/edit/:hash", new MockController(),
                MockController.class.getMethod("init", Request.class, Response.class));
        Request request = new ServletRequest(servletRequest);
        request.setRoute(route);

        Map<String, String> pathVariables = request.pathParams();
        Assert.assertNotNull(pathVariables);
        Assert.assertEquals(pathVariables.size(), 2);
        Assert.assertEquals(pathVariables.get("userId"), "1");
        Assert.assertEquals(pathVariables.get("hash"), "th1s1s4hAsh");
    }

    @Test
    public void testNotRetrieveNonExistingPathVariable() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getContextPath()).thenReturn("");
        when(servletRequest.getRequestURI()).thenReturn("/users/1/edit/th1s1s4hAsh");

        Route route = new Route(HttpMethod.GET, "/users/1/edit/th1s1s4hAsh", new MockController(),
                MockController.class.getMethod("init", Request.class, Response.class));
        Request request = new ServletRequest(servletRequest);
        request.setRoute(route);

        Map<String, String> pathVariables = request.pathParams();
        Assert.assertNotNull(pathVariables);
        Assert.assertEquals(pathVariables.size(), 0);
    }

    @Test
    public void testRetrieveQueryString() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getQueryString()).thenReturn("method=test&action=success");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.queryString(), "method=test&action=success");
    }

    @Test
    public void testRetrieveParams() throws Exception {
        Map<String, String[]> mockParams = new HashMap<String, String[]>();
        mockParams.put("param1", new String[]{"value1"});
        mockParams.put("param2", new String[]{"val1", "val2"});

        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getParameterMap()).thenReturn(mockParams);

        Request request = new ServletRequest(servletRequest);

        Map<String, String> params = request.querys();
        Assert.assertNotNull(params);
        Assert.assertEquals(params.size(), 2);

        String param1 = params.get("param1");
        Assert.assertNotNull(param1);
        Assert.assertEquals(param1, "value1");

        String param2 = params.get("param2");
        Assert.assertNotNull(param2);
        Assert.assertEquals(param2, "val1,val2");

        Assert.assertNull(params.get("notexistnet"));
    }

    @Test
    public void testRetrieveStringParam() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getParameterValues("param1")).thenReturn(new String[]{"value1"});

        Request request = new ServletRequest(servletRequest);

        Assert.assertNotNull(request.query("param1"));
        Assert.assertEquals(request.query("param1"), "value1");
    }

    @Test
    public void testNotRetrieveNonExistingParam() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        Request request = new ServletRequest(servletRequest);
        Assert.assertNull(request.query("nonexisting"));
    }

    @Test
    public void testRetrieveLongParam() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getParameterValues("param1")).thenReturn(new String[]{"1"});

        Request request = new ServletRequest(servletRequest);

        Assert.assertNotNull(request.query("param1"));
        Assert.assertEquals(request.query("param1"), "1");
    }

    @Test
    public void testRetrieveUrl() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://www.google.com:81/test"));

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.url(), "http://www.google.com:81/test");
    }

    @Test
    public void testRetrieveMethod() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest("GET");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.method(), "GET");
    }

    @Test
    public void testRetrieveRemoteAddress() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getRemoteAddr()).thenReturn("localhost");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.address(), "localhost");
    }

    @Test
    public void testRetrieveContentType() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getContentType()).thenReturn("application/json");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.contentType(), "application/json");
    }

    @Test
    public void testRetrievePort() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getServerPort()).thenReturn(1);

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.port(), 1);
    }

    @Test
    public void testRetrieveIsSecure() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.isSecure()).thenReturn(true);

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.isSecure(), true);
    }

    @Test
    public void testRetrieveIsAjax() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getHeader("x-requested-with")).thenReturn("XMLHttpRequest");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.isAjax(), true);

        when(servletRequest.getHeader("x-requested-with")).thenReturn(null);
        Assert.assertEquals(request.isAjax(), false);

        when(servletRequest.getHeader("x-requested-with")).thenReturn("Another");
        Assert.assertEquals(request.isAjax(), false);
    }

    @Test
    public void testRetrieveCookie() throws Exception {
        javax.servlet.http.Cookie[] servletCookies = {new javax.servlet.http.Cookie("test-1", "1")};

        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getCookies()).thenReturn(servletCookies);

        Request request = new ServletRequest(servletRequest);
        Map<String, Cookie> cookies = request.cookies();

        Assert.assertEquals(cookies.size(), 1);

        Cookie cookie = cookies.get("test-1");
        Assert.assertNotNull(cookie);
        Assert.assertEquals(cookie.getValue(), "1");
        Assert.assertEquals(cookie.getMaxAge(), -1);
        Assert.assertNull(cookie.getDomain());
        Assert.assertNull(cookie.getPath());

        Assert.assertNotNull(request.cookie("test-1"));
        Assert.assertNull(request.cookie("not-existent"));
    }

    @Test
    public void testRetrieveEmptyCookies() throws Exception {
        javax.servlet.http.Cookie[] servletCookies = {};

        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getCookies()).thenReturn(servletCookies);

        Request request = new ServletRequest(servletRequest);
        Map<String, Cookie> cookies = request.cookies();

        Assert.assertEquals(cookies.size(), 0);
    }

    @Test
    public void testRetrieveHeader() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getHeader("Authorization")).thenReturn("Basic ...");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.header("Authorization"), "Basic ...");
    }


    private HttpServletRequest mockServletRequest() {
        return mockServletRequest("GET");
    }

    private HttpServletRequest mockServletRequest(String methodName) {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getMethod()).thenReturn(methodName);

        return servletRequest;
    }

    private static void fixMultipartLineSeparators(String fileName) throws IOException {
        File file = new File("src/test/resources/multipart/" + fileName + "-fixed.txt");
        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(ServletRequestTest.class.getResourceAsStream("/multipart/" + fileName + ".txt")));
            writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "\r\n");
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        fixMultipartLineSeparators("single-file");
        fixMultipartLineSeparators("multiple-files");
    }

}
