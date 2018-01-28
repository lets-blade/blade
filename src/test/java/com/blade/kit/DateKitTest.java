package com.blade.kit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * @author biezhi
 * @date 2017/9/20
 */
public class DateKitTest {

    private long          time;
    private Date          date;
    private LocalDateTime localDateTime;

    @Before
    public void before() {
        time = 1505892010L;
        date = new Date(1505892470110L);
        localDateTime = LocalDateTime.from(Instant.ofEpochSecond(time).atZone(ZoneId.systemDefault()));
    }

    @Test
    public void testToString() {
        String dateStr = DateKit.toString(date, "yyyy-MM-dd");
        Assert.assertEquals("2017-09-20", dateStr);
    }

    @Test
    public void testToString2() {
        String dateStr = DateKit.toString(time, "yyyy-MM-dd");
        Assert.assertEquals("2017-09-20", dateStr);
    }

    @Test
    public void testToString3() {
        String dateStr = DateKit.toString(localDateTime, "yyyy-MM-dd");
        String timeStr = DateKit.toString(localDateTime, "yyyy-MM-dd HH:mm:ss");
        Assert.assertEquals("2017-09-20", dateStr);
        Assert.assertEquals("2017-09-20 15:20:10", timeStr);
    }

    @Test
    public void testGmtDate() {
        String gmt = DateKit.gmtDate(date);
        Assert.assertEquals("Wed, 20 Sep 2017 15:27:50 GMT", gmt);

        gmt = DateKit.gmtDate(localDateTime);
        Assert.assertEquals("Wed, 20 Sep 2017 15:20:10 GMT", gmt);

        Assert.assertNotNull(DateKit.gmtDate());
    }

    @Test
    public void testToDateTime() {
        Date date = DateKit.toDate("2017-09-09", "yyyy-MM-dd");
        Assert.assertNotNull(date);

        date = DateKit.toDate(time);
        Assert.assertNotNull(date);

        Date dateTime = DateKit.toDateTime("2017-09-09 11:22:33", "yyyy-MM-dd HH:mm:ss");
        Assert.assertNotNull(dateTime);

        LocalDate localDate = DateKit.toLocalDate("2017-09-09", "yyyy-MM-dd");
        Assert.assertNotNull(localDate);

        LocalDateTime localDateTime = DateKit.toLocalDateTime("2017-09-09 11:22:33", "yyyy-MM-dd HH:mm:ss");
        Assert.assertNotNull(localDateTime);
    }

    @Test
    public void testToUnix() {
        int unixTime = DateKit.toUnix(date);
        Assert.assertEquals(1505892470, unixTime);

        unixTime = DateKit.toUnix("2017-09-09 11:22:33");
        Assert.assertEquals(1504927353, unixTime);

        unixTime = DateKit.toUnix("2017-09-09 11:22", "yyyy-MM-dd HH:mm");
        Assert.assertEquals(1504927320, unixTime);

        unixTime = DateKit.nowUnix();
        Assert.assertNotNull(unixTime);
    }

    /**
     * 获取今天的开始的时间
     */
    @Test
    public void testToBeginOfDay() {
        Calendar calendar = DateKit.beginOfDay(Calendar.getInstance());
        String dateStr = DateKit.toString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
        System.out.println(dateStr);
    }

    /**
     * 获取今天的结束的时间
     */
    @Test
    public void testToEndOfDay() {
        Calendar calendar = DateKit.endOfDay(Calendar.getInstance());
        String dateStr = DateKit.toString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
        System.out.println(dateStr);
    }

    /**
     * 是否为闰年
     */
    @Test
    public void tesTisLeapYear() {
        boolean leapYear = DateKit.isLeapYear(2016);
        Assert.assertEquals(true, leapYear);
    }

    /**
     * 是否为最后一天
     */
    @Test
    public void testIsLastDay() {
        boolean lastDay = DateKit.isLastDay("2018-1-31");
        Assert.assertEquals(true, lastDay);
    }

}
