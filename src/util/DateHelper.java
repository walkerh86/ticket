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
 * Description:����ʱ������Ĺ�����
 * 
 */
public class DateHelper {
	/** ���ڸ�ʽ(yyyy) */
	public static final String yyyy_EN = "yyyy";
	/** ���ڸ�ʽ(yyyy-MM-dd) */
	public static final String yyyy_MM_dd_EN = "yyyy-MM-dd";

	/** ���ڸ�ʽ(yyyy-MM-dd HH:mm) */
	public static final String yyyy_MM_dd_HH_mm_EN = "yyyy-MM-dd HH:mm";

	/** ���ڸ�ʽ(yyyy-MM-dd HH) */
	public static final String yyyy_MM_dd_HH_EN = "yyyy-MM-dd HH";

	/** ���ڸ�ʽ(yyyyMMdd) */
	public static final String yyyyMMdd_EN = "yyyyMMdd";

	/** ���ڸ�ʽ(yyyy-MM) */
	public static final String yyyy_MM_EN = "yyyy-MM";

	/** ���ڸ�ʽ(yyyyMM) */
	public static final String yyyyMM_EN = "yyyyMM";

	/** ���ڸ�ʽ(yyyy-MM-dd HH:mm:ss) */
	public static final String yyyy_MM_dd_HH_mm_ss_EN = "yyyy-MM-dd HH:mm:ss";

	/** ���ڸ�ʽ(yyyyMMddHHmmss) */
	public static final String yyyyMMddHHmmss_EN = "yyyyMMddHHmmss";

	/** ���ڸ�ʽ(yyyy��MM��dd��) */
	public static final String yyyy_MM_dd_CN = "yyyy��MM��dd��";

	/** ���ڸ�ʽ(yyyy��MM��dd��HHʱmm��ss��) */
	public static final String yyyy_MM_dd_HH_mm_ss_CN = "yyyy��MM��dd��HHʱmm��ss��";

	/** ���ڸ�ʽ(yyyy��MM��dd��HHʱmm��) */
	public static final String yyyy_MM_dd_HH_mm_CN = "yyyy��MM��dd��HHʱmm��";

	/** DateFormat���� */
	private static Map<String, DateFormat> dateFormatMap = new HashMap<String, DateFormat>();

	/**
	 * ��ȡDateFormat
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
	 * ����ת�����ַ���
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
	 * ����ת�����ַ���
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
	 * ����Ĭ��formatStr�ĸ�ʽ��ת��dateTimeStrΪDate���� dateTimeStr������formatStr����ʽ
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
	 * ת��dateTimeStrΪDate����
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
	 * ����Ĭ����ʾ����ʱ��ĸ�ʽ"yyyy-MM-dd"��ת��dateTimeStrΪDate����
	 * dateTimeStr������"yyyy-MM-dd"����ʽ
	 * 
	 * @param dateTimeStr
	 * @return
	 */
	public static Date getDate(String dateTimeStr) {
		return getDate(dateTimeStr, yyyy_MM_dd_EN);
	}

	/**
	 * ��YYYYMMDDת����Date����
	 * 
	 * @param date
	 * @return
	 * @throws BusinessException
	 */
	public static Date transferDate(String date) throws Exception {
		if (date == null || date.length() < 1)
			return null;

		if (date.length() != 8)
			throw new Exception("���ڸ�ʽ����");
		String con = "-";

		String yyyy = date.substring(0, 4);
		String mm = date.substring(4, 6);
		String dd = date.substring(6, 8);

		int month = Integer.parseInt(mm);
		int day = Integer.parseInt(dd);
		if (month < 1 || month > 12 || day < 1 || day > 31)
			throw new Exception("���ڸ�ʽ����");

		String str = yyyy + con + mm + con + dd;
		return DateHelper.getDate(str, DateHelper.yyyy_MM_dd_EN);
	}

	/**
	 * ��Dateת�����ַ�����yyyy-mm-dd hh:mm:ss�����ַ���
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToDateString(Date date) {
		return dateToDateString(date, yyyy_MM_dd_HH_mm_ss_EN);
	}

	/**
	 * ��Dateת����formatStr��ʽ���ַ���
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
	 * ��Stringת����formatStr��ʽ���ַ���
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
	 * ��ȡ��ǰ����yyyy-MM-dd����ʽ
	 * 
	 * @return
	 */
	public static String getCurDate() {
		return dateToDateString(new Date(), yyyy_MM_dd_EN);
	}

	/**
	 * ��ȡ��ǰ����yyyy��MM��dd�յ���ʽ
	 * 
	 * @return
	 */
	public static String getCurCNDate() {
		return dateToDateString(new Date(), yyyy_MM_dd_CN);
	}

	/**
	 * ��ȡ��ǰ����ʱ��yyyy-MM-dd HH:mm:ss����ʽ
	 * 
	 * @return
	 */
	public static String getCurDateTime() {
		return dateToDateString(new Date(), yyyy_MM_dd_HH_mm_ss_EN);
	}

	/**
	 * ��ȡ��ǰ����ʱ��yyyy��MM��dd��HHʱmm��ss�����ʽ
	 * 
	 * @return
	 */
	public static String getCurZhCNDateTime() {
		return dateToDateString(new Date(), yyyy_MM_dd_HH_mm_ss_CN);
	}

