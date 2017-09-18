package com.blade;

import org.junit.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * @author biezhi
 * @date 2017/9/18
 */
public class EnvironmentTest {

    @Test
    public void testEmpty() {
        Environment environment = Environment.empty();
        assertEquals(0, environment.toMap().size());
    }

    @Test
    public void testOf() {
        Environment      environment = Environment.of("app.properties");
        Optional<String> version     = environment.get("app.version");
        String           lang        = environment.get("app.lang", "cn");

        assertEquals("0.0.2", version.get());
        assertEquals("cn", lang);

        environment = Environment.of("classpath:app.properties");
        version = environment.get("app.version");
        lang = environment.get("app.lang", "cn");

        assertEquals("0.0.2", version.get());
        assertEquals("cn", lang);
    }

    @Test
    public void testGetValue() {
        Environment environment = Environment.of("app.properties");

        Optional<String> url = environment.get("jdbc.url");
        assertEquals("jdbc:mysql://127.0.0.1:3306/test", url.get());

        Optional<Integer> users = environment.getInt("app.users");
        assertEquals(Integer.valueOf(301), users.get());

        Optional<Double> maxMoney = environment.getDouble("app.maxMoney");
        assertEquals(Double.valueOf(38.1), maxMoney.get());

        Optional<Boolean> sex = environment.getBoolean("app.sex");
        assertEquals(Boolean.TRUE, sex.get());

        Optional<Long> hits = environment.getLong("app.hits");
        assertEquals(Long.valueOf(199283818033L), hits.get());
    }

    @Test
    public void testGetPrefix() {
        Environment         environment = Environment.of("app.properties");
        Map<String, Object> map         = environment.getPrefix("app");
        assertEquals(5, map.size());
        assertEquals("0.0.2", map.get("version"));
    }

}