package ltd.finelink.tool.disk.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jimmy
 *
 */
@Slf4j
public class DateUtil {

	/**
	 * 日期时间操作
	 * 
	 * @param date
	 *            操作的日期
	 * @param year
	 *            添加年数
	 * @param month
	 *            添加月数
	 * @param day
	 *            添加天数
	 * @param hour
	 *            添加小时
	 * @param minute
	 *            添加分
	 * @param second
	 *            添加秒
	 * @return
	 */
	public static Date addTime(Date date, Integer year, Integer month, Integer day, Integer hour, Integer minute,
			Integer second) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		} else {
			return null;
		}
		if (year != null) {
			cal.add(Calendar.YEAR, year);
		}
		if (month != null) {
			cal.add(Calendar.MONTH, month);
		}
		if (day != null) {
			cal.add(Calendar.DAY_OF_YEAR, day);
		}
		if (hour != null) {
			cal.add(Calendar.HOUR_OF_DAY, hour);
		}

		if (minute != null) {
			cal.add(Calendar.MINUTE, minute);
		}
		if (second != null) {
			cal.add(Calendar.SECOND, second);
		}
		return cal.getTime();
	}

	/**
	 * 为日期添加年
	 * 
	 * @param date
	 *            操作日期
	 * @param year
	 *            年数
	 * @return
	 */
	public static Date addYear(Date date, int year) {
		return addTime(date, year, null, null, null, null, null);
	}

	/**
	 * 为日期添加月
	 * 
	 * @param date
	 *            操作日期
	 * @param month
	 *            月
	 * @return
	 */
	public static Date addMonth(Date date, int month) {
		return addTime(date, null, month, null, null, null, null);
	}

	/**
	 * 为日期添加天
	 * 
	 * @param date
	 *            操作日期
	 * @param day
	 *            天
	 * @return
	 */
	public static Date addDay(Date date, int day) {
		return addTime(date, null, null, day, null, null, null);
	}

	/**
	 * 为日期添加小时
	 * 
	 * @param date
	 *            操作日期
	 * @param hour
	 *            小时
	 * @return
	 */
	public static Date addHour(Date date, int hour) {
		return addTime(date, null, null, null, hour, null, null);
	}

	/**
	 * 为日期添加分钟
	 * 
	 * @param date
	 *            操作日期
	 * @param minute
	 *            分钟
	 * @return
	 */
	public static Date addMinute(Date date, int minute) {
		return addTime(date, null, null, null, null, minute, null);
	}

	/**
	 * 为日期添加秒
	 * 
	 * @param date
	 *            操作日期
	 * @param second
	 *            秒
	 * @return
	 */
	public static Date addSecond(Date date, int second) {
		return addTime(date, null, null, null, null, null, second);
	}

	/**
	 * 两个日期相减
	 * 
	 * @param main
	 * @param other
	 * @return
	 */
	public static long timeDiff(Date main, Date other) {
		return main.getTime() - other.getTime();

	}

	/**
	 * 去除日期时分秒 
	 * @param date
	 * @return 日期为 YYY-MM-DD 00:00:00
	 */
	public static Date trimTime(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	/**
	 * 比较两个日期是否为同一天
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDay(Date date1,Date date2) {
		Calendar c = Calendar.getInstance();
		c.setTime(date1); 
		int day = c.get(Calendar.DAY_OF_YEAR);
		int year = c.get(Calendar.YEAR);
		c.setTime(date2);
		if (c.get(Calendar.DAY_OF_YEAR) == day && c.get(Calendar.YEAR) == year) {
			return true;
		} 
		return false;
	}
	
	 
	/**
	 * 字符串转日期
	 * @param date 日期字符串
	 * @param pattern 格式
	 * @return
	 */
	public static Date parse(String date, String pattern) {
		try {
			return DateUtils.parseDate(date,pattern);
		} catch (ParseException e) {
			log.error(e.getMessage(),e);
			return null;
		}
	}
	
	 
	/**
	 * 格式化日期
	 * @param date 日期
	 * @param pattern 格式
	 * @return
	 */
	public static String format(Date date, String pattern) {
		return DateFormatUtils.format(date, pattern,TimeZone.getDefault());
	}

}
