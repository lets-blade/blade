package com.blade.mvc;

import com.blade.model.Person;
import com.blade.mvc.ui.template.UncheckedTemplateException;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.blade.mvc.ui.template.BladeTemplate.fromFile;
import static com.blade.mvc.ui.template.BladeTemplate.template;
import static org.junit.Assert.assertTrue;

/**
 * @author biezhi
 *         2017/6/2
 */
public class DefaultTemplateTest {

    @Test
    public void testFormatSimpleArguments() throws Exception {
        String result = template("${int3}-${int2}").arg("int3", 5).arg("int2", 7).fmt();
        assertTrue("5-7".equals(result));
    }

    @Test
    public void testFormatSimpleFormatNullArgument() throws Exception {
        String result = template("${NULL}-${a}").args("NULL", null, "a", 5).fmt();
        assertTrue("null-5".equals(result));
    }

    @Test
    public void testFormatSimpleMultiplePointsArgument() throws Exception {
        String result = template("${} ${a}").args("", "A", "a", 2).fmt();
        assertTrue("A 2".equals(result));
    }

    @Test(expected = UncheckedTemplateException.class)
    public void testWithInvalidParam() throws Exception {
        String result = template("${${}} ${a}").args().fmt();
        System.out.println(result);
    }

    @Test
    public void testWithList() throws Exception {
        List<String> stress = Arrays.asList("1", "2", "3");
        String result = template("${l}").args("l", stress).fmt();
        assertTrue("[1, 2, 3]".equals(result));
    }

    @Test
    public void testWithPrimitiveArray() throws Exception {
        int[] arr = {1, 3, 4};
        String result = template("${a}").args("a", arr).fmt();
        assertTrue("[1, 3, 4]".equals(result));
    }

    @Test
    public void testWithPrimitiveArrayEmpty() throws Exception {
        int[] a = {};
        String result = template("${a}").args("a", a).fmt();
        assertTrue("[]".equals(result));
    }

    @Test
    public void testEscape() throws Exception {
        String result = template("`${q},${q}").args("q", "Q").fmt();
        assertTrue("${q},Q".equals(result));
        System.out.println(result);
    }

    @Test
    public void testDoubleEscape() throws Exception {
        String result = template("``${q},${q}").args("q", "Q").fmt();
        assertTrue("`Q,Q".equals(result));
    }

    @Test(expected = UncheckedTemplateException.class)
    public void testEscapeInParamName() throws Exception {
        String result = template("${`q}${q`}").args("q", "Q").fmt();
        System.out.println(result);
    }

    @Test
    public void testEscapeLastCharInTemplate() throws Exception {
        String result = template("${q}`", "q", "Q").fmt();
        assertTrue("Q".equals(result));
    }

    @Test
    public void testWithGetters() throws Exception {
        Person person = new Person("A", "B", 20);
        String result = template("${p.getName}/${p.getText}/${p.getAge}", "p", person).fmt();
        assertTrue("A/B/20".equals(result));
    }

    @Test
    public void testWithFieldNames() throws Exception {
        Person person = new Person("A", "B", 20);
        String result = template("${p.name}/${p.text}/${p.age}", "p", person).fmt();
        assertTrue("A/B/20".equals(result));
    }

    @Test
    public void testFromFile() throws Exception {
        File tmp = File.createTempFile("aleph" + UUID.randomUUID().toString(), ".tmp");
        tmp.deleteOnExit();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp))) {
            bw.write("${p.name}/${p.text}/${p.age}");
            bw.flush();

        }

        Person person = new Person("A", "B", 20);
        String result = fromFile(tmp.getPath(), "p", person).fmt();

        assertTrue("A/B/20".equals(result));
    }

}
