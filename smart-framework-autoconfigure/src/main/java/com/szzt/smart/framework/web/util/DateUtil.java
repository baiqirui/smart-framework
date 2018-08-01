package com.szzt.smart.framework.web.util;

import com.szzt.smart.framework.web.exception.ArgumentException;
import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 为 Date 提供常数和静态方法。
 * 
 * @author: yidi
 */
public class DateUtil
{
    private static final long TicksPerSecond = 1000;
    
    private static final long TicksPerMinute = TicksPerSecond * 60;
    
    private static final long TicksPerHour = TicksPerMinute * 60;
    
    private static final long TicksPerDay = TicksPerHour * 24;
    
    private static final long LangTicksDifference = 62135625600000L;
    
    private static final int MinYear = 1;
    
    private static final int MaxYear = 9999;
    
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * 获取当前时间的 Date。
     * 
     * @return 当前时间的 Date。
     */
    public static Date getNow()
    {
        return new Date();
    }
    
    /**
     * 将日期和时间的字符串表示形式转换为它的等效 Date。
     * 
     * @param s 包含要转换的日期和时间的字符串。
     * @param format 指定 Date 格式的字符串。
     * @return 日期的字符串表示形式。
     */
    public static Date parse(String s, String format)
    {
        if (StringUtils.isBlank(s))
        {
            return null;
        }
        DateFormat df = new SimpleDateFormat(format);
        try
        {
            return df.parse(s);
        }
        catch (ParseException e)
        {
            // throw new ArgumentException("不能使用日期格式“" + format + "”解析：" + s, "dateString");
        }
        return null;
    }
    
