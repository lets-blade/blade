package com.blade.kit;

import org.junit.Test;

import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: chenchen42
 * Date: 2018/1/8
 * Time: 下午12:10
 * To change this template use File | Settings | File Templates.
 */
public class I18NKitTest {
    @Test
    public void testI18nByKey() {
       String name = I18nKit.getInstance("i18n_en_US").get("name");
       assert name.equals("ccqy66");
    }
    @Test
    public void testI18nByLocale() {
        String name = I18nKit.getInstance(new Locale("zh","CN")).get("name");
        assert name.equals("ccqy66");
    }
}
