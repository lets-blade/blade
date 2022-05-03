package com.hellokaton.blade.kit;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/21
 */
public class UUIDTest {

    @Test
    public void testUU32(){
        String uu32 = UUID.UU32();
        System.out.println(uu32);
    }

    @Test
    public void testUU64(){
        String uu64 = UUID.UU64();
        System.out.println(uu64);
    }

    @Test
    public void testCaptchaNumber() throws Exception {
        Assert.assertEquals(0, UUID.captchaNumber(0).length());
        Assert.assertEquals(2, UUID.captchaNumber(2).length());
        Assert.assertEquals(4, UUID.captchaNumber(4).length());
        Assert.assertEquals(10, UUID.captchaNumber(10).length());
        Assert.assertEquals(2, UUID.captchaChar(2).length());
        Assert.assertEquals(4, UUID.captchaChar(4).length());
        Assert.assertEquals(10, UUID.captchaChar(10).length());
    }

    @Test
    public void testCaptchaContent() throws Exception {
        String c1 = UUID.captchaNumber(100);
        TestCase.assertTrue(hasNumber(c1));
        Assert.assertFalse(hasUpperLetter(c1));
        Assert.assertFalse(hasLowerLetter(c1));

        String c2 = UUID.captchaChar(1000);
        TestCase.assertTrue(hasNumber(c2));
        TestCase.assertTrue(hasLowerLetter(c2));
        Assert.assertFalse(hasUpperLetter(c2));

        // 1000个字符里肯定得有个大写的
        String c3 = UUID.captchaChar(1000, true);
        TestCase.assertTrue(hasNumber(c3));
        TestCase.assertTrue(hasLowerLetter(c3));
        TestCase.assertTrue(hasUpperLetter(c3));
    }

    // 48~57
    public boolean hasNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            int c = (int) str.charAt(i);
            if (c >= 48 && c <= 57) {
                return true;
            }
        }
        return false;
    }

    // 65-90
    private boolean hasUpperLetter(String str) {
        for (int i = 0; i < str.length(); i++) {
            int c = (int) str.charAt(i);
            if (c >= 65 && c <= 90) {
                return true;
            }
        }
        return false;
    }

    // 97~122
    private boolean hasLowerLetter(String str) {
        for (int i = 0; i < str.length(); i++) {
            int c = (int) str.charAt(i);
            if (c >= 97 && c <= 122) {
                return true;
            }
        }
        return false;
    }

}
