
package com.blade.ioc;

import com.blade.ioc.bean.ClassInfo;
import com.blade.ioc.bean.Scanner;

import java.util.Set;

/**
 * 一个类读取器的接口
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
public interface ClassReader {

    Set<ClassInfo> readClasses(Scanner scanner);

}