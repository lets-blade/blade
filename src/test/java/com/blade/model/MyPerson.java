package com.blade.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author biezhi
 * @date 2018/4/9
 */
@Data
@ToString(callSuper = true)
public class MyPerson extends Person {

    private Boolean sex;

}
