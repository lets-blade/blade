package com.blade.model;

import com.blade.ioc.annotation.Value;
import lombok.Data;

/**
 * @author : ccqy66
 * Date: 2017/12/25
 */
@Value(name = "app")
@Data
public class AppInfo {
    private int users;
    private double maxMoney;
    private boolean sex;
    private Long hits;
    private String startDate;
    private int age;
}
