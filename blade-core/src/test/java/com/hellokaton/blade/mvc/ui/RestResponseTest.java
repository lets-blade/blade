package com.hellokaton.blade.mvc.ui;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/20
 */
public class RestResponseTest {

    @Test
    public void testRestResponse() {
        RestResponse<String> restResponse = new RestResponse<>();
        Assert.assertTrue(restResponse.getTimestamp() > 0);

        RestResponse restResponse2 = RestResponse.ok();
        Assert.assertTrue(restResponse2.isSuccess());

        RestResponse restResponse3 = RestResponse.ok("biezhi");
        Assert.assertTrue(restResponse3.isSuccess());
        Assert.assertEquals("biezhi", restResponse3.getPayload());
    }

    @Test
    public void testOk() {
        Assert.assertTrue(RestResponse.ok().isSuccess());
        Assert.assertEquals("Hello", RestResponse.ok("Hello").getPayload());

        RestResponse ok = RestResponse.ok("Hello", 200);
        Assert.assertEquals("Hello", ok.getPayload());
        Assert.assertTrue(ok.isSuccess());
    }

    @Test
    public void testFail() {
        Assert.assertFalse(RestResponse.fail().isSuccess());
        Assert.assertEquals("error", RestResponse.fail("error").getMsg());
        Assert.assertEquals("error", RestResponse.fail(500, "error").getMsg());
    }

}
