package com.hellokaton.blade.kit.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author ydq
 * @date 2020/3/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChildBean extends SuperBean {

    private String repeatField;

    private String childField;

}
