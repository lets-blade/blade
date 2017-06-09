package com.blade;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author biezhi
 *         2017/6/7
 */
public class StaticFileTest extends BaseTestCase {

    @Test
    public void testTxt() throws Exception {
        start(
                app.disableSession()
        );
        assertThat(bodyToString("/static/a.txt"), is("hello blade"));
    }

    @Test
    public void test401() throws Exception {
        start(
                app.before("/static/*", ((request, response) -> response.unauthorized().body("")))
        );
        assertThat(get("/static/a.txt").code(), is(401));
    }

}