    /**
     * 使用指定格式将日期转换为它的字符串表示形式。
     * 
     * @param date 日期，非 null。
     * @param format 指定返回格式的字符串。
     * @return 日期的字符串表示形式。
     */
    public static String format(Date date, String format)
    {
        if (date == null)
        {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }
    
    /**
     * 默认标准时间格式 格式化
     * 
     * @param date
     * @return
     */
    public static String formatDatetime(Date date)
    {
        return format(date, DEFAULT_DATETIME_FORMAT);
    }
    
    /**
     * 默认标准日期格式 格式化
     * 
     * @param date
     * @return
     */
    public static String formatDate(Date date)
    {
        return format(date, DEFAULT_DATE_FORMAT);
    }
    
    /**
     * 返回指定的年份是否为闰年的指示。
     * 
     * @param year 四位数年份。
     * @return 如果 year 为闰年，则为 true；否则为 false。
     */
    public static boolean isLeapYear(int year)
    {
        ArgumentException.checkNotBetweenIn("year", year, MinYear, MaxYear);
        if (0 != (year % 4))
        {
            return false;
        }
        if (0 == (year % 100))
        {
            return 0 == (year % 400);
        }
        return true;
    }
    
    /**
     * 在日期上增加年数。
     * 
     * @param date 日期，非 null。
     * @param amount 要增加的数量，可以为负数。
     * @return 增加年数后的新日期。
     */
    public static Date addYears(Date date, int amount)
    {
        return add(date, Calendar.YEAR, amount);
    }
    
    /**
     * 在日期上增加月数。
     * 
     * @param date 日期，非 null。
     * @param amount 要增加的数量，可以为负数。
     * @return 增加月数后的新日期。
     */
    public static Date addMonths(Date date, int amount)
    {
        return add(date, Calendar.MONTH, amount);
    }
    
    /**
     * 在日期上增加周数。
     * 
     * @param date 日期，非 null。
     * @param amount 要增加的数量，可以为负数。
     * @return 增加周数后的新日期。
     */
    public static Date addWeeks(Date date, int amount)
    {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }
    
    /**
     * 在日期上增加日数。
     * 
     * @param date 日期，非 null。
     * @param amount 要增加的数量，可以为负数。
     * @return 增加日数后的新日期。
     */
    public static Date addDays(Date date, int amount)
    {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }
    
    /**
     * 在日期上增加小时数。
     * 
     * @param date 日期，非 null。
     * @param amount 要增加的数量，可以为负数。
     * @return 增加小时数后的新日期。
     */
    public static Date addHours(Date date, int amount)
    {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }
    
    /**
     * 在日期上增加分钟数。
     * 
     * @param date 日期，非 null。
     * @param amount 要增加的数量，可以为负数。
     * @return 增加分钟数后的新日期。
     */
    public static Date addMinutes(Date date, int amount)
    {
        return add(date, Calendar.MINUTE, amount);
    }
    
    /**
     * 在日期上增加秒数。
     * 
     * @param date 日期，非 null。
     * @param amount 要增加的数量，可以为负数。
     * @return 增加秒数后的新日期。
     */
    public static Date addSeconds(Date date, int amount)
    {
        return add(date, Calendar.SECOND, amount);
    }
    
    /**
     * 在日期上增加毫秒数。
     * 
     * @param date 日期，非 null。
     * @param amount 要增加的数量，可以为负数。
     * @return 增加毫秒数后的新日期。
     */
    public static Date addMilliseconds(Date date, int amount)
    {
        return add(date, Calendar.MILLISECOND, amount);
    }
    
    /**
     * 根据指定 DatePart 截断日期。 例如，如果你的日期是 28 Mar 2002 13:45:01.231， 如果 datePart 为 Hour，它将返回 28 Mar 2002 13:00:00.000； 如果
     * datePart 为 Month，它将返回 1 Mar 2002 0:00:00.000。
     * 
     * @param date 日期，非 null。
     * @param datePart 保留的部分。
     * @return 截断后的新日期。
     */
    public static Date truncate(Date date, DatePart datePart)
    {
        long data = date.getTime();
        switch (datePart)
        {
            case Year:
                return new Date(data - (data + LangTicksDifference) % TicksPerDay
                    - (get(date, Calendar.DAY_OF_YEAR) - 1) * TicksPerDay);
            case Month:
                return new Date(data - (data + LangTicksDifference) % TicksPerDay
                    - (get(date, Calendar.DAY_OF_MONTH) - 1) * TicksPerDay);
            case Day:
                return new Date(data - (data + LangTicksDifference) % TicksPerDay);
            case Hour:
                return new Date(data - data % TicksPerHour);
            case Minute:
                return new Date(data - data % TicksPerMinute);
            case Second:
                return new Date(data - data % TicksPerSecond);
            case Millisecond:
                return new Date(data);
            default:
                return new Date(data - data % TicksPerDay);
        }
    }
    
    /**
     * 获取日期的年份。
     * 
     * @param date 日期，非 null。
     * @return 日期的年份。
     */
    public static int getYear(Date date)
    {
        return get(date, Calendar.YEAR);
    }
    
    /**
     * 给日期设置一个新的年份。
     * 
     * @param date 日期，非 null。
     * @param amount 要设置的年份。
     * @return 设置年份后的新日期。
     */
    public static Date setYear(Date date, int amount)
    {
        return set(date, Calendar.YEAR, amount);
    }
    
    /**
     * 获取日期的月份。
     * 
     * @param date 日期，非 null。
     * @return 日期的月份。
     */
    public static int getMonth(Date date)
    {
        return get(date, Calendar.MONTH);
    }
    
    public static int getNowMonth(Date date)
    {
        return get(date, Calendar.MONTH) + 1;
    }
    
    /**
     * 给日期设置一个新的月份。
     * 
     * @param date 日期，非 null。
     * @param amount 要设置的月份。
     * @return 设置月份后的新日期。
     */
    public static Date setMonth(Date date, int amount)
    {
        return set(date, Calendar.MONTH, amount);
    }
    
    /**
     * 获取日期的月中的第几天。
     * 
     * @param date 日期，非 null。
     * @return 日期的月中的第几天。
     */
    public static int getDay(Date date)
    {
        return get(date, Calendar.DAY_OF_MONTH);
    }
    
    /**
     * 给日期设置一个新的月中的第几天。
     * 
     * @param date 日期，非 null。
     * @param amount 要设置的月中的第几天。
     * @return 设置月中的第几天后的新日期。
     */
    public static Date setDay(Date date, int amount)
    {
        return set(date, Calendar.DAY_OF_MONTH, amount);
    }
    
    /**
     * 获取日期的小时部份。
     * 
     * @param date 日期，非 null。
     * @return 日期的小时部份。
     */
    public static int getHour(Date date)
    {
        return get(date, Calendar.HOUR_OF_DAY);
    }
    
    /**
     * 给日期设置一个新的小时部份。
     * 
     * @param date 日期，非 null。
     * @param amount 要设置的小时部份。
     * @return 设置小时部份后的新日期。
     */
    public static Date setHour(final Date date, int amount)
    {
        return set(date, Calendar.HOUR_OF_DAY, amount);
    }
    
    /**
     * 获取日期的分钟部份。
     * 
     * @param date 日期，非 null。
     * @return 日期的分钟部份。
     */
    public static int getMinute(Date date)
    {
        return get(date, Calendar.MINUTE);
    }
    
    /**
     * 给日期设置一个新的分钟部份。
     * 
     * @param date 日期，非 null。
     * @param amount 要设置的分钟部份。
     * @return 设置分钟部份后的新日期。
     */
    public static Date setMinute(Date date, int amount)
    {
        return set(date, Calendar.MINUTE, amount);
    }
    
    /**
     * 获取日期的秒部份。
     * 
     * @param date 日期，非 null。
     * @return 日期的秒部份。
     */
    public static int getSecond(Date date)
    {
        return get(date, Calendar.SECOND);
    }
    
    /**
     * 给日期设置一个新的秒部份。
     * 
     * @param date 日期，非 null。
     * @param amount 要设置的秒部份。
     * @return 设置秒部份后的新日期。
     */
    public static Date setSecond(Date date, int amount)
    {
        return set(date, Calendar.SECOND, amount);
    }
    
    /**
     * 获取日期的毫秒部份。
     * 
     * @param date 日期，非 null。
     * @return 日期的毫秒部份。
     */
    public static int getMillisecond(Date date)
    {
        return get(date, Calendar.MILLISECOND);
    }
    
    /**
     * 获取二个日期的相差天数
     * 
     * @param startDate
     * @param endDate
     * @return
     */
    public static long getDifferenceDay(Date startDate, Date endDate)
    {
        long DAY = 24L * 60L * 60L * 1000L;
        return (endDate.getTime() - startDate.getTime()) / DAY;
    }
    
    /**
     * 给日期设置一个新的毫秒部份。
     * 
     * @param date 日期，非 null。
     * @param amount 要设置的毫秒部份。
     * @return 设置毫秒部份后的新日期。
     */
    public static Date setMillisecond(Date date, int amount)
    {
        return set(date, Calendar.MILLISECOND, amount);
    }
    
    private static Date add(Date date, int calendarField, int amount)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }
    
