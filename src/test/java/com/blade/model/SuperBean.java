package com.blade.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author ydq
 * @date 2020/3/21
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SuperBean {
    String repeatField;
    String superField;
}
