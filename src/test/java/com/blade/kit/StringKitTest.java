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
        Assert.assertTrue(StringKit.isBlank(""));
        Assert.assertTrue(StringKit.isBlank(null));

        Assert.assertFalse(StringKit.isBlank("a"));
        Assert.assertFalse(StringKit.isBlank("null"));

        Assert.assertTrue(StringKit.isNotBlank("a b"));
        Assert.assertTrue(StringKit.isNotBlank("a"));

        Assert.assertFalse(StringKit.isNotBlank(""));
        Assert.assertFalse(StringKit.isNotBlank(null));


        Assert.assertFalse(StringKit.isNotBlank("a", "b", "  "));
        Assert.assertFalse(StringKit.isNotBlank("a", "b", null));

        Assert.assertTrue(StringKit.isNotBlank("a", "b", "c"));
        Assert.assertTrue(StringKit.isNotBlank("abc", "d ef", "gh i"));

        Bar bar =  new Bar();
        StringKit.isNotBlankThen("barName", bar::setName);
        Assert.assertEquals("barName", bar.getName());

        StringKit.notBankAccept("1", Integer::parseInt, bar::setAge);
        Assert.assertEquals(1, bar.getAge());

        StringKit.notBankThen("bar", bar::doSameThing);

        bar.setName("bar");
        Foo foo = new Foo();
        String name = StringKit.noNullElseGet(foo::getName, bar::getName);
        Assert.assertEquals("bar", name);

        foo.setName("foo");
        String fooName = StringKit.noNullElseGet(foo::getName, bar::getName);
        Assert.assertEquals("foo", fooName);
    }

    @Test
    public void testRand() {
        Assert.assertEquals(8, StringKit.rand(8).length());
        Assert.assertEquals(10, StringKit.rand(10).length());

        for (int i = 0; i < 100; i++) {
            int num = StringKit.rand(1, 10);
            Assert.assertTrue(num < 11);
            Assert.assertTrue(num > 0);
        }
    }

    @Test
    public void testIsNumber() {
        Assert.assertTrue(StringKit.isNumber("20"));
        Assert.assertTrue(StringKit.isNumber("20.1"));
        Assert.assertFalse(StringKit.isNumber("abc"));
        Assert.assertFalse(StringKit.isNumber("21w"));
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
        Assert.assertTrue(StringKit.equals("a", "a"));
        Assert.assertFalse(StringKit.equals("a", "b"));
    }

    @Test
    public void testPadRight(){
        String a = "hello";
        String b = StringKit.padRight(a, 10);
        Assert.assertEquals("hello     ", b);
    }

    @Test
    public void testPadLeft(){
        String a = "hello";
        String b = StringKit.padLeft(a, 10);
        Assert.assertEquals("     hello", b);
    }

    @Test
    public void testToUnderlineName(){
        String a = "userName";
        Assert.assertEquals("user_name", StringKit.toUnderlineName(a));
    }

    @Test
    public void testToCamelCase(){
        String a = "user_name";
        Assert.assertEquals("userName", StringKit.toCamelCase(a));
    }

    @Test
    public void testToCapitalizeCamelCase(){
        String a = "user_name";
        String b = "user_name_age";
        Assert.assertEquals("UserName", StringKit.toCapitalizeCamelCase(a));
        Assert.assertEquals("UserNameAge", StringKit.toCapitalizeCamelCase(b));
    }

    class Bar {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public void doSameThing() {
            System.out.println("do same thing");
        }
    }

    class Foo {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
