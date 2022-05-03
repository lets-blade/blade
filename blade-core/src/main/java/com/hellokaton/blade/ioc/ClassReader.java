
package com.hellokaton.blade.ioc;

import com.hellokaton.blade.ioc.bean.ClassInfo;
import com.hellokaton.blade.ioc.bean.Scanner;

import java.util.Set;

/**
 * 一个类读取器的接口
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 1.0
 */
public interface ClassReader {

    Set<ClassInfo> readClasses(Scanner scanner);

}