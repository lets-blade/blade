/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.kit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 
 * <p>
 * 日期处理类
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class DateKit {

	/**
	 * 日
	 */
	public final static int INTERVAL_DAY = 1;
	
	/**
	 * 周
	 */
	public final static int INTERVAL_WEEK = 2;
	
	/**
	 * 月
	 */
	public final static int INTERVAL_MONTH = 3;
	
	/**
	 * 年
	 */
	public final static int INTERVAL_YEAR = 4;
	
	/**
	 * 小时
	 */
	public final static int INTERVAL_HOUR = 5;
	
	/**
	 * 分钟
	 */
	public final static int INTERVAL_MINUTE = 6;
	
	/**
	 * 秒
	 */
	public final static int INTERVAL_SECOND = 7;
	
	/**
	 * date = 1901-01-01
	 */
	public final static Date tempDate = new Date(new Long("-2177481952000"));;

	/**
	 * 测试是否是当天
	 * 
	 * @param date	某一日期
	 * @return true	今天, false-不是
	 */
	@SuppressWarnings("deprecation")
	public static boolean isToday(Date date) {
		Date now = new Date();
		boolean result = true;
		result &= date.getYear() == now.getYear();
		result &= date.getMonth() == now.getMonth();
		result &= date.getDate() == now.getDate();
		return result;
	}

	/**
	 * 两个日期相减，取天数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long DaysBetween(Date date1, Date date2) {
		if (date2 == null)
			date2 = new Date();
		long day = (date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000);
		return day;
	}

	/**
	 * 比较两个日期 if date1<=date2 return true
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean compareDate(String date1, String date2) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d1 = format.parse(date1);
			Date d2 = format.parse(date2);
			return !d1.after(d2);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 字符型转换成日期型
	 * 
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static Date dateFormat(String date, String dateFormat) {
		if (date == null)
			return null;
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		if (date != null) {
			try {
				return format.parse(date);
			} catch (Exception ex) {
			}
		}
		return null;
	}

	/**
	 * 使用默认格式 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static Date dateFormat(String date) {
		return dateFormat(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 日期型转换成字符串
	 * 
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String dateFormat(Date date, String dateFormat) {
		if (date != null){
			SimpleDateFormat format = new SimpleDateFormat(dateFormat);
			if (date != null) {
				return format.format(date);
			}
		}
		return "";
	}

	/**
	 * 由于生日增加保密属性，现决定1900为保密对应值，如果遇到1900的年份，则隐掉年份
	 * 
	 * @param date
	 * @param dateFormat
	 * @return 不保密显示1981-12-01保密则显示`12-01
	 */
	public static String birthdayFormat(Date date) {
		if (date != null){
			SimpleDateFormat format = null;
			if (date.before(tempDate)) {
				format = new SimpleDateFormat("MM-dd");
			} else {
				format = new SimpleDateFormat("yyyy-MM-dd");
			}
			if (date != null) {
				return format.format(date);
			}
		}
		return "";
	}

	/**
	 * 使用默认格式 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String dateFormat(Date date) {
		return dateFormat(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static boolean isExpiredDay(Date date1) {
		long day = (new Date().getTime() - date1.getTime()) / (24 * 60 * 60 * 1000);
		if (day >= 1)
			return true;
		else
			return false;
	}

	public static Date getYesterday() {
		Date date = new Date();
		long time = (date.getTime() / 1000) - 60 * 60 * 24;
		date.setTime(time * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = format.parse(format.format(date));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return date;
	}

	public static Date getWeekAgo() {
		Date date = new Date();
		long time = (date.getTime() / 1000) - 7 * 60 * 60 * 24;
		date.setTime(time * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = format.parse(format.format(date));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return date;
	}

	public static String getDaysAgo(int interval) {
		Date date = new Date();
		long time = (date.getTime() / 1000) - interval * 60 * 60 * 24;
		date.setTime(time * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return format.format(date);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return "";
	}

	public static Date getTomorrow() {
		Date date = new Date();
		long time = (date.getTime() / 1000) + 60 * 60 * 24;
		date.setTime(time * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = format.parse(format.format(date));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return date;
	}

	public static Date getBeforeDate(String range) {
		Calendar today = Calendar.getInstance();
		if ("week".equalsIgnoreCase(range))
			today.add(Calendar.WEEK_OF_MONTH, -1);
		else if ("month".equalsIgnoreCase(range))
			today.add(Calendar.MONTH, -1);
		else
			today.clear();
		return today.getTime();
	}

	public static Date getThisWeekStartTime() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.DAY_OF_WEEK, today.getFirstDayOfWeek());
		Calendar weekFirstDay = Calendar.getInstance();
		weekFirstDay.clear();
		weekFirstDay.set(Calendar.YEAR, today.get(Calendar.YEAR));
		weekFirstDay.set(Calendar.MONTH, today.get(Calendar.MONTH));
		weekFirstDay.set(Calendar.DATE, today.get(Calendar.DATE));
		return weekFirstDay.getTime();
	}

	public static String getToday(String format) {
		String result = "";
		try {
			Date today = new Date();
			SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
			result = simpleFormat.format(today);
		} catch (Exception e) {
		}
		return result;
	}

	public static Date getStartDay(int year, int month) {
		Calendar today = Calendar.getInstance();
		today.clear();
		today.set(Calendar.YEAR, year);
		today.set(Calendar.MONTH, month - 1);
		today.set(Calendar.DAY_OF_MONTH, 1);
		return today.getTime();
	}

	public static List<Integer> getBeforeYearList(int before) {
		Calendar today = Calendar.getInstance();
		int theYear = today.get(Calendar.YEAR);
		List<Integer> list = new ArrayList<Integer>();
		for (int i = before; i >= 0; i--)
			list.add(theYear - i);

		return list;
	}

	/**
	 * 增加时间
	 * 
	 * @param interval
	 *            [INTERVAL_DAY,INTERVAL_WEEK,INTERVAL_MONTH,INTERVAL_YEAR,
	 *            INTERVAL_HOUR,INTERVAL_MINUTE]
	 * @param date
	 * @param n
	 *            可以为负数
	 * @return
	 */
	public static Date dateAdd(int interval, Date date, int n) {
		long time = (date.getTime() / 1000); // 单位秒
		switch (interval) {
		case INTERVAL_DAY:
			time = time + n * 86400;// 60 * 60 * 24;
			break;
		case INTERVAL_WEEK:
			time = time + n * 604800;// 60 * 60 * 24 * 7;
			break;
		case INTERVAL_MONTH:
			time = time + n * 2678400;// 60 * 60 * 24 * 31;
			break;
		case INTERVAL_YEAR:
			time = time + n * 31536000;// 60 * 60 * 24 * 365;
			break;
		case INTERVAL_HOUR:
			time = time + n * 3600;// 60 * 60 ;
			break;
		case INTERVAL_MINUTE:
			time = time + n * 60;
			break;
		case INTERVAL_SECOND:
			time = time + n;
			break;
		default:
		}

		Date result = new Date();
		result.setTime(time * 1000);
		return result;
	}

	/**
	 * 计算两个时间间隔
	 * 
	 * @param interval
	 *            [INTERVAL_DAY,INTERVAL_WEEK,INTERVAL_MONTH,INTERVAL_YEAR,
	 *            INTERVAL_HOUR,INTERVAL_MINUTE]
	 * @param begin
	 * @param end
	 * @return
	 */
	public static int dateDiff(int interval, Date begin, Date end) {
		long beginTime = (begin.getTime() / 1000); // 单位：秒
		long endTime = (end.getTime() / 1000); // 单位: 秒
		long tmp = 0;
		if (endTime == beginTime) {
			return 0;
		}

		// 确定endTime 大于 beginTime 结束时间秒数 大于 开始时间秒数
		if (endTime < beginTime) {
			tmp = beginTime;
			beginTime = endTime;
			endTime = tmp;
		}

		long intervalTime = endTime - beginTime;
		long result = 0;
		switch (interval) {
		case INTERVAL_DAY:
			result = intervalTime / 86400;// 60 * 60 * 24;
			break;
		case INTERVAL_WEEK:
			result = intervalTime / 604800;// 60 * 60 * 24 * 7;
			break;
		case INTERVAL_MONTH:
			result = intervalTime / 2678400;// 60 * 60 * 24 * 31;
			break;
		case INTERVAL_YEAR:
			result = intervalTime / 31536000;// 60 * 60 * 24 * 365;
			break;
		case INTERVAL_HOUR:
			result = intervalTime / 3600;// 60 * 60 ;
			break;
		case INTERVAL_MINUTE:
			result = intervalTime / 60;
			break;
		case INTERVAL_SECOND:
			result = intervalTime / 1;
			break;
		default:
		}

		// 做过交换
		if (tmp > 0) {
			result = 0 - result;
		}
		return (int) result;
	}

	/**
	 * 当前年份
	 * 
	 * @return
	 */
	public static int getTodayYear() {
		int yyyy = Integer.parseInt(dateFormat(new Date(), "yyyy"));
		return yyyy;
	}

	public static Date getNow() {
		return new Date();
	}

	/**
	 * 把日期格式为rss格式兼容的字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String dateFormatRss(Date date) {
		if (date != null) {
			return dateFormat(date, "E, d MMM yyyy H:mm:ss") + " GMT";
		}
		return "";
	}

	/**
	 * 判断当前日期是否在两个日期之间
	 * 
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            　结束时间
	 * @return　
	 */
	public static boolean betweenStartDateAndEndDate(Date startDate, Date endDate) {
		boolean bool = false;
		Date curDate = new Date();
		if (curDate.after(startDate) && curDate.before(DateKit.dateAdd(INTERVAL_DAY, endDate, 1))) {
			bool = true;
		}
		return bool;

	}

	/**
	 * 判断当前时间是否在在两个时间之间
	 * 
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            　结束时间
	 * @return　
	 */
	public static boolean nowDateBetweenStartDateAndEndDate(Date startDate, Date endDate) {
		boolean bool = false;
		Date curDate = new Date();
		if (curDate.after(startDate) && curDate.before(endDate)) {
			bool = true;
		}
		return bool;
	}

	/**
	 * 判断当前时间是否在date之后
	 * 
	 * @param date
	 * @return　
	 */
	public static boolean nowDateAfterDate(Date date) {
		boolean bool = false;
		Date curDate = new Date();
		if (curDate.after(date)) {
			bool = true;
		}
		return bool;
	}

	/**
	 * 判断二个日期相隔的天数,结束时间为null时，，取当前时间
	 * 
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            　结束时间
	 * @return　
	 */
	public static int getBetweenTodaysStartDateAndEndDate(Date startDate, Date endDate) {
		int betweentoday = 0;
		if (startDate == null) {
			return betweentoday;
		}
		if (endDate == null) {
			Calendar calendar = Calendar.getInstance();
			String year = new Integer(calendar.get(Calendar.YEAR)).toString();
			String month = new Integer((calendar.get(Calendar.MONTH) + 1)).toString();
			String day = new Integer(calendar.get(Calendar.DAY_OF_MONTH)).toString();
			String strtodaytime = year + "-" + month + "-" + day;
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			try {
				endDate = formatter.parse(strtodaytime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (endDate.after(startDate)) {
			betweentoday = (int) ((endDate.getTime() - startDate.getTime()) / 86400000);
		} else {
			betweentoday = (int) ((startDate.getTime() - endDate.getTime()) / 86400000);
		}
		return betweentoday;
	}

	/**
	 * 取得指定长度日期时间字符串{不含格式}
	 * 
	 * @param format
	 *            时间格式由常量决定 8: 　YYMMDDHH 8位 10:　YYMMDDHHmm 10位 12:　YYMMDDHHmmss
	 *            12位 14:　YYYYMMDDHHmmss 14位 15:　YYMMDDHHmmssxxx 15位 (最后的xxx
	 *            是毫秒)
	 */
	public static String getTime(int format) {
		StringBuffer cTime = new StringBuffer(10);
		Calendar time = Calendar.getInstance();
		int miltime = time.get(Calendar.MILLISECOND);
		int second = time.get(Calendar.SECOND);
		int minute = time.get(Calendar.MINUTE);
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int day = time.get(Calendar.DAY_OF_MONTH);
		int month = time.get(Calendar.MONTH) + 1;
		int year = time.get(Calendar.YEAR);
		if (format != 14) {
			if (year >= 2000)
				year = year - 2000;
			else
				year = year - 1900;
		}
		if (format >= 2) {
			if (format == 14)
				cTime.append(year);
			else
				cTime.append(getFormatTime(year, 2));
		}
		if (format >= 4)
			cTime.append(getFormatTime(month, 2));
		if (format >= 6)
			cTime.append(getFormatTime(day, 2));
		if (format >= 8)
			cTime.append(getFormatTime(hour, 2));
		if (format >= 10)
			cTime.append(getFormatTime(minute, 2));
		if (format >= 12)
			cTime.append(getFormatTime(second, 2));
		if (format >= 15)
			cTime.append(getFormatTime(miltime, 3));
		return cTime.toString();
	}

	/**
	 * 　产生任意位的字符串
	 * 
	 * @param time
	 *            要转换格式的时间
	 * @param format
	 *            　转换的格式
	 * @return　String 转换的时间
	 */
	private static String getFormatTime(int time, int format) {
		StringBuffer numm = new StringBuffer();
		int length = String.valueOf(time).length();
		if (format < length)
			return null;
		for (int i = 0; i < format - length; i++) {
			numm.append("0");
		}
		numm.append(time);
		return numm.toString().trim();
	}

	/**
	 * 根据生日去用户年龄
	 * 
	 * @param birthday
	 * @return int
	 * @exception
	 * @Date Apr 24, 2008
	 */
	public static int getUserAge(Date birthday) {
		if (birthday == null)
			return 0;
		Calendar cal = Calendar.getInstance();
		if (cal.before(birthday)) {
			return 0;
		}
		int yearNow = cal.get(Calendar.YEAR);
		cal.setTime(birthday);// 给时间赋值
		int yearBirth = cal.get(Calendar.YEAR);
		return yearNow - yearBirth;
	}

	/**
	 * 将int型时间(1970年至今的秒数)转换成Date型时间
	 * 
	 * @param unixTime
	 *            1970年至今的秒数
	 * @return
	 */
	public static Date getDateByUnixTime(int unixTime) {
		return new Date(unixTime * 1000L);
	}

	public static long getUnixTimeLong() {
		return getUnixTimeByDate(new Date());
	}
	
	public static int getCurrentUnixTime() {
		return getUnixTimeByDate(new Date());
	}
	
	/**
	 * 将Date型时间转换成int型时间(1970年至今的秒数)
	 * 
	 * @param unixTime
	 *            1970年至今的秒数
	 * @return
	 */
	public static int getUnixTimeByDate(Date date) {
		return (int) (date.getTime() / 1000);
	}
	
	public static long getUnixTimeLong(Date date) {
		return (date.getTime() / 1000);
	}

	public static Date getNextDay(Date date) {
		long time = (date.getTime() / 1000) + 60 * 60 * 24;
		date.setTime(time * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = format.parse(format.format(date));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return date;

	}

	/**
	 * @param date
	 * @return 复制新Date，不改变参数
	 */
	public static Date nextDay(Date date) {
		Date newDate = (Date) date.clone();
		long time = (newDate.getTime() / 1000) + 60 * 60 * 24;
		newDate.setTime(time * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			newDate = format.parse(format.format(newDate));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return newDate;

	}

	public static Date getNowTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String dateStr = dateFormat(date);
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date getTomorrow(Date date1) {

		// 创建当前时间对象
		Calendar now = Calendar.getInstance();
		now.setTime(date1);
		// 日期[+1]day
		now.add(Calendar.DATE, 1);
		return now.getTime();
	}

	public static Date getWeekAgo(Date date) {
		Date newDate = (Date) date.clone();
		long time = (newDate.getTime() / 1000) - 60 * 60 * 24 * 7;
		newDate.setTime(time * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			newDate = format.parse(format.format(newDate));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return newDate;
	}

	public static Date getDatebyTime(Date date, int n) {
		String str = DateKit.dateFormat(date, "yyyy-MM-dd");
		String[] strs = StringKit.split(str, "-");
		int month = Integer.parseInt(strs[1]);
		int monthnow = (month + n) % 12;
		int year = Integer.parseInt(strs[0]) + (month + n) / 12;
		str = String.valueOf(year) + "-" + String.valueOf(monthnow) + "-" + strs[2];
		return DateKit.dateFormat(str, "yyyy-MM-dd");
	}

	/**
	 * @param date
	 * @return 复制新Date，不改变参数
	 */
	public static Date yesterday(Date date) {
		Date newDate = (Date) date.clone();
		long time = (newDate.getTime() / 1000) - 60 * 60 * 24;
		newDate.setTime(time * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			newDate = format.parse(format.format(newDate));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return newDate;
	}

	public static Date getYesterday(Date date) {
		long time = (date.getTime() / 1000) - 60 * 60 * 24;
		date.setTime(time * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = format.parse(format.format(date));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return date;
	}

	public static String getStringNowTime() {
		Date date = new Date();
		String dateStr = dateFormat(date);
		return dateStr;
	}

	/**
	 * 指定时间的秒数 指定时间零点的秒数加指定天数的秒数
	 * 
	 * @param time
	 *            时间
	 * @param range
	 *            天
	 * @return
	 */
	public static long getSpecifyTimeSec(long time, int range) {
		Date date = new Date((time * 1000 + (23 - Calendar.ZONE_OFFSET) * 3600000) / 86400000 * 86400000
				- (23 - Calendar.ZONE_OFFSET) * 3600000);
		long zeroTime = date.getTime() / 1000;
		long specifyTime = range * 24 * 3600;
		return (zeroTime + specifyTime);
	}

	/**
	 * 将int型时间(1970年至今的秒数)转换成指定格式的时间
	 * 
	 * @param unixTime
	 *            1970年至今的秒数
	 * @param dateFormat
	 *            时间格式
	 * @return
	 */
	public static String formatDateByUnixTime(long unixTime, String dateFormat) {
		return dateFormat(new Date(unixTime * 1000), dateFormat);
	}
	
	private static List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>(12) {
		private static final long serialVersionUID = 2249396579858199535L;
		{
			add(new SimpleDateFormat("yyyy-MM-dd"));
			add(new SimpleDateFormat("yyyy/MM/dd"));
			add(new SimpleDateFormat("yyyy.MM.dd"));
			add(new SimpleDateFormat("yyyy-MM-dd HH:24:mm:ss"));
			add(new SimpleDateFormat("yyyy/MM/dd HH:24:mm:ss"));
			add(new SimpleDateFormat("yyyy.MM.dd HH:24:mm:ss"));
		    add(new SimpleDateFormat("M/dd/yyyy"));
		    add(new SimpleDateFormat("dd.M.yyyy"));
		    add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss a"));
		    add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss a"));
		    add(new SimpleDateFormat("dd.MMM.yyyy"));
		    add(new SimpleDateFormat("dd-MMM-yyyy"));
		}
	};
	
	public static Date convertToDate(String input) {
	    Date date = null;
	    if(null == input) {
	        return null;
	    }
	    for (SimpleDateFormat format : dateFormats) {
	        try {
	            format.setLenient(false);
	            date = format.parse(input);
	        } catch (ParseException e) {
	            //Shhh.. try other formats
	        }
	        if (date != null) {
	            break;
	        }
	    }
	    return date;
	}

	public static Long getTodayTime() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		return Long.valueOf(String.valueOf(today.getTimeInMillis()).substring(0, 10));
	}
	
	public static Long getYesterdayTime() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, -24);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		return Long.valueOf(String.valueOf(today.getTimeInMillis()).substring(0, 10));
	}

	public static Long getTomorrowTime() {
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.set(Calendar.HOUR_OF_DAY, 24);
		tomorrow.set(Calendar.MINUTE, 0);
		tomorrow.set(Calendar.SECOND, 0);
		return Long.valueOf(String.valueOf(tomorrow.getTimeInMillis()).substring(0, 10));
	}
	
}