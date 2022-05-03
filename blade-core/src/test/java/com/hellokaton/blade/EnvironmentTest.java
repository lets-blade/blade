package com.hellokaton.blade;

import com.hellokaton.blade.mvc.BladeConst;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    public void testEnvByProperties() {
        Properties properties = new Properties();
        properties.setProperty("name", "jack");
        Environment environment = Environment.of(properties);
        assertEquals("jack", environment.get("name").get());

        assertNotNull(environment.props());
    }

    @Test
    public void testDefaultValue() {
        Properties properties = new Properties();
        properties.setProperty("name", "jack");
        Environment environment = Environment.of(properties);

        String name = environment.get("name", "rose");
        String age = environment.get("age", "20");
        int version = environment.getInt("app.version", 1001);
        long base = environment.getLong("app.BANNER_PADDING", 1002);

        Assert.assertEquals("jack", name);
        Assert.assertEquals("20", age);
        Assert.assertEquals(1001, version);
        Assert.assertEquals(1002, base);
    }

    @Test
    public void testEnvByMap() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "mapJack");
        Environment environment = Environment.of(map);
        assertEquals("mapJack", environment.getOrNull("name"));
    }

    @Test
    public void testEnvByUrl() {
        URL url = EnvironmentTest.class.getResource("/application.properties");
        Environment environment = Environment.of(url);
        assertEquals("0.0.2", Objects.requireNonNull(environment)
                .getOrNull("app.version"));
    }

    @Test
    public void testEnvByFile() {
        File file = new File(EnvironmentTest.class.getResource("/").getPath() + "application.properties");
        Environment environment = Environment.of(file);
        assertEquals("0.0.2", environment.get("app.version").get());
    }

    @Test
    public void testOf() {
        Environment environment = Environment.of("application.properties");
        Optional<String> version = Objects.requireNonNull(environment).get("app.version");
        String lang = environment.get("app.lang", "cn");

        assertEquals("0.0.2", version.orElse(""));
        assertEquals("cn", lang);

        environment = Environment.of("classpath:application.properties");
        version = Objects.requireNonNull(environment).get("app.version");
        lang = environment.get("app.lang", "cn");

        assertEquals("0.0.2", version.orElse(""));
        assertEquals("cn", lang);
    }

    @Test
    public void testGetValue() {
        Environment environment = Environment.of("application.properties");

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

        Optional<Date> dateOptional = environment.getDate("app.startDate");
        assertEquals(true, dateOptional.isPresent());

        Boolean helloWorld = environment.getBoolean("hello.world201", false);
        assertEquals(Boolean.FALSE, helloWorld);
    }

    @Test
    public void testGetValueOrNull() {
        Environment environment = Environment.of("application.properties");

        String abcd = Objects.requireNonNull(environment).getOrNull("app.abcd");
        Assert.assertNull(abcd);

        Integer intVal = environment.getIntOrNull("app.abcd");
        Assert.assertNull(intVal);

        Long longVal = environment.getLongOrNull("app.abcd");
        Assert.assertNull(longVal);

        Double doubleVal = environment.getDoubleOrNull("app.abcd");
        Assert.assertNull(doubleVal);

        Boolean boolVal = environment.getBooleanOrNull("app.abcd");
        Assert.assertNull(boolVal);

        Date dateVal = environment.getDateOrNull("app.abcd");
        Assert.assertNull(dateVal);
    }

    @Test
    public void testGetPrefix() {
        Environment environment = Environment.of("application.properties");
        Map<String, Object> map = environment.getPrefix("app");
        assertEquals(7, map.size());
        assertEquals("0.0.2", map.get("version"));
    }

    @Test
    public void testHasKey() {
        Environment environment = Environment.of("classpath:/application.properties");
        assertEquals(Boolean.FALSE, environment.hasKey("hello"));
        assertEquals(Boolean.TRUE, environment.hasKey("app.version"));
        assertEquals(Boolean.FALSE, environment.hasKey(null));
    }

    @Test
    public void testHasValue() {
        Environment environment = Environment.empty();
        assertEquals(Boolean.FALSE, environment.hasValue("hello"));
    }

    @Test(expected = IllegalStateException.class)
    public void testNoEnv() {
        Environment.of("url:http://www.biezhi.ddd");
    }

    @Test(expected = IllegalStateException.class)
    public void testNoEnvByFile() {
        Environment.of(new File("a123.properties"));
    }

    @Test
    public void testNoEnvByFileString() {
        String path = BladeConst.CLASSPATH + "/application.properties";
        Environment.of("file:" + path);
    }

    @Test
    public void testAddAll() {
        Environment environment = Environment.empty();

        Properties prop = new Properties();
        prop.setProperty("a", "1");
        environment.addAll(prop);

        assertEquals(1, environment.size());

        Map<String, String> map = Collections.singletonMap("aa", "bb");

        environment.addAll(map);
        assertEquals(2, environment.size());
    }

    @Test
    public void testSet() {
        Environment environment = Environment.empty();
        environment.set("name", "biezhi");

        assertEquals("biezhi", environment.getOrNull("name"));

        environment.add("age", 20);
        assertEquals(Integer.valueOf(20), environment.getIntOrNull("age"));
    }
}