package dtri.com.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Fm_Time_Model {

	private static SimpleDateFormat format_yyyyMMdd_HHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat format_HHmm = new SimpleDateFormat("HH:mm");
	private static SimpleDateFormat format_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/** 格式:yyyy-MM-dd HH:mm:ss **/
	public static String to_yMd_Hms(Date date) {
		return format_yyyyMMdd_HHmmss.format(date);
	}

	/** 格式:HH:mm:ss **/
	public static String to_Hms(Date date) {
		String Hms = format_HHmm.format(date) + ":00";
		return Hms;
	}

	/** 格式:yyyyMMdd **/
	public static String to_yMd(Date date) {
		return format_yyyyMMdd.format(date);
	}

	/** 格式:yyyy-MM-dd **/
	public static String to_yyMMdd(Date date) {
		return sdf.format(date);
	}

	/** 格式:yyyy-MM-dd **/
	public static String to_yyMMdd(String date) {
		try {
			date = sdf.format(format_yyyyMMdd.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
			date = "";
		}
		return date;
	}

	/** 計算:加/減 天數 **/
	public static Date to_count(Integer n, Date dt) {
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, n);
		dt = c.getTime();
		return dt;
	}

	/** yyyy-MM-dd to Date **/
	public static Date toDate(String dt) {
		try {
			return sdf.parse(dt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** yyyy-MM-dd HH:mm:ss to Date **/
	public static Date toDateHHmmss(String dt) {
		try {
			return format_yyyyMMdd_HHmmss.parse(dt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** get Date to week **/
	public static int getWeek(Date date) {
		try {
			// 今年
			Date dateNow = new Date();
			Calendar calNow = Calendar.getInstance(Locale.TAIWAN);
			calNow.setMinimalDaysInFirstWeek(7);
			calNow.setTime(dateNow);
			int yearNow = calNow.get(Calendar.YEAR);

			// 預計日
			Calendar cal = Calendar.getInstance(Locale.TAIWAN);
			cal.setMinimalDaysInFirstWeek(7);
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			// 週期(可能是在每年最後 交替會出現異常 由取代)
			if (month == 0 && yearNow <= year && day < 7 && week >= 52) {
				week = 0;
			}

			return week;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/** get Date to Year **/
	public static int getYear(Date date) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR);
			return year;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
