package com.blade.ioc;

import lombok.Builder;
import lombok.Data;

import java.lang.annotation.Annotation;

/**
 * @author biezhi
 * @date 2017/10/19
 */
@Data
@Builder
public class Scanner {

    private String                      packageName;
    private boolean                     recursive;
    private Class<?>                    parent;
    private Class<? extends Annotation> annotation;
}
