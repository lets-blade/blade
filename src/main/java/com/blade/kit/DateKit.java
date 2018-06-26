/**
 * Copyright (c) 2017, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.kit;

import com.blade.mvc.Const;
import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Date kit
 *
 * @author biezhi
 * 2017/6/2
 */
@UtilityClass
public class DateKit {

    /**
     * GMT Format
     */
    public static final DateTimeFormatter GMT_FMT = DateTimeFormatter.ofPattern(Const.HTTP_DATE_FORMAT, Locale.US);

    /**
     * GMT ZoneId
     */
    public static final ZoneId GMT_ZONE_ID = ZoneId.of("GMT");

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

    public static void main(String[] args) {
        String s = "Thu, 31 May 2018 00:38:15 GMT";
        System.out.println(LocalDateTime.parse(s, GMT_FMT).atZone(GMT_ZONE_ID).toInstant().getEpochSecond());
    }

    private static final Map<String, String> PRETTY_TIME_I18N = new HashMap<>();

    static {
        PRETTY_TIME_I18N.put("zh_YEARS", "年前");
        PRETTY_TIME_I18N.put("zh_MONTHS", "个月前");
        PRETTY_TIME_I18N.put("zh_WEEKS", "周前");
        PRETTY_TIME_I18N.put("zh_DAYS", "天前");
        PRETTY_TIME_I18N.put("zh_HOURS", "小时前");
        PRETTY_TIME_I18N.put("zh_MINUTES", "分钟前");
        PRETTY_TIME_I18N.put("zh_SECONDS", "秒前");
        PRETTY_TIME_I18N.put("zh_JUST_NOW", "刚刚");
        PRETTY_TIME_I18N.put("zh_YESTERDAY", "昨天");
        PRETTY_TIME_I18N.put("zh_LAST_WEEK", "上周");
        PRETTY_TIME_I18N.put("zh_LAST_MONTH", "上个月");
        PRETTY_TIME_I18N.put("zh_LAST_YEAR", "去年");

        PRETTY_TIME_I18N.put("us_YEARS", "years ago");
        PRETTY_TIME_I18N.put("us_MONTHS", "months ago");
        PRETTY_TIME_I18N.put("us_DAYS", "days ago");
        PRETTY_TIME_I18N.put("us_HOURS", "hours ago");
        PRETTY_TIME_I18N.put("us_MINUTES", "minutes ago");
        PRETTY_TIME_I18N.put("us_SECONDS", "seconds ago");
        PRETTY_TIME_I18N.put("us_JUST_NOW", "Just now");
        PRETTY_TIME_I18N.put("us_YESTERDAY", "Yesterday");
        PRETTY_TIME_I18N.put("us_LAST_WEEK", "Last week");
        PRETTY_TIME_I18N.put("us_LAST_MONTH", "Last month");
        PRETTY_TIME_I18N.put("us_LAST_YEAR", "Last year");
    }

    public static String prettyTime(LocalDateTime date, Locale locale) {
        if (date == null) {
            return null;
        }
        String keyPrefix = locale.getLanguage().equals("zh") ? "zh" : "us";
        long   diff      = Duration.between(date, LocalDateTime.now()).toMillis() / 1000;
        int    amount;

        /**
         * Second counts
         * 3600: hour
         * 86400: day
         * 604800: week
         * 2592000: month
         * 31536000: year
         */
        if (diff >= 31536000) {
            amount = (int) (diff / 31536000);
            if (amount == 1) {
                return PRETTY_TIME_I18N.get(keyPrefix + "_LAST_YEAR");
            }
            return amount + PRETTY_TIME_I18N.get(keyPrefix + "_YEARS");
        } else if (diff >= 2592000) {
            amount = (int) (diff / 2592000);
            if (amount == 1) {
                return PRETTY_TIME_I18N.get(keyPrefix + "_LAST_MONTH");
            }
            return amount + PRETTY_TIME_I18N.get(keyPrefix + "_MONTHS");
        } else if (diff >= 604800) {
            amount = (int) (diff / 604800);
            if (amount == 1) {
                return PRETTY_TIME_I18N.get(keyPrefix + "_LAST_WEEK");
            }
            return amount + PRETTY_TIME_I18N.get(keyPrefix + "_WEEKS");
        } else if (diff >= 86400) {
            amount = (int) (diff / 86400);
            if (amount == 1) {
                return PRETTY_TIME_I18N.get(keyPrefix + "_YESTERDAY");
            }
            return amount + PRETTY_TIME_I18N.get(keyPrefix + "_DAYS");
        } else if (diff >= 3600) {
            amount = (int) (diff / 3600);
            return amount + PRETTY_TIME_I18N.get(keyPrefix + "_HOURS");
        } else if (diff >= 60) {
            amount = (int) (diff / 60);
            return amount + PRETTY_TIME_I18N.get(keyPrefix + "_MINUTES");
        } else {
            amount = (int) diff;
            if (amount < 6) {
                return PRETTY_TIME_I18N.get(keyPrefix + "_JUST_NOW");
            } else {
                return amount + PRETTY_TIME_I18N.get(keyPrefix + "_SECONDS");
            }
        }
    }

    public static String prettyTime(LocalDateTime date) {
        return prettyTime(date, Locale.getDefault());
    }

}
