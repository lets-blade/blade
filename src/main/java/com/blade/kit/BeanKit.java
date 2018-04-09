package com.blade.kit;

import com.blade.exception.BeanCopyException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author biezhi
 * @date 2018/4/9
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeanKit {

    public static <T> T copy(Object origin, Class<T> destCls) {
        T dest = ReflectKit.newInstance(destCls);
        copy(origin, dest);
        return dest;
    }

    public static void copy(Object origin, Object dest) {
        String      fileName, str, getName, setName;
        List<Field> fields = new ArrayList<>();
        Method      getMethod;
        Method      setMethod;
        try {
            Class<?> c1 = origin.getClass();
            Class<?> c2 = dest.getClass();

            Field[] fs1 = c1.getDeclaredFields();
            Field[] fs2 = c2.getDeclaredFields();
            // 两个类属性比较剔除不相同的属性，只留下相同的属性
            for (Field aFs2 : fs2) {
                for (Field aFs1 : fs1) {
                    if (aFs1.getName().equals(aFs2.getName())) {
                        fields.add(aFs1);
                        break;
                    }
                }
            }
            if (fields.size() > 0) {
                for (Field f : fields) {
                    // 获取属性名称
                    fileName = f.getName();
                    // 属性名第一个字母大写
                    str = fileName.substring(0, 1).toUpperCase();
                    // 拼凑getXXX和setXXX方法名
                    getName = "get" + str + fileName.substring(1);
                    setName = "set" + str + fileName.substring(1);
                    // 获取get、set方法
                    getMethod = c1.getMethod(getName);
                    setMethod = c2.getMethod(setName, f.getType());

                    // 获取属性值
                    Object o = getMethod.invoke(origin);
                    // System.out.println(fileName + " : " + o);
                    // 将属性值放入另一个对象中对应的属性
                    if (null != o) {
                        // System.out.println("o2.setMethod = " + setMethod);
                        setMethod.invoke(dest, o);
                    }
                }
            }
        } catch (Exception e) {
            throw new BeanCopyException(e);
        }
    }

}
