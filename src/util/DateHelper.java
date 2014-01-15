package util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 
 * Description:日期时间操作的工具类
 * 
 */
public class DateHelper {
	/** 日期格式(yyyy) */
	public static final String yyyy_EN = "yyyy";
	/** 日期格式(yyyy-MM-dd) */
	public static final String yyyy_MM_dd_EN = "yyyy-MM-dd";

	/** 日期格式(yyyy-MM-dd HH:mm) */
	public static final String yyyy_MM_dd_HH_mm_EN = "yyyy-MM-dd HH:mm";

	/** 日期格式(yyyy-MM-dd HH) */
	public static final String yyyy_MM_dd_HH_EN = "yyyy-MM-dd HH";

	/** 日期格式(yyyyMMdd) */
	public static final String yyyyMMdd_EN = "yyyyMMdd";

	/** 日期格式(yyyy-MM) */
	public static final String yyyy_MM_EN = "yyyy-MM";

	/** 日期格式(yyyyMM) */
	public static final String yyyyMM_EN = "yyyyMM";

	/** 日期格式(yyyy-MM-dd HH:mm:ss) */
	public static final String yyyy_MM_dd_HH_mm_ss_EN = "yyyy-MM-dd HH:mm:ss";

	/** 日期格式(yyyyMMddHHmmss) */
	public static final String yyyyMMddHHmmss_EN = "yyyyMMddHHmmss";

	/** 日期格式(yyyy年MM月dd日) */
	public static final String yyyy_MM_dd_CN = "yyyy年MM月dd日";

	/** 日期格式(yyyy年MM月dd日HH时mm分ss秒) */
	public static final String yyyy_MM_dd_HH_mm_ss_CN = "yyyy年MM月dd日HH时mm分ss秒";

	/** 日期格式(yyyy年MM月dd日HH时mm分) */
	public static final String yyyy_MM_dd_HH_mm_CN = "yyyy年MM月dd日HH时mm分";

	/** DateFormat缓存 */
	private static Map<String, DateFormat> dateFormatMap = new HashMap<String, DateFormat>();

	/**
	 * 获取DateFormat
	 * 
	 * @param dateTimeStr
	 * @param formatStr
	 * @return
	 */
	public static DateFormat getDateFormat(String formatStr) {
		DateFormat df = dateFormatMap.get(formatStr);
		if (df == null) {
			df = new SimpleDateFormat(formatStr);
			dateFormatMap.put(formatStr, df);
		}
		return df;
	}

	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 * @return str
	 */

	public static String DateToStr(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(yyyy_MM_dd_EN);
		if (date != null) {
			String str = format.format(date);
			return str;
		} else {
			return null;
		}

	}

	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 * @return str
	 */

	public static String DateToStr(Date date, String formatStr,Locale local) {
		if(local == null){
			local = local.ENGLISH;
		}
		SimpleDateFormat format = new SimpleDateFormat(formatStr, local);
		format.setTimeZone(TimeZone.getDefault());
		if (date != null) {
			String str = format.format(date);
			return str;
		} else {
			return null;
		}

	}

