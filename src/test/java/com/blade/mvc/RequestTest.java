package com.blade.mvc;

import com.blade.BaseTestCase;
import com.blade.kit.JsonKit;
import com.blade.kit.json.Ason;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * HttpRequest TestCase
 *
 * @author biezhi
 * 2017/6/3
 */
public class RequestTest extends BaseTestCase {

    @Test
    public void testNullCookie() throws Exception {
        start(app.get("/cookie", (req, res) -> res.text(req.cookie("user-id").orElse("null"))));
        assertEquals("null", bodyToString("/cookie"));
    }

    @Test
    public void testSetCookie() throws Exception {
        start(app.get("/cookie", (req, res) -> res.text(req.cookie("user-id").orElse("null"))));
        String body = get("/cookie").header("Cookie", "user-id=221").asString().getBody();
        assertEquals("221", body);
    }

    @Test
    public void testGetCookies() throws Exception {
        start(app.get("/cookie", (req, res) -> res.json(req.cookies())));
        String body = bodyToString("/cookie");
        Ason   ason = JsonKit.toAson(body);
        assertEquals("{}", ason.toString());
    }

    @Test
    public void testMultipleCookies() throws Exception {
        start(app.get("/cookie", (req, res) -> res.json(req.cookies())));
        String body = get("/cookie").header("Cookie", "c1=a;c2=b;c3=c").asString().getBody();
        Ason   ason = JsonKit.toAson(body);
        assertEquals("a", ason.getString("c1"));
        assertEquals("b", ason.getString("c2"));
        assertEquals("c", ason.getString("c3"));
    }

    @Test
    public void testPathParam() throws Exception {
        start(
                app.get("/user1/:id", (req, res) -> res.text(req.pathInt("id").toString()))
                        .get("/user2/:id", (req, res) -> res.text(req.pathLong("id").toString()))
                        .get("/user3/:name/:age", (req, res) -> res.text(req.pathString("name") + ":" + req.pathString("age")))
        );
        assertEquals("24", bodyToString("/user1/24"));
        assertEquals("25", bodyToString("/user2/25"));
        assertEquals("jack:18", bodyToString("/user3/jack/18"));
    }

    @Test
    public void testPathParams() throws Exception {
        start(
                app.get("/user1/:id", (req, res) -> res.json(req.pathParams()))
                        .get("/user2/:name/:age", (req, res) -> res.json(req.pathParams()))
        );

        String body1 = getBodyString("/user1/10");
        Ason   ason1 = JsonKit.toAson(body1);
        assertEquals("10", ason1.getString("id"));

        String body2 = getBodyString("/user2/biezhi/20");
        Ason   ason2 = JsonKit.toAson(body2);

        assertEquals("20", ason2.getString("age"));
        assertEquals("biezhi", ason2.getString("name"));
    }

    @Test
    public void testHost() throws Exception {
        start(
                app.get("/", (request, response) -> System.out.println(request.host()))
        );
        bodyToString("/");
    }

    @Test
    public void testUri() throws Exception {
        start(
                app.get("/a", (request, response) -> response.text(request.uri()))
                        .get("/a/b", (request, response) -> response.text(request.uri()))
                        .get("/a/b/c", (request, response) -> response.text(request.uri()))
        );
        assertEquals("/a", bodyToString("/a"));
        assertEquals("/a/b", bodyToString("/a/b"));
        assertEquals("/a/b/c", bodyToString("/a/b/c?name=q1"));
    }

    @Test
    public void testUrl() throws Exception {
        start(
                app.get("/hello", (request, response) -> response.text(request.url()))
        );
        assertEquals("/hello?name=q1", bodyToString("/hello?name=q1"));
    }

    @Test
    public void testUserAgent() throws Exception {
        start(
                app.get("/", (request, response) -> response.text(request.userAgent()))
        );
        String body = get("/").header("User-Agent", firefoxUA).asString().getBody();
        assertEquals(firefoxUA, body);
    }

    @Test
    public void testProtocol() throws Exception {
        start(
                app.get("/", (request, response) -> response.text(request.protocol()))
        );
        assertEquals("HTTP/1.1", bodyToString("/"));
    }

    @Test
    public void testQueryString() throws Exception {
        start(
                app.get("/hello", (request, response) -> response.text(request.queryString()))
        );
        assertEquals("/hello?name=q1", bodyToString("/hello?name=q1"));
    }

