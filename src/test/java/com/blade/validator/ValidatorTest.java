package com.blade.validator;

import com.blade.exception.ValidatorException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.blade.validator.Validators.between;
import static org.junit.Assert.assertTrue;

/**
 * @author biezhi
 * @date 2018/4/21
 */
public class ValidatorTest {

    @Before
    public void before() {
        Validators.useChinese();
    }

    @Test
    public void testNormal() {
        String str1 = "";
        try {
            Validators.notEmpty().test(str1).throwMessage("str1不能为空");
        } catch (Exception e) {
            Assert.assertEquals("str1不能为空", e.getMessage());
        }

        str1 = "hello";
        try {
            Validators.notEmpty().and(Validators.isEmail()).test(str1).throwIfInvalid("登录邮箱");
        } catch (Exception e) {
            Assert.assertEquals("登录邮箱 不是一个合法的邮箱", e.getMessage());
        }

        Integer num = 2;
        Validators.range(3, 5).test(num).throwIfInvalid("num");
    }

    @Test
    public void testTopicValidator() {
        Topic param = null;
        try {
            Validators.notNull().test(param).throwIfInvalid("topic");
        } catch (ValidatorException e) {
            assertTrue(e.getMessage().contains("topic"));
        }
    }

    @Test
    public void testTopicValidatorAnd() {
        Topic param = new Topic();
        param.setTitle("hello");
        param.setContent("blade is good mvc framework.");
        Validators.notNull().test(param).throwIfInvalid("topic");
        Validators.notEmpty().and(between(3, 20)).test(param.getTitle()).throwIfInvalid("title");
        Validators.notEmpty().and(between(5, 100)).test(param.getContent()).throwIfInvalid("content");
    }

    @Test
    public void testStaticClass() {
        Topic topic = new Topic();
        topic.setTitle("hello");
        topic.setContent("world");
        topic.setEmail("123@gmail.com");
        topic.setUrl("https://github.com/biezhi");
        topic.setRange(15);
        TopicValidator.valid(topic);
    }

    static class TopicValidator {
        public static void valid(Topic param) {
            Validators.notNull().test(param).throwIfInvalid("topic");
            Validators.between(3, 20).test(param.getTitle()).throwIfInvalid("title");
            Validators.between(5, 100).test(param.getContent()).throwIfInvalid("content");
            Validators.isEmail().test(param.getEmail()).throwIfInvalid("email");
            Validators.isURL().test(param.getUrl()).throwIfInvalid(Topic::getUrl);
            Validators.notNull().test(param.getRange()).throwIfInvalid(Topic::getRange);
            Validators.range(10, 20).test(param.getRange()).throwIfInvalid(Topic::getRange);
        }
    }

}
