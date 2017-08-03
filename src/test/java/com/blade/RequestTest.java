package com.blade;

import com.blade.kit.Assert;
import com.blade.kit.JsonKit;
import com.blade.kit.json.Ason;
import com.blade.mvc.Const;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


/**
 * HttpRequest TestCase
 *
 * @author biezhi
 *         2017/6/3
 */
public class RequestTest extends BaseTestCase {

    @Test
    public void testNullCookie() throws Exception {
        start(app.get("/cookie", (req, res) -> res.text(req.cookie("user-id").orElse("null"))));
        assertThat(bodyToString("/cookie"), is("null"));
    }

    @Test
    public void testSetCookie() throws Exception {
        start(app.get("/cookie", (req, res) -> res.text(req.cookie("user-id").orElse("null"))));
        String body = get("/cookie").header("Cookie", "user-id=221").body();
        assertThat(body, is("221"));
    }

    @Test
    public void testGetCookies() throws Exception {
        start(app.get("/cookie", (req, res) -> res.json(req.cookies())));
        String body = bodyToString("/cookie");
        Ason ason = JsonKit.toAson(body);
        Assert.notNull(ason.toString(), "{}");
    }

    @Test
    public void testMultipleCookies() throws Exception {
        start(app.get("/cookie", (req, res) -> res.json(req.cookies())));
        String body = get("/cookie").header("Cookie", "c1=a;c2=b;c3=c").body();
        Ason ason = JsonKit.toAson(body);
        assertThat(ason.getString("c1"), is("a"));
        assertThat(ason.getString("c2"), is("b"));
        assertThat(ason.getString("c3"), is("c"));
    }

    @Test
    public void testPathParam() throws Exception {
        start(
                app.get("/user1/:id", (req, res) -> res.text(req.pathInt("id").toString()))
                        .get("/user2/:id", (req, res) -> res.text(req.pathLong("id").toString()))
                        .get("/user3/:name/:age", (req, res) -> res.text(req.pathString("name") + ":" + req.pathString("age")))
        );
        assertThat(bodyToString("/user1/24"), is("24"));
        assertThat(bodyToString("/user2/25"), is("25"));
        assertThat(bodyToString("/user3/jack/18"), is("jack:18"));
    }

    @Test
    public void testPathParams() throws Exception {
        start(
                app.get("/user1/:id", (req, res) -> res.json(req.pathParams()))
                        .get("/user2/:name/:age", (req, res) -> res.json(req.pathParams()))
        );

        String body1 = get("/user1/10").body();
        Ason ason1 = JsonKit.toAson(body1);
        assertThat(ason1.getString("id"), is("10"));

        String body2 = get("/user2/biezhi/20").body();
        Ason ason2 = JsonKit.toAson(body2);

        assertThat(ason2.getString("age"), is("20"));
        assertThat(ason2.getString("name"), is("biezhi"));
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
        assertThat(bodyToString("/a"), is("/a"));
        assertThat(bodyToString("/a/b"), is("/a/b"));
        assertThat(bodyToString("/a/b/c?name=q1"), is("/a/b/c"));
    }

    @Test
    public void testUrl() throws Exception {
        start(
                app.get("/hello", (request, response) -> response.text(request.url()))
        );
        assertThat(bodyToString("/hello?name=q1"), is("/hello?name=q1"));
    }

    @Test
    public void testUserAgent() throws Exception {
        start(
                app.get("/", (request, response) -> response.text(request.userAgent()))
        );
        String body = get("/").userAgent(firefoxUA).body();
        assertThat(firefoxUA, is(firefoxUA));
    }

    @Test
    public void testProtocol() throws Exception {
        start(
                app.get("/", (request, response) -> response.text(request.protocol()))
        );
        assertThat(bodyToString("/"), is("HTTP/1.1"));
    }

    @Test
    public void testQueryString() throws Exception {
        start(
                app.get("/hello", (request, response) -> response.text(request.queryString()))
        );
        assertThat(bodyToString("/hello?name=q1"), is("/hello?name=q1"));
    }

