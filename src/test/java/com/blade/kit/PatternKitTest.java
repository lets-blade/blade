package com.blade.kit;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/20
 */
public class PatternKitTest {

    @Test
    public void testEmail() {
        Assert.assertEquals(false, PatternKit.isEmail("abcd"));
        Assert.assertEquals(true, PatternKit.isEmail("biezhi.me@gmail.com"));
        Assert.assertEquals(true, PatternKit.isEmail("1234@q.com"));
    }

    @Test
    public void testMobile() {
        Assert.assertEquals(true, PatternKit.isMobile("15900234821"));
        Assert.assertEquals(false, PatternKit.isMobile("11112232111"));
    }

    @Test
    public void testPhone() {
        Assert.assertEquals(true, PatternKit.isPhone("033-88888888"));
        Assert.assertEquals(true, PatternKit.isPhone("033-7777777"));
        Assert.assertEquals(true, PatternKit.isPhone("0444-88888888"));
        Assert.assertEquals(true, PatternKit.isPhone("0444-7777777"));
        Assert.assertEquals(true, PatternKit.isPhone("04447777777"));

        Assert.assertEquals(false, PatternKit.isPhone("133 88888888"));
        Assert.assertEquals(false, PatternKit.isPhone("033-666666"));
        Assert.assertEquals(false, PatternKit.isPhone("0444-999999999"));
    }

    @Test
    public void testIp() {
        Assert.assertEquals(true, PatternKit.isIpAddress("192.168.1.1"));
        Assert.assertEquals(false, PatternKit.isIpAddress("256.255.255.0"));
    }

    @Test
    public void testChinese() {
        Assert.assertEquals(true, PatternKit.isChinese("你好"));
        Assert.assertEquals(false, PatternKit.isChinese("Hello"));
    }

    @Test
    public void testBirthday() {
        Assert.assertEquals(true, PatternKit.isBirthday("1999-01-11"));
        Assert.assertEquals(false, PatternKit.isBirthday("1999年01月11日"));
        Assert.assertEquals(false, PatternKit.isBirthday("1999"));
    }

    @Test
    public void testBlankSpace() {
        Assert.assertEquals(true, PatternKit.isBlankSpace(" "));
        Assert.assertEquals(false, PatternKit.isBlankSpace("a"));
    }

    @Test
    public void testDecimals() {
        Assert.assertEquals(true, PatternKit.isDecimals("29"));
        Assert.assertEquals(true, PatternKit.isDecimals("29.1"));
        Assert.assertEquals(true, PatternKit.isDecimals("29.21"));

        Assert.assertEquals(true, PatternKit.isNumber("12"));
        Assert.assertEquals(false, PatternKit.isNumber("12.2"));
        Assert.assertEquals(false, PatternKit.isNumber("abc"));

        Assert.assertEquals(false, PatternKit.isDecimals("29.2a1"));
    }

    @Test
    public void testIdCard() {
        Assert.assertEquals(true, PatternKit.isIdCard18("33698418400112523x"));
        Assert.assertEquals(true, PatternKit.isIdCard18("336984184001125233"));
        Assert.assertEquals(false, PatternKit.isIdCard18("336984184021125"));

        Assert.assertEquals(false, PatternKit.isIdCard15("336984184021125"));
    }

    @Test
    public void testURl() {
        Assert.assertEquals(true, PatternKit.isURL("http://biezhi.me"));
        Assert.assertEquals(true, PatternKit.isURL("https://biezhi.me"));
        Assert.assertEquals(true, PatternKit.isURL("ftp://192.168.1.2"));

        Assert.assertEquals(false, PatternKit.isURL("http:192.168.1.2"));
    }

    @Test
    public void testIsImage(){
        Assert.assertEquals(true, PatternKit.isImage("a.png"));
        Assert.assertEquals(true, PatternKit.isImage("a.jpeg"));
        Assert.assertEquals(true, PatternKit.isImage("g.gif"));

        Assert.assertEquals(false, PatternKit.isImage("ggif"));
        Assert.assertEquals(false, PatternKit.isImage("g.txt"));
    }

}