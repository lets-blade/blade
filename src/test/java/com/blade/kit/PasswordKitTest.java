package com.blade.kit;

import org.junit.Assert;
import org.junit.Test;

import java.util.Base64;

/**
 * @author biezhi
 * @date 2017/9/20
 */
public class PasswordKitTest {

    @Test
    public void testPassowrd() {
        String testPasswd = "biezhi123";

        String computed_hash1 = PasswordKit.hashPassword(testPasswd);
        String computed_hash2 = PasswordKit.hashPassword(testPasswd);

        String base641 = Base64.getEncoder().encodeToString(computed_hash1.getBytes());
        String base642 = Base64.getEncoder().encodeToString(computed_hash2.getBytes());

        System.out.println(base641);
        System.out.println(base642);

        Assert.assertTrue(PasswordKit.checkPassword(testPasswd, new String(Base64.getDecoder().decode(base641))));
        Assert.assertTrue(PasswordKit.checkPassword(testPasswd, new String(Base64.getDecoder().decode(base642))));
    }

}
