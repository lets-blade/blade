package com.blade.kit;

import com.blade.mvc.Const;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * Date kit
 *
 * @author biezhi
 * 2017/6/2
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateKit {

    /**
     * GMT Format
     */
    private static final DateTimeFormatter GMT_FMT     = DateTimeFormatter.ofPattern(Const.HTTP_DATE_FORMAT, Locale.US);

    /**
     * GMT ZoneId
     */
    private static final ZoneId            GMT_ZONE_ID = ZoneId.of("GMT");

    /**
     * get current unix time
     *
     * @return return current unix time
     */
    public static int nowUnix() {
        return (int) Instant.now().getEpochSecond();
    }

    /**
     * format unix time to string
     *
     * @param unixTime unix time
     * @param pattern  date format pattern
     * @return return string date
     */
    public static String toString(long unixTime, String pattern) {
        return Instant.ofEpochSecond(unixTime).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * format date to string
     *
     * @param date    date instance
     * @param pattern date format pattern
     * @return return string date
     */
    public static String toString(Date date, String pattern) {
        Instant instant = new java.util.Date((date.getTime())).toInstant();
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String toString(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String toString(LocalDateTime date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String toString(LocalDateTime time) {
        return toString(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * format string time to unix time
     *
     * @param time    string date
     * @param pattern date format pattern
     * @return return unix time
     */
    public static int toUnix(String time, String pattern) {
        LocalDateTime formatted = LocalDateTime.parse(time, DateTimeFormatter.ofPattern(pattern));
        return (int) formatted.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * format string (yyyy-MM-dd HH:mm:ss) to unix time
     *
     * @param time string datetime
     * @return return unix time
     */
    public static int toUnix(String time) {
        return toUnix(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static int toUnix(Date date) {
        return (int) date.toInstant().getEpochSecond();
    }

    public static Date toDate(String time, String pattern) {
        LocalDate formatted = LocalDate.parse(time, DateTimeFormatter.ofPattern(pattern));
        return Date.from(Instant.from(formatted.atStartOfDay(ZoneId.systemDefault())));
    }

    public static Date toDateTime(String time, String pattern) {
        LocalDateTime formatted = LocalDateTime.parse(time, DateTimeFormatter.ofPattern(pattern));
        return Date.from(formatted.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate toLocalDate(String time, String pattern) {
        return LocalDate.parse(time, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime toLocalDateTime(String time, String pattern) {
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(pattern));
    }

    public static Date toDate(long unixTime) {
        return Date.from(Instant.ofEpochSecond(unixTime));
    }

    public static String gmtDate() {
        return GMT_FMT.format(LocalDateTime.now().atZone(GMT_ZONE_ID));
    }

    public static String gmtDate(LocalDateTime localDateTime) {
        return GMT_FMT.format(localDateTime.atZone(GMT_ZONE_ID));
    }

    public static String gmtDate(Date date) {
        return GMT_FMT.format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).atZone(GMT_ZONE_ID));

    }

}