    @Test
    public void testQueryParam() throws Exception {
        start(
                app.get("/query", (request, response) -> response.text(request.query("name", "jack")))
                        .post("/query", (request, response) -> response.text(request.query("name", "jack")))
                        .get("/query2", (request, response) -> response.text(request.queryDouble("price", 10.2D) + ""))
        );

        assertThat(bodyToString("/query?name=rose"), is("rose"));
        assertThat(bodyToString("/query"), is("jack"));
        assertThat(bodyToString("/query2?price=22.1"), is("22.1"));
        assertThat(bodyToString("/query2"), is("10.2"));

        String tom = post("/query?name=tom").body();
        String biezhi = post("/query").form("name", "biezhi").body();
        assertThat(tom, is("tom"));
        assertThat(biezhi, is("biezhi"));

    }

    @Test
    public void testHttpMethod() throws Exception {
        start(
                app.get("/", (request, response) -> response.text(request.method()))
                        .post("/", (request, response) -> response.text(request.method()))
                        .put("/", (request, response) -> response.text(request.method()))
                        .delete("/", (request, response) -> response.text(request.method()))
        );

        assertThat(bodyToString("/"), is("GET"));
        assertThat(post("/").body(), is("POST"));
        assertThat(put("/").body(), is("PUT"));
        assertThat(delete("/").body(), is("DELETE"));
    }

    @Test
    public void testAddress() throws Exception {
        start(
                app.get("/", (request, response) -> response.text(request.address()))
        );

        assertThat(bodyToString("/"), is("127.0.0.1"));
    }

    @Test
    public void testContentType() throws Exception {
        start(
                app.get("/c1", (request, response) -> response.text(response.contentType()))
                        .get("/c2", (request, response) -> response.contentType("application/json; charset=UTF-8").text(response.contentType()))
        );

        MatcherAssert.assertThat(bodyToString("/c1"), Matchers.is(Const.CONTENT_TYPE_HTML));
        assertThat(bodyToString("/c2"), is(Const.CONTENT_TYPE_JSON));
    }

    @Test
    public void testIsSecure() throws Exception {
        start(
                app.get("/", (request, response) -> response.text(request.isSecure() + ""))
        );

        assertThat(bodyToString("/"), is("false"));
    }

    @Test
    public void testIsAjax() throws Exception {
        start(
                app.get("/a1", (request, response) -> response.text(request.isAjax() + ""))
                        .get("/a2", (request, response) -> response.text(request.isAjax() + ""))
        );

        assertThat(bodyToString("/a1"), is("false"));
        assertThat(get("/a2").header("x-requested-with", "XMLHttpRequest").body(), is("true"));
    }

    @Test
    public void testIsIE() throws Exception {
        start(
                app.get("/", (request, response) -> response.text(request.isIE() + ""))
        );

        assertThat(get("/").userAgent(firefoxUA).body(), is("false"));
        assertThat(get("/").userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)").body(), is("true"));
    }

    @Test
    public void testHeaders() throws Exception {
        start(
                app.get("/", (request, response) -> response.json(request.headers()))
        );

        String body = get("/")
                .header("h1", "a1").header("h2", "a2").header("h3", "a3")
                .body();

        Ason ason = JsonKit.toAson(body);

        assertThat(ason.getString("h1"), is("a1"));
        assertThat(ason.getString("h2"), is("a2"));
        assertThat(ason.getString("h3"), is("a3"));
    }

    @Test
    public void testHeader() throws Exception {
        start(
                app.get("/", (request, response) -> response.json(request.header("h1")))
        );

        String body = get("/")
                .header("h1", "a1").header("h2", "a2").header("h3", "a3")
                .body();

        assertThat(body, is("a1"));
    }

    @Test
    public void testKeepAlive() throws Exception {
        start(
                app.get("/", (request, response) -> response.json(request.keepAlive() + ""))
        );

        assertThat(bodyToString("/"), is("true"));
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
                app.post("/upload1", (request, response) -> {
                    response.json(request.fileItems());
                })
                        .post("/upload2", (request, response) -> {
                            response.json(request.fileItem("file1").orElse(null));
                        })
        );

        String body = post("/upload1").part("file1", "a.txt", new File(Const.CLASSPATH + File.separator + "log_config.txt")).body();
        assertThat(body, is("{\"file1\":{\"name\":\"file1\",\"fileName\":\"a.txt\",\"contentType\":\"text/plain\",\"length\":1551}}"));

        body = post("/upload2").part("file1", "a.txt", new File(Const.CLASSPATH + File.separator + "log_config.txt")).body();
        assertThat(body, is("{\"name\":\"file1\",\"fileName\":\"a.txt\",\"contentType\":\"text/plain\",\"length\":1551}"));
    }

}
