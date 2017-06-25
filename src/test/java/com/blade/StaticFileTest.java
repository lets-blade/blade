package com.blade;

import org.junit.Test;

import java.io.StringReader;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author biezhi
 *         2017/6/7
 */
public class StaticFileTest extends BaseTestCase {

    @Test
    public void test401() throws Exception {
        start(
                app.before("/static/*", ((request, response) -> response.unauthorized().body("")))
        );
        assertThat(get("/static/a.txt").code(), is(401));
    }

    @Test
    public void testTxt() throws Exception {
        start(app.disableSession());
//        String txtBody = get("/static/a.txt").bufferedReader().lines().collect(Collectors.joining());
//        assertThat(bodyToString("/static/a.txt"), is("hello blade"));
    }

}
