package com.blade.metric;

import com.blade.kit.DateKit;

import java.time.LocalDateTime;

/**
 * @author biezhi
 *         2017/6/3
 */
public class Metric {

    private long count;
    private LocalDateTime time;

    public Metric(long count, LocalDateTime time) {
        this.count = count;
        this.time = time;
    }

    public Metric plus(int count) {
        this.count += count;
        return this;
    }

    public Metric time(LocalDateTime time) {
        this.time = time;
        return this;
    }

    public long count() {
        return this.count;
    }

    public LocalDateTime time() {
        return this.time;
    }

    @Override
    public String toString() {
        return "Metric(" +
                "count=" + count +
                ", time=" + DateKit.toString(time, "yyyy-MM-dd HH:mm:ss") +
                ')';
    }
}