    private static int get(Date date, int calendarField)
    {
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        return c.get(calendarField);
    }
    
    private static Date set(Date date, int calendarField, int amount)
    {
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(calendarField, amount);
        return c.getTime();
    }
    
    /**
     * 获取当天日期
     * 
     * @return String
     */
    public static String getToday()
    {
        return format(getNow(), DEFAULT_DATE_FORMAT);
    }
    
    /**
     * 获取当日日期(yyyy-MM-dd)
     * 
     * @return Date
     */
    public static Date getDate()
    {
        // String today = getToday();
        // Date date = parse(today, DEFAULT_DATE_FORMAT);
        return getDate(getNow());
    }
    
    /**
     * 去掉时间部分
     * 
     * @param date
     * @return Date
     */
    public static Date getDate(Date date)
    {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.set(Calendar.HOUR_OF_DAY, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        calender.set(Calendar.MILLISECOND, 0);
        return calender.getTime();
    }
    
    public static String getDatePath()
    {
        return format(getNow(), "yyyyMMdd");
    }
    
    /**
     * 获取当前时间毫秒数
     * 
     * @return long
     */
    public static long getCurrentTime()
    {
        return getNow().getTime();
    }
    
    /**
     * 获取当前时间秒数
     * 
     * @return long
     */
    public static long getCurrentTimeSecond()
    {
        return getNow().getTime() / 1000;
    }
    
    /**
     * 根据日期获得星期
     * 
     * @param date
     * @return
     */
    public static String getWeekOfDate(Date date)
    {
        String[] weekDaysName = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        // String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }
    
    /**
     * 判断是否是周末
     * 
     * @return
     */
    public static boolean isWeekend(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (week == 6 || week == 0)
        {// 0代表周日，6代表周六
            return true;
        }
        return false;
    }
    
    /**
     * 获取两个日期间的所有日期
     * 
     * @param beginDate 开始日期 不带时间
     * @param endDate 结束日期
     * @return List<Date>
     */
    public static List<Date> getDateList(Date beginDate, Date endDate)
    {
        List<Date> list = new ArrayList<>();
        while (!beginDate.after(endDate))
        {
            list.add(beginDate);
            beginDate = addDays(beginDate, 1);
        }
        return list;
    }
    
    /**
     * 获取两个日期间的所有日期
     * 
     * @param beginDate 开始日期 不带时间
     * @param endDate 结束日期
     * @return List<String>
     */
    public static List<String> getFormatDateList(Date beginDate, Date endDate)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DEFAULT_DATE_FORMAT);
        List<String> list = new ArrayList<>();
        while (!beginDate.after(endDate))
        {
            list.add(sdf.format(beginDate));
            beginDate = addDays(beginDate, 1);
        }
        return list;
    }
    
    /**
     * 查询某年某月的最后一天
     * 
     * @param year 年份
     * @param month 月份（0-11）
     * @return int
     */
    public static int getLastDayOfMonth(int year, int month)
    {
        Calendar cal = Calendar.getInstance();
        // int i = cal.get(Calendar.MONTH);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        // 某年某月的最后一天
        return cal.getActualMaximum(Calendar.DATE);
    }
    
    public static Date getTodayStartTime()
    {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }
    
    public static Date getTodayEndTime()
    {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 000);
        return todayEnd.getTime();
    }
    
    public static Date getTodayTime(int hourOfDay, int minute, int second)
    {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
        todayStart.set(Calendar.MINUTE, minute);
        todayStart.set(Calendar.SECOND, second);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }
    
    /**
     * 获取指定日期的最早时间
     * 
     * @param date 日期
     */
    public static Date earliestTime(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        ;
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    
    /**
     * 将指定日期的时间设置为一天的最早时间
     * 
     * @param date 日期
     */
    public static Date latestTime(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        ;
        c.set(Calendar.HOUR, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 000);
        return c.getTime();
    }
}
