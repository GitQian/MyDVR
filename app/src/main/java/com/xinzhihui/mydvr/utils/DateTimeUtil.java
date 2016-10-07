package com.xinzhihui.mydvr.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.text.TextUtils;

/**
 * 时间日期工具类
 *
 * @author QianSheng
 */
public class DateTimeUtil {

    public static final String DATE_FORMAT_CHINA = "yyyy年MM月dd日";
    public static final String DATE_FORMAT_SIMPLE = "yyyy-MM-dd";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_NUMBER = "yyyyMMddHHmmss";
    public static final String TIME_FORMAT = "HH:mm:ss";

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat SDF_CHINA = new SimpleDateFormat(DATE_FORMAT_CHINA);
    private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT);
    private static final SimpleDateFormat SDF_TIME = new SimpleDateFormat(TIME_FORMAT);
    private static final SimpleDateFormat SDF_NUMBER_TIME = new SimpleDateFormat(DATE_FORMAT_NUMBER);

    /**
     * 获取系统当前日期 yyyy-MM-dd
     *
     * @return
     */
    public static String getCurrentDate() {
        String datatime = getCurrentDateTime();
        return datatime.substring(0, DATE_FORMAT_SIMPLE.length());
    }

    /**
     * 获取系统当前时间 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentDateTime() {
        Date date = new Date(System.currentTimeMillis());
        return SDF.format(date);
    }

    /**
     * 获取系统当前时间 yyyyMMddHHmmss
     *
     * @return
     */
    public static String getCurrentNumberDateTime() {
        Date date = new Date(System.currentTimeMillis());
        return SDF_NUMBER_TIME.format(date);
    }

    /**
     * 获取系统当前时间，去空格冒号，用于写日志时文件命名
     *
     * @return
     */
    public static String getCurrentDateTimeReplaceSpace() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String date1 = format1.format(new Date(System.currentTimeMillis()));
        return date1;
    }

    public static Long strToLong(String s) {// 标准的时间格式转换为long类型
        try {
            return SDF.parse(s).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0l;
    }

    /**
     * 日期字符串 yyyy-MM-dd or yyyy-MM-dd HH:mm:ss 转为Date对象
     *
     * @param date 日期字符串 yyyy-MM-dd or yyyy-MM-dd HH:mm:ss
     * @return
     * @throws ParseException
     */
    public static Date parseCurrentDate(String date) throws ParseException {
        if (TextUtils.isEmpty(date))
            return null;

        if (date.length() == DATE_FORMAT_SIMPLE.length()) {
            return SDF.parse(date + " 00:00:00");
        } else {
            return SDF.parse(date);
        }
    }

    /**
     * 秒数转换为日期 yyyy-MM-dd
     *
     * @param seconds 秒数
     * @return
     */
    public static String parseSecondsToDate(long seconds) {
        Date date = new Date(seconds * 1000L);
        return SDF.format(date).substring(0, DATE_FORMAT_SIMPLE.length());
    }

    /**
     * 秒数转换为日期 yyyy年MM月dd日
     *
     * @param seconds 秒数
     * @return
     */
    public static String parseSecondsToChineseDate(long seconds) {
        Date date = new Date(seconds * 1000L);
        return SDF_CHINA.format(date).substring(0, DATE_FORMAT_CHINA.length());
    }

    public static String parseSecondsToChineseDate1(long seconds) {
        Date date = new Date(seconds);
        return SDF_CHINA.format(date).substring(0, DATE_FORMAT_CHINA.length());
    }

    /**
     * Date转换为日期 yyyy年MM月dd日
     *
     * @param seconds 秒数
     * @return
     */
    public static String parseDateToChineseDate(Date date) {
        return SDF_CHINA.format(date).substring(0, DATE_FORMAT_CHINA.length());
    }

    /**
     * 秒数转换为日期 yyyy-MM-dd HH:mm:ss
     *
     * @param seconds 秒数
     * @return
     */
    public static String parseSecondsToDateTime(long seconds) {
        Date date = new Date(seconds * 1000L);
        return SDF.format(date);
    }

    /**
     * 获取格式化的时间，如果是当天的显示时分秒，否则显示年月日时分秒
     *
     * @param time
     * @return
     */
    public static String getStandardDate(long time) {
        String result = "";
        if (isInCurrentDay(time)) {
            Date date = new Date(time);
            result = SDF_TIME.format(date);
        } else {
            Date date = new Date(time);
            result = SDF.format(date);
        }
        return result;
    }

    /**
     * 判断某个时间是否属于今天
     *
     * @param l
     * @return
     */
    private static boolean isInCurrentDay(long l) {
        long comparedMillons = l;

        //当前年月日  
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);


        c.set(y, m, (d + 1), 0, 0, 0);
        //下一日零点  
        long currDayMillions = c.getTimeInMillis();
        c.set(y, m, d, 0, 0, 0);
        //当日零点  
        long preDayMillions = c.getTimeInMillis();

        //零点算新的一天  
        return (comparedMillons >= preDayMillions && comparedMillons < currDayMillions);
    }

    /**
     * 毫秒转时分秒
     *
     * @param time 毫秒数
     * @return 转换后的时分秒字符串
     */
    public static String longToTimeStr(long time) {
        return SDF_TIME.format(time);
    }

    /**
     * 毫秒转时分秒 适合于总时长的转换
     *
     * @param l
     * @return
     */
    public static String formatLongToTimeStr(long l) {
        String hours = "";
        String minutes = "";
        String seconds = "";
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = ((int) l) / 1000;
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        if (hour < 10) {
            hours = "0" + hour;
        } else {
            hours = "" + hour;
        }
        if (minute < 10) {
            minutes = "0" + minute;
        } else {
            minutes = "" + minute;
        }
        if (second < 10) {
            seconds = "0" + second;
        } else {
            seconds = "" + second;
        }
        String strtime = hours + ":" + minutes + ":" + seconds;
        return strtime;
    }
}
