package com.blade.kit;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/21
 */
public class StringKitTest {

    @Test
    public void testIsBlank() {
        Assert.assertEquals(true, StringKit.isBlank(""));
        Assert.assertEquals(true, StringKit.isBlank(null));

        Assert.assertEquals(false, StringKit.isBlank("a"));
        Assert.assertEquals(false, StringKit.isBlank("null"));

        Assert.assertEquals(true, StringKit.isNotBlank("a b"));
        Assert.assertEquals(true, StringKit.isNotBlank("a"));

        Assert.assertEquals(false, StringKit.isNotBlank(""));
        Assert.assertEquals(false, StringKit.isNotBlank(null));
    }

    @Test
    public void testRand() {
        Assert.assertEquals(8, StringKit.rand(8).length());
        Assert.assertEquals(10, StringKit.rand(10).length());

        for (int i = 0; i < 100; i++) {
            int num = StringKit.rand(1, 10);
            Assert.assertEquals(true, num < 11);
            Assert.assertEquals(true, num > 0);
        }
    }

    @Test
    public void testIsNumber() {
        Assert.assertEquals(true, StringKit.isNumber("20"));
        Assert.assertEquals(true, StringKit.isNumber("20.1"));
        Assert.assertEquals(false, StringKit.isNumber("abc"));
        Assert.assertEquals(false, StringKit.isNumber("21w"));
    }

    @Test
    public void testAlign() {
        String str = StringKit.alignRight("Hello", 10, '#');
        Assert.assertEquals("#####Hello", str);

        str = StringKit.alignLeft("Hello", 10, '#');
        Assert.assertEquals("Hello#####", str);
    }

    @Test
    public void testDup() {
        String str = StringKit.dup('c', 6);
        Assert.assertEquals(6, str.length());
        Assert.assertEquals("cccccc", str);
    }

    @Test
    public void testFileExt() {
        String ext = StringKit.fileExt("a.png");
        Assert.assertEquals("png", ext);
    }

    @Test
    public void testMimeType(){
        String mimeType = StringKit.mimeType("a.png");
        Assert.assertEquals("image/png", mimeType);

        mimeType = StringKit.mimeType("a.txt");
        Assert.assertEquals("text/plain", mimeType);

        mimeType = StringKit.mimeType("a.pdf");
        Assert.assertEquals("application/pdf", mimeType);
    }

    @Test
    public void testEquals(){
        Assert.assertEquals(true, StringKit.equals("a", "a"));
        Assert.assertEquals(false, StringKit.equals("a", "b"));
    }

}