    @Test
    public void testQueryParam() throws Exception {
        start(
                app.get("/query", (request, response) -> response.text(request.query("name", "jack")))
                        .post("/query", (request, response) -> response.text(request.query("name", "jack")))
                        .get("/query2", (request, response) -> response.text(request.queryDouble("price", 10.2D) + ""))
        );

        assertEquals("rose", bodyToString("/query?name=rose"));
        assertEquals("jack", bodyToString("/query"));
        assertEquals("22.1", bodyToString("/query2?price=22.1"));
        assertEquals("10.2", bodyToString("/query2"));

        String tom    = postBodyString("/query?name=tom");
        String biezhi = post("/query").queryString("name", "biezhi").asString().getBody();
        assertEquals("tom", tom);
        assertEquals("biezhi", biezhi);

    }

    @Test
    public void testHttpMethod() throws Exception {
        start(
                app.get("/", (request, response) -> response.text(request.method()))
                        .post("/", (request, response) -> response.text(request.method()))
                        .put("/", (request, response) -> response.text(request.method()))
                        .delete("/", (request, response) -> response.text(request.method()))
        );

        assertEquals("GET", bodyToString("/"));
        assertEquals("POST", postBodyString("/"));
        assertEquals("PUT", putBodyString("/"));
        assertEquals("DELETE", deleteBodyString("/"));
    }

    @Test
    public void testAddress() throws Exception {
        start(
                app.get("/", (request, response) -> response.text(request.address()))
        );
        assertEquals("127.0.0.1", bodyToString("/"));
    }

    @Test
    public void testContentType() throws Exception {
        start(
                app.get("/c1", (request, response) -> response.text(response.contentType()))
                        .get("/c2", (request, response) -> response.contentType("application/json; charset=UTF-8").text(response.contentType()))
        );

        assertEquals(Const.CONTENT_TYPE_HTML, bodyToString("/c1"));
        assertEquals(Const.CONTENT_TYPE_JSON, bodyToString("/c2"));
    }

    @Test
    public void testIsSecure() throws Exception {
        start(
                app.get("/", (request, response) -> response.text(request.isSecure() + ""))
        );

        assertEquals("false", bodyToString("/"));
    }

    @Test
    public void testIsAjax() throws Exception {
        start(
                app.get("/a1", (request, response) -> response.text(request.isAjax() + ""))
                        .get("/a2", (request, response) -> response.text(request.isAjax() + ""))
        );

        assertEquals("false", bodyToString("/a1"));

        String body = get("/a2").header("x-requested-with", "XMLHttpRequest").asString().getBody();
        assertEquals("true", body);
    }

    @Test
    public void testIsIE() throws Exception {
        start(
                app.get("/", (request, response) -> response.text(request.isIE() + ""))
        );

        assertEquals("false", get("/").header("User-Agent", firefoxUA).asString().getBody());
        assertEquals("true", get("/").header("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)").asString().getBody());
    }

    @Test
    public void testHeaders() throws Exception {
        start(
                app.get("/", (request, response) -> response.json(request.headers()))
        );

        String body = get("/")
                .header("h1", "a1").header("h2", "a2").header("h3", "a3")
                .asString().getBody();

        Ason ason = JsonKit.toAson(body);

        assertEquals("a1", ason.getString("h1"));
        assertEquals("a2", ason.getString("h2"));
        assertEquals("a3", ason.getString("h3"));
    }

    @Test
    public void testHeader() throws Exception {
        start(
                app.get("/", (request, response) -> response.json(request.header("h1")))
        );

        String body = get("/")
                .header("h1", "a1").header("h2", "a2").header("h3", "a3")
                .asString().getBody();

        assertEquals("a1", body);
    }

    @Test
    public void testKeepAlive() throws Exception {
        start(
                app.get("/", (request, response) -> response.json(request.keepAlive() + ""))
        );

        assertEquals("true", bodyToString("/"));
    }

    @Test
    public void testAttribute() throws Exception {
        start(
                app.get("/", (request, response) -> request.attribute("name", "jack"))
                        .after("*", ((request, response) -> {
                            System.out.println(request.attribute("name").toString());
                        }))
        );
        bodyToString("/");
    }

    @Test
    public void testFileItems() throws Exception {
        start(
                app.post("/upload1", (request, response) -> response.json(request.fileItems()))
                        .post("/upload2", (request, response) -> response.json(request.fileItem("file1").orElse(null)))
        );

        String body = post("/upload1")
                .field("file1", new File(RequestTest.class.getResource("/log_config.txt").getPath()))
                .asString().getBody();

        assertEquals("{\"file1\":{\"name\":\"file1\",\"fileName\":\"log_config.txt\",\"contentType\":\"text/plain\",\"length\":1551}}", body);

        body = post("/upload2")
                .field("file1", new File(RequestTest.class.getResource("/log_config.txt").getPath()))
                .asString().getBody();

        assertEquals("{\"name\":\"file1\",\"fileName\":\"log_config.txt\",\"contentType\":\"text/plain\",\"length\":1551}", body);
    }

}