	/**
	 * 按照默认formatStr的格式，转化dateTimeStr为Date类型 dateTimeStr必须是formatStr的形式
	 * 
	 * @param dateTimeStr
	 * @param formatStr
	 * @return
	 */
	public static Date getDate(String dateTimeStr, String formatStr) {
		try {
			if (dateTimeStr == null || dateTimeStr.equals("")) {
				return null;
			}
			DateFormat sdf = DateHelper.getDateFormat(formatStr);
			Date d = sdf.parse(dateTimeStr);
			return d;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 转化dateTimeStr为Date类型
	 * 
	 * @param dateTimeStr
	 * @param formatStr
	 * @return
	 */
	public static Date convertDate(String dateTimeStr) {
		try {
			if (dateTimeStr == null || dateTimeStr.equals("")) {
				return null;
			}
			DateFormat sdf = DateHelper.getDateFormat(yyyy_MM_dd_HH_mm_ss_EN);
			Date d = sdf.parse(dateTimeStr);
			return d;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 按照默认显示日期时间的格式"yyyy-MM-dd"，转化dateTimeStr为Date类型
	 * dateTimeStr必须是"yyyy-MM-dd"的形式
	 * 
	 * @param dateTimeStr
	 * @return
	 */
	public static Date getDate(String dateTimeStr) {
		return getDate(dateTimeStr, yyyy_MM_dd_EN);
	}

	/**
	 * 将YYYYMMDD转换成Date日期
	 * 
	 * @param date
	 * @return
	 * @throws BusinessException
	 */
	public static Date transferDate(String date) throws Exception {
		if (date == null || date.length() < 1)
			return null;

		if (date.length() != 8)
			throw new Exception("日期格式错误");
		String con = "-";

		String yyyy = date.substring(0, 4);
		String mm = date.substring(4, 6);
		String dd = date.substring(6, 8);

		int month = Integer.parseInt(mm);
		int day = Integer.parseInt(dd);
		if (month < 1 || month > 12 || day < 1 || day > 31)
			throw new Exception("日期格式错误");

		String str = yyyy + con + mm + con + dd;
		return DateHelper.getDate(str, DateHelper.yyyy_MM_dd_EN);
	}

	/**
	 * 将Date转换成字符串“yyyy-mm-dd hh:mm:ss”的字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToDateString(Date date) {
		return dateToDateString(date, yyyy_MM_dd_HH_mm_ss_EN);
	}

	/**
	 * 将Date转换成formatStr格式的字符串
	 * 
	 * @param date
	 * @param formatStr
	 * @return
	 */
	public static String dateToDateString(Date date, String formatStr) {
		DateFormat df = getDateFormat(formatStr);
		return df.format(date);
	}

	/**
	 * 将String转换成formatStr格式的字符串
	 * 
	 * @param dateTime
	 * @param formatStr1
	 * @param formatStr2
	 * @return
	 */
	public static String stringToDateString(String date, String formatStr1, String formatStr2) {
		Date d = getDate(date, formatStr1);
		DateFormat df = getDateFormat(formatStr2);

		return df.format(d);
	}

	/**
	 * 获取当前日期yyyy-MM-dd的形式
	 * 
	 * @return
	 */
	public static String getCurDate() {
		return dateToDateString(new Date(), yyyy_MM_dd_EN);
	}

	/**
	 * 获取当前日期yyyy年MM月dd日的形式
	 * 
	 * @return
	 */
	public static String getCurCNDate() {
		return dateToDateString(new Date(), yyyy_MM_dd_CN);
	}

	/**
	 * 获取当前日期时间yyyy-MM-dd HH:mm:ss的形式
	 * 
	 * @return
	 */
	public static String getCurDateTime() {
		return dateToDateString(new Date(), yyyy_MM_dd_HH_mm_ss_EN);
	}

	/**
	 * 获取当前日期时间yyyy年MM月dd日HH时mm分ss秒的形式
	 * 
	 * @return
	 */
	public static String getCurZhCNDateTime() {
		return dateToDateString(new Date(), yyyy_MM_dd_HH_mm_ss_CN);
	}

	/**
	 * 比较两个"yyyy-MM-dd"格式的日期，之间相差多少毫秒,time2-time1
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long compareDateStr(String time1, String time2) {
		Date d1 = getDate(time1);
		Date d2 = getDate(time2);
		return d2.getTime() - d1.getTime();
	}

	/**
	 * 比较两个"yyyy-MM-dd HH:mm:ss"格式的日期的大小
	 * 
	 * @param time1
	 * @param time2
	 * @return boolean true第一个时间大
	 */
	public static boolean compareDateStrTime(String time1, String time2) {
		boolean b = false;
		Date d1 = getDate(time1, yyyy_MM_dd_HH_mm_ss_EN);
		Date d2 = getDate(time2, yyyy_MM_dd_HH_mm_ss_EN);
		long temp = d1.getTime() - d2.getTime();
		if (temp >= 0) {
			b = true;
		}
		return b;
	}

	/**
	 * 将小时数换算成返回以毫秒为单位的时间
	 * 
	 * @param hours
	 * @return
	 */
	public static long getMicroSec(BigDecimal hours) {
		BigDecimal bd;
		bd = hours.multiply(new BigDecimal(3600 * 1000));
		return bd.longValue();
	}

	/**
	 * 获取当前日期years年后的一个(formatStr)的字符串
	 * 
	 * @param months
	 * @param formatStr
	 * @return
	 */
	public static String getDateStringOfYear(int years, String formatStr) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(new Date());
		now.add(Calendar.YEAR, years);
		return dateToDateString(now.getTime(), formatStr);
	}

	/**
	 * 获取日期mon月后的一个(formatStr)的字符串
	 * 
	 * @param months
	 * @param formatStr
	 * @return
	 */
	public static String getDateStringOfMon(String startTime, int months, String formatStr) {
		Calendar now = Calendar.getInstance();
		now.setTime(getDate(startTime, yyyy_MM_EN));
		now.add(Calendar.MONTH, months);
		return dateToDateString(now.getTime(), formatStr);
	}

	/**
	 * 获取当前日期days天后的一个(formatStr)的字符串
	 * 
	 * @param days
	 * @param formatStr
	 * @return
	 */
	public static String getDateStringOfDay(int days, String formatStr) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(new Date());
		now.add(Calendar.DATE, days);
		return dateToDateString(now.getTime(), formatStr);
	}

	/**
	 * 获取当前日期hours小时后的一个(formatStr)的字符串
	 * 
	 * @param hours
	 * @param formatStr
	 * @return
	 */
	public static String getDateStringOfHour(int hours, String formatStr) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(new Date());
		now.add(Calendar.HOUR_OF_DAY, hours);
		return dateToDateString(now.getTime(), formatStr);
	}

	/**
	 * 获取指定日期mon月后的一个(formatStr)的字符串
	 * 
	 * @param date
	 * @param mins
	 * @param formatStr
	 * @return
	 */
	public static String getDateOfMon(String date, int mon, String formatStr) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(DateHelper.getDate(date, formatStr));
		now.add(Calendar.MONTH, mon);
		return dateToDateString(now.getTime(), formatStr);
	}

