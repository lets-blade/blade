package com.blade.mvc.ui;

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
        Assert.assertEquals(true, restResponse.getTimestamp() > 0);

        RestResponse restResponse2 = new RestResponse<>(true);
        Assert.assertEquals(true, restResponse2.isSuccess());

        RestResponse restResponse3 = new RestResponse<>(true, "biezhi");
        Assert.assertEquals(true, restResponse3.isSuccess());
        Assert.assertEquals("biezhi", restResponse3.getPayload());
    }

    @Test
    public void testOk() {
        Assert.assertEquals(true, RestResponse.ok().isSuccess());
        Assert.assertEquals("Hello", RestResponse.ok("Hello").getPayload());

        RestResponse ok = RestResponse.ok("Hello", 200);
        Assert.assertEquals("Hello", ok.getPayload());
        Assert.assertEquals(true, ok.isSuccess());
    }

    @Test
    public void testFail(){
        Assert.assertEquals(false, RestResponse.fail().isSuccess());
        Assert.assertEquals(500, RestResponse.fail(500).getCode());
        Assert.assertEquals("error", RestResponse.fail("error").getMsg());
        Assert.assertEquals("error", RestResponse.fail(500, "error").getMsg());
    }

}
