package com.blade.kit;

import com.blade.model.MyPerson;
import com.blade.model.Person;
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
        System.out.println(myPerson);
    }

}
