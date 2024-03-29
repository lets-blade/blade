package com.hellokaton.blade.kit;

import com.hellokaton.blade.kit.model.MyPerson;
import com.hellokaton.blade.kit.model.Person;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2018/4/9
 */
public class BeanKitTest {

    @Test
    public void testCopy() {
        Person source = new Person("jack", "nu", 22);
        Person dest   = new Person();
        BeanKit.copy(source, dest);
        Assert.assertEquals(source.toString(), dest.toString());

        Person dest2 = BeanKit.copy(source, Person.class);
        Assert.assertEquals(source.toString(), dest2.toString());

        MyPerson myPerson = BeanKit.copy(source, MyPerson.class);
        Assert.assertNotNull(myPerson.getName());
    }

}