	/**
	 * 获取指定日期day天后的一个(formatStr)的字符串
	 * 
	 * @param date
	 * @param mins
	 * @param formatStr
	 * @return
	 */
	public static String getDateOfDay(String date, int day, String formatStr) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(DateHelper.getDate(date, formatStr));
		now.add(Calendar.DATE, day);
		return dateToDateString(now.getTime(), formatStr);
	}

	/**
	 * 获取指定日期mins分钟后的一个(formatStr)的字符串
	 * 
	 * @param date
	 * @param mins
	 * @param formatStr
	 * @return
	 */
	public static String getDateOfMin(String date, int mins, String formatStr) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(DateHelper.getDate(date, formatStr));
		now.add(Calendar.SECOND, mins * 60);
		return dateToDateString(now.getTime(), formatStr);
	}

	/**
	 * 获取当前日期mins分钟后的一个(formatStr)的字符串
	 * 
	 * @param mins
	 * @param formatStr
	 * @return
	 */
	public static String getDateStringOfMin(int mins, String formatStr) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(new Date());
		now.add(Calendar.MINUTE, mins);
		return dateToDateString(now.getTime(), formatStr);
	}

	/**
	 * 获取当前日期sec秒后的一个(formatStr)的字符串
	 * 
	 * @param sec
	 * @param formatStr
	 * @return
	 */
	public static String getDateStringOfSec(int sec, String formatStr) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(new Date());
		now.add(Calendar.SECOND, sec);
		return dateToDateString(now.getTime(), formatStr);
	}

	/**
	 * 获得指定日期月份的天数
	 * 
	 * @return
	 */
	public static int getMonthDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);

	}

	/**
	 * 获得系统当前月份的天数
	 * 
	 * @return
	 */
	public static int getCurentMonthDay() {
		Date date = Calendar.getInstance().getTime();
		return getMonthDay(date);
	}

	/**
	 * 获得指定日期月份的天数 yyyy-mm-dd
	 * 
	 * @return
	 */
	public static int getMonthDay(String date) {
		Date strDate = getDate(date, yyyy_MM_dd_EN);
		return getMonthDay(strDate);
	}

	/**
	 * 获取19xx,20xx形式的年
	 * 
	 * @param d
	 * @return
	 */
	public static int getYear(Date d) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(d);
		return now.get(Calendar.YEAR);
	}

	/**
	 * 获取月份，1-12月
	 * 
	 * @param d
	 * @return
	 */
	public static int getMonth(Date d) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(d);
		return now.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取xxxx-xx-xx的日
	 * 
	 * @param d
	 * @return
	 */
	public static int getDay(Date d) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(d);
		return now.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取Date中的小时(24小时)
	 * 
	 * @param d
	 * @return
	 */
	public static int getHour(Date d) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(d);
		return now.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取Date中的分钟
	 * 
	 * @param d
	 * @return
	 */
	public static int getMin(Date d) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(d);
		return now.get(Calendar.MINUTE);
	}

	/**
	 * 获取Date中的秒
	 * 
	 * @param d
	 * @return
	 */
	public static int getSecond(Date d) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(d);
		return now.get(Calendar.SECOND);
	}

	/**
	 * 得到本周周一
	 * 
	 * @return yyyy-MM-dd
	 */
	public static String getMondayOfThisWeek() {
		Calendar c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 1);
		return dateToDateString(c.getTime(), yyyy_MM_dd_EN);
	}

	/**
	 * 得到本周周日
	 * 
	 * @return yyyy-MM-dd
	 */
	public static String getSundayOfThisWeek() {
		Calendar c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 7);
		return dateToDateString(c.getTime());
	}

	/**
	 * 得到本周周(*)
	 * 
	 * @return yyyy-MM-dd
	 */
	public static String getDayOfThisWeek(int num) {
		Calendar c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + num);
		return dateToDateString(c.getTime(), yyyy_MM_dd_EN);
	}

	/**
	 * 得到本月指定天
	 * 
	 * @return yyyy-MM-dd
	 */
	public static String getDayOfThisMoon(String num) {
		String date = dateToDateString(new Date(), yyyy_MM_EN);
		date = date + "-" + num;
		return date;
	}

	/**
	 * 
	 * 获取每月的最后一天
	 * 
	 * @param dateStr
	 *            要格式话的日期(yyyy_mm_dd)
	 * @param dateFormat
	 *            格式化字符串
	 * @return
	 */
	public static String getDayOfLastDay(String dateStr, String dateFormat) {
		Date startDate = getDate(dateStr, yyyy_MM_dd_EN);
		// 开始时间
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		startCal.set(Calendar.DATE, 1);// 设为当前月的1号
		startCal.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
		startCal.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天
		return dateToDateString(startCal.getTime(), dateFormat);
	}

	/**
	 * 得到两个日期相隔的日期区间段字符串
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param dateFroamt
	 *            日期格式字符串
	 * @throws ParseException
	 */
	public static List<String> printDate(String startDate, String endDate, String dateFormat) throws ParseException {
		List<String> dateString = new ArrayList<String>();
		DateFormat format = getDateFormat(dateFormat);
		// 设置起始时间段(得到的日期会大于起始时间段一天)
		Calendar calStartDate = Calendar.getInstance();
		calStartDate.setTime(format.parse(startDate));
		// 得到实际其实时间段
		calStartDate.add(Calendar.DATE, -1);
		// 设置结束时间段
		Calendar calEndDate = Calendar.getInstance();
		calEndDate.setTime(format.parse(endDate));
		while (calStartDate.before(calEndDate)) {
			calStartDate.add(Calendar.DAY_OF_YEAR, 1);
			dateString.add(format.format(calStartDate.getTime()));

		}
		return dateString;
	}

	/**
	 * 得到两个月份相隔的日期区间段字符串
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param dateFroamt
	 *            日期格式字符串
	 */
	public static List<String> printMonth(String startDate, String endDate, String dateFormat) {
		List<String> dateStr = new ArrayList<String>();
		// 日期格式化实体
		DateFormat format = getDateFormat(dateFormat);
		Date startMonth = getDate(startDate, yyyy_MM_EN);
		// 开始时间
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startMonth);
		// 结束时间
		Date endMonth = getDate(endDate, yyyy_MM_EN);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endMonth);

		while (startCal.before(endCal)) {
			dateStr.add(format.format(startCal.getTime()));
			startCal.add(Calendar.MONTH, 1);
		}
		dateStr.add(format.format(startCal.getTime()));
		return dateStr;
	}

	/**
	 * 得到两个年份区间段字符串
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param dateFroamt
	 *            日期格式字符串
	 */
	public static List<String> printYear(String startDate, String endDate, String dateFormat) {
		List<String> dateStr = new ArrayList<String>();
		// 日期格式化实体
		DateFormat format = getDateFormat(dateFormat);
		Date startYear = getDate(startDate, yyyy_EN);
		// 开始时间
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startYear);
		// 结束时间
		Date endYear = getDate(endDate, yyyy_EN);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endYear);

		while (startCal.before(endCal)) {
			dateStr.add(format.format(startCal.getTime()));
			startCal.add(Calendar.YEAR, 1);
		}
		dateStr.add(format.format(startCal.getTime()));
		return dateStr;
	}

	/**
	 * 将Calendar转换成formatStr格式的字符串 詹伟
	 * 
	 * @param date
	 * @param formatStr
	 * @return
	 */
	public static String dateToCalendarString(Calendar date, String formatStr) {
		SimpleDateFormat df = new SimpleDateFormat(formatStr);// 设置你想要的格式
		return df.format(date.getTime());
	}
}

