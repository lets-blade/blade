package com.blade.mvc;

import com.blade.BaseTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author biezhi
 *         2017/6/7
 */
public class StaticFileTest extends BaseTestCase {

    @Test
    public void testTxt() throws Exception {
        start(
                app.before("/static/*", ((request, response) -> response.unauthorized().body("")))
        );
        String body =getBodyString("/static/a.txt");
        assertEquals("hello blade", body);
    }

}