	/**
	 * �Ƚ�����"yyyy-MM-dd"��ʽ�����ڣ�֮�������ٺ���,time2-time1
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
	 * �Ƚ�����"yyyy-MM-dd HH:mm:ss"��ʽ�����ڵĴ�С
	 * 
	 * @param time1
	 * @param time2
	 * @return boolean true��һ��ʱ���
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
	 * ��Сʱ������ɷ����Ժ���Ϊ��λ��ʱ��
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
	 * ��ȡ��ǰ����years����һ��(formatStr)���ַ���
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
	 * ��ȡ����mon�º��һ��(formatStr)���ַ���
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
	 * ��ȡ��ǰ����days����һ��(formatStr)���ַ���
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
	 * ��ȡ��ǰ����hoursСʱ���һ��(formatStr)���ַ���
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
	 * ��ȡָ������mon�º��һ��(formatStr)���ַ���
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
	 * ��ȡָ������day����һ��(formatStr)���ַ���
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
	 * ��ȡָ������mins���Ӻ��һ��(formatStr)���ַ���
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
	 * ��ȡ��ǰ����mins���Ӻ��һ��(formatStr)���ַ���
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
	 * ��ȡ��ǰ����sec����һ��(formatStr)���ַ���
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
	 * ���ָ�������·ݵ�����
	 * 
	 * @return
	 */
	public static int getMonthDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);

	}

	/**
	 * ���ϵͳ��ǰ�·ݵ�����
	 * 
	 * @return
	 */
	public static int getCurentMonthDay() {
		Date date = Calendar.getInstance().getTime();
		return getMonthDay(date);
	}

	/**
	 * ���ָ�������·ݵ����� yyyy-mm-dd
	 * 
	 * @return
	 */
	public static int getMonthDay(String date) {
		Date strDate = getDate(date, yyyy_MM_dd_EN);
		return getMonthDay(strDate);
	}

	/**
	 * ��ȡ19xx,20xx��ʽ����
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
	 * ��ȡ�·ݣ�1-12��
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
	 * ��ȡxxxx-xx-xx����
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
	 * ��ȡDate�е�Сʱ(24Сʱ)
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
	 * ��ȡDate�еķ���
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
	 * ��ȡDate�е���
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
	 * �õ�������һ
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
	 * �õ���������
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
	 * �õ�������(*)
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
	 * �õ�����ָ����
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
	 * ��ȡÿ�µ����һ��
	 * 
	 * @param dateStr
	 *            Ҫ��ʽ��������(yyyy_mm_dd)
	 * @param dateFormat
	 *            ��ʽ���ַ���
	 * @return
	 */
	public static String getDayOfLastDay(String dateStr, String dateFormat) {
		Date startDate = getDate(dateStr, yyyy_MM_dd_EN);
		// ��ʼʱ��
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		startCal.set(Calendar.DATE, 1);// ��Ϊ��ǰ�µ�1��
		startCal.add(Calendar.MONTH, 1);// ��һ���£���Ϊ���µ�1��
		startCal.add(Calendar.DATE, -1);// ��ȥһ�죬��Ϊ�������һ��
		return dateToDateString(startCal.getTime(), dateFormat);
	}

	/**
	 * �õ������������������������ַ���
	 * 
	 * @param startDate
	 *            ��ʼ����
	 * @param endDate
	 *            ��������
	 * @param dateFroamt
	 *            ���ڸ�ʽ�ַ���
	 * @throws ParseException
	 */
	public static List<String> printDate(String startDate, String endDate, String dateFormat) throws ParseException {
		List<String> dateString = new ArrayList<String>();
		DateFormat format = getDateFormat(dateFormat);
		// ������ʼʱ���(�õ������ڻ������ʼʱ���һ��)
		Calendar calStartDate = Calendar.getInstance();
		calStartDate.setTime(format.parse(startDate));
		// �õ�ʵ����ʵʱ���
		calStartDate.add(Calendar.DATE, -1);
		// ���ý���ʱ���
		Calendar calEndDate = Calendar.getInstance();
		calEndDate.setTime(format.parse(endDate));
		while (calStartDate.before(calEndDate)) {
			calStartDate.add(Calendar.DAY_OF_YEAR, 1);
			dateString.add(format.format(calStartDate.getTime()));

		}
		return dateString;
	}

	/**
	 * �õ������·����������������ַ���
	 * 
	 * @param startDate
	 *            ��ʼ����
	 * @param endDate
	 *            ��������
	 * @param dateFroamt
	 *            ���ڸ�ʽ�ַ���
	 */
	public static List<String> printMonth(String startDate, String endDate, String dateFormat) {
		List<String> dateStr = new ArrayList<String>();
		// ���ڸ�ʽ��ʵ��
		DateFormat format = getDateFormat(dateFormat);
		Date startMonth = getDate(startDate, yyyy_MM_EN);
		// ��ʼʱ��
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startMonth);
		// ����ʱ��
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
	 * �õ��������������ַ���
	 * 
	 * @param startDate
	 *            ��ʼ����
	 * @param endDate
	 *            ��������
	 * @param dateFroamt
	 *            ���ڸ�ʽ�ַ���
	 */
	public static List<String> printYear(String startDate, String endDate, String dateFormat) {
		List<String> dateStr = new ArrayList<String>();
		// ���ڸ�ʽ��ʵ��
		DateFormat format = getDateFormat(dateFormat);
		Date startYear = getDate(startDate, yyyy_EN);
		// ��ʼʱ��
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startYear);
		// ����ʱ��
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
	 * ��Calendarת����formatStr��ʽ���ַ��� ղΰ
	 * 
	 * @param date
	 * @param formatStr
	 * @return
	 */
	public static String dateToCalendarString(Calendar date, String formatStr) {
		SimpleDateFormat df = new SimpleDateFormat(formatStr);// ��������Ҫ�ĸ�ʽ
		return df.format(date.getTime());
	}
}

