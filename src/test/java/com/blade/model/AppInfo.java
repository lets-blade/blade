package com.blade.model;

import com.blade.ioc.annotation.Bean;
import com.blade.ioc.annotation.Value;

/**
 * @author : ccqy66
 * Date: 2017/12/25
 */
@Bean
@Value(name="app")
public class AppInfo {
    private String users;
    private String maxMoney;
    private String sex;
    private String hits;
    private String startDate;

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(String maxMoney) {
        this.maxMoney = maxMoney;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHits() {
        return hits;
    }

    public void setHits(String hits) {
        this.hits = hits;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "users='" + users + '\'' +
                ", maxMoney='" + maxMoney + '\'' +
                ", sex='" + sex + '\'' +
                ", hits='" + hits + '\'' +
                ", startDate='" + startDate + '\'' +
                '}';
    }
}
