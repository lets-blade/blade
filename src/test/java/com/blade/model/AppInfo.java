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
    private String users;
    private String maxMoney;
    private String sex;
    private String hits;
    private String startDate;

}
