package com.blade;

import com.blade.mvc.Const;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * HttpResponse TestCase
 *
 * @author biezhi
 *         2017/6/3
 */
public class ResponseTest extends BaseTestCase {

    @Test
    public void testStatus() throws Exception {
        start(
                app.get("/ok", ((request, response) -> {
                }))
                        .get("/noOk", ((request, response) -> response.status(666)))
        );
        int code1 = get("/ok").code();
        int code2 = get("/noOk").code();
        assertEquals(200, code1);
        assertEquals(666, code2);
    }

    @Test
    public void testBadRequest() throws Exception {
        start(
                app.get("/", ((request, response) -> response.badRequest()))
        );
        int code = get("/").code();
        assertEquals(400, code);
    }

    @Test
    public void testUnauthorized() throws Exception {
        start(
                app.get("/", ((request, response) -> response.unauthorized()))
        );
        int code = get("/").code();
        assertEquals(401, code);
    }

    @Test
    public void testNotFound() throws Exception {
        start(
                app.get("/", ((request, response) -> response.notFound()))
        );
        int code = get("/").code();
        assertEquals(404, code);
    }

    @Test
    public void testContentType() throws Exception {
        start(
                app.get("/c1", ((request, response) -> response.text(response.contentType())))
                        .get("/c2", ((request, response) -> response.contentType("a/b").text(response.contentType())))
        );
        String c1 = bodyToString("/c1");
        String c2 = bodyToString("/c2");
        assertEquals(Const.CONTENT_TYPE_HTML, c1);
        assertEquals("a/b", c2);
    }

    @Test
    public void testHeaders() throws Exception {
        start(
                app.get("/", ((request, response) -> response.json(response.headers())))
        );
        String headers = bodyToString("/");
        assertEquals("{}", headers);
    }

    @Test
    public void testHeader() throws Exception {
        start(
                app.get("/", ((request, response) -> {
                    response.header("a", "value1").json(response.headers());
                }))
        );
        String headers = bodyToString("/");
        assertEquals("{\"a\":\"value1\"}", headers);
    }

    @Test
    public void testCookie() throws Exception {
        start(
                app.get("/", ((request, response) -> {
                    response.cookie("c1", "value1").json(response.cookies());
                })).disableSession()
        );
        String headers = bodyToString("/");
        assertEquals("{\"c1\":\"value1\"}", headers);
    }

    @Test
    public void testText() throws Exception {
        start(
                app.get("/", ((request, response) -> response.text("hello blade")))
        );
        String text = bodyToString("/");
        assertEquals("hello blade", text);

        String contentType = get("/").contentType();
        assertEquals(Const.CONTENT_TYPE_TEXT, contentType);
    }

    @Test
    public void testHtml() throws Exception {
        start(
                app.get("/", ((request, response) -> response.html("<h1>hello blade</h1>")))
        );
        String text = bodyToString("/");
        assertEquals("<h1>hello blade</h1>", text);

        String contentType = get("/").contentType();
        assertEquals(Const.CONTENT_TYPE_HTML, contentType);
    }

    @Test
    public void testJson() throws Exception {
        start(
                app.get("/json1", ((request, response) -> response.json(Arrays.asList(1, 2, 3))))
                        .get("/json2", ((request, response) -> response.json("[4,5,6]")))
        );

        assertEquals("[1,2,3]", bodyToString("/json1"));
        assertEquals("[4,5,6]", bodyToString("/json2"));

        assertEquals(Const.CONTENT_TYPE_JSON, get("/json1").contentType());
        assertEquals(Const.CONTENT_TYPE_JSON, get("/json2").contentType());
    }

    @Test
    public void testRender() throws Exception {
        start(
                app.get("/", ((request, response) -> {
                    request.attribute("user", "biezhi");
                    response.render("test.html");
                }))
        );
        String html = bodyToString("/");
        assertEquals("user is biezhi", html);

        String contentType = get("/").contentType();
        assertEquals(Const.CONTENT_TYPE_HTML, contentType);
    }

    @Test
    public void testRedirect() throws Exception {
        start(
                app.get("/", ((request, response) -> response.redirect("http://jd.com")))
        );

        String contentType = get("/").body();
        System.out.println(contentType);
//        assertEquals(Const.CONTENT_TYPE_HTML, contentType);
    }

}
