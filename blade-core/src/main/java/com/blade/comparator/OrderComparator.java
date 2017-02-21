package com.blade.comparator;

import com.blade.annotation.Order;
import com.blade.kit.resource.ClassInfo;

import java.util.Comparator;

public class OrderComparator implements Comparator<ClassInfo> {

    @Override
    public int compare(ClassInfo c1, ClassInfo c2) {
        Order o1 = c1.getClazz().getAnnotation(Order.class);
        Order o2 = c2.getClazz().getAnnotation(Order.class);
        if (null == o1 || null == o2)
            return 0;
        if (o1.sort() > o2.sort())
            return 1;
        if (o1.sort() < o2.sort())
            return -1;
        return 0;
    }

}