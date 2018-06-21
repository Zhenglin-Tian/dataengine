package com.tcredit.engine.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Berg.T on 2016/11/11.
 */
public class DateUtil {


    public static final String DATE_FORMAT_yMdHmsSSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_FORMAT_yMdHms = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 按指定格式字符串转化时间字符串为时间格式
     *
     * @param dateString
     * @param formatExpression
     * @return
     */
    public static Date parseString2Date(String dateString, String formatExpression) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatExpression);
        Date formatDate = null;
        try {
            formatDate = sdf.parse(dateString);
        } catch (Exception e) {
            throw new RuntimeException("格式化时间出错" + e);
        }
        return formatDate;
    }

    /**
     * 按指定格式化字符串转化时间毫秒数为时间字符串
     *
     * @param millisTime
     * @param formatExpression
     * @return
     */
    public static String formatDate2StrFromDate(long millisTime, String formatExpression) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatExpression);
        String format = null;
        try {
            format = sdf.format(new Date(millisTime));
        } catch (Exception e) {
            throw new RuntimeException("格式化时间出错" + e);
        }
        return format;
    }

    /**
     * 按指定格式化字符串转换时间格式为时间字符串
     *
     * @param date
     * @param formatExpression
     * @return
     */
    public static String formatDate2StrFromDate(Date date, String formatExpression) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatExpression);
        String format = null;
        try {
            format = sdf.format(date);
        } catch (Exception e) {
            throw new RuntimeException("格式化时间出错" + e);
        }
        return format;
    }

    /**
     * 指定时间-指定时间间隔=时间，按指定的格式字符串转化该时间为时间字符串
     *
     * @param date
     * @param formatExpression
     * @param interval         时间间隔
     * @return
     */
    public static String formatDate2StrFromDate(Date date, String formatExpression, String interval) {
        int length = interval.length();
        String um = interval.substring(length - 1, length);
        int i = checkTimeDimension(um);
        switch (i) {
            case 0:
                return getTimeString(date, formatExpression, interval);
            case 1:
                return formatDate2StrFromDate(new Date(date.getTime() - Long.parseLong(interval)), formatExpression);
            default:
                throw new RuntimeException("格式化时间出错");
        }
    }

    /**
     * 按指定格式字符串格式化当前时间
     *
     * @param formatExpression
     * @return
     */
    public static String formatNowDate2Str(String formatExpression) {
        return formatDate2StrFromDate(new Date(), formatExpression);
    }

    /**
     * 帮助类，指定时间-指定时间间隔=时间，按指定的格式字符串转化该时间为时间字符串
     *
     * @param date
     * @param formatExpression
     * @param interval
     * @return
     */
    private static String getTimeString(Date date, String formatExpression, String interval) {
        int length = interval.length();
        String um = interval.substring(length - 1, length);
        String substring = interval.substring(0, length - 1);
        int inter = Integer.parseInt(substring);
        Calendar calendar = Calendar.getInstance();  //当前时间
        calendar.setTime(date);//设置时间
        if (null == um | StringUtils.isBlank(um)) {
            calendar.add(Calendar.MILLISECOND, inter);
            return formatDate2StrFromDate(calendar.getTime(), formatExpression);
        } else if ("y".equals(um)) {//年
            calendar.add(Calendar.YEAR, inter);
            return formatDate2StrFromDate(calendar.getTime(), formatExpression);
        } else if ("M".equals(um)) { //yue
            calendar.add(Calendar.MONTH, inter);
            return formatDate2StrFromDate(calendar.getTime(), formatExpression);
        } else if ("w".equals(um) | "W".equals(um)) {//周
            calendar.add(Calendar.WEEK_OF_YEAR, inter);
            return formatDate2StrFromDate(calendar.getTime(), formatExpression);
        } else if ("d".equals(um) | "D".equals(um)) {//日
            calendar.add(Calendar.DAY_OF_MONTH, inter);
            return formatDate2StrFromDate(calendar.getTime(), formatExpression);
        } else if ("h".equals(um) | "H".equals(um)) {//时
            calendar.add(Calendar.HOUR_OF_DAY, inter);
            return formatDate2StrFromDate(calendar.getTime(), formatExpression);
        } else if ("m".equals(um)) {//分
            calendar.add(Calendar.MINUTE, inter);
            return formatDate2StrFromDate(calendar.getTime(), formatExpression);
        } else if ("s".equals(um)) {//秒
            calendar.add(Calendar.SECOND, inter);
            return formatDate2StrFromDate(calendar.getTime(), formatExpression);
        } else if ("S".equals(um)) {//毫秒
            calendar.add(Calendar.MILLISECOND, inter);
            return formatDate2StrFromDate(calendar.getTime(), formatExpression);
        } else {
            throw new RuntimeException("格式化时间出错");
        }

    }


    /**
     * 帮助方法，检查指定时间间隔的时间度量，1h,1m,1s
     *
     * @param dimension
     * @return
     */
    private static int checkTimeDimension(String dimension) {
        if ("y".equals(dimension) |
                "M".equals(dimension) |
                "w".equals(dimension) |
                "W".equals(dimension) |
                "d".equals(dimension) |
                "D".equals(dimension) |
                "h".equals(dimension) |
                "H".equals(dimension) |
                "m".equals(dimension) |
                "s".equals(dimension) |
                "S".equals(dimension)) {
            return 0;
        } else {
            try {
                Integer.parseInt(dimension);
                return 1;
            } catch (NumberFormatException e) {
                throw new RuntimeException("不能识别时间度量单位，y：年，M：月，w|W:周，d|D:日，h|H:时，m:分，s:秒，S|或者不写：毫秒");
            }

        }
    }


    /*public static String nowDate() {

        String nowDate = formatNowDate2Str(DATE_FORMAT);
        // String nowDate = "2018-02-06";
        int day = Integer.parseInt(nowDate.substring(8, 10));
        int month = Integer.parseInt(nowDate.substring(5, 7));
        int year = Integer.parseInt(nowDate.substring(0, 4));
        if (day <= 5) {
            int reMonth = repairZero(month - 2);
            int resMonth = (month - 2);
            return repairZero(year, resMonth) + "-" + repairZero(String.valueOf(reMonth)) + "-" + repairZero(String.valueOf(day));
        } else {
            int reMonth = repairZero(month - 1);
            int resMonth = repairZero(month - 1);
            return repairZero(year, resMonth) + "-" + repairZero(String.valueOf(reMonth)) + "-" + repairZero(String.valueOf(day));
        }
    }

    private static String repairZero(String str) {
        if (str.length() != 2) {
            return "0" + str;
        }
        return str;
    }

    private static int repairZero(int i) {
        if (i < 0) {
            return 12 + i;
        } else if (i == 0) {
            return 12;
        }
        return i;
    }
    private static int repairZero(int year, int month) {
        if (month <= 0) {
            return year - 1;
        }
        return year;
    }
    */

    /**
     * 多头借贷回溯查询（如果当前日小于5或等于5号则前推2个月，否则前推1个月）
     *
     * @return
     */
    public static String nowDate() {
        DateTime dateTime = new DateTime();
        int day = Integer.valueOf(dateTime.toString("dd"));
        if (day <= 5) {
            return dateTime.minusMonths(2).toString(DATE_FORMAT);
        } else {
            return dateTime.minusMonths(1).toString(DATE_FORMAT);
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        //System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        //String s = formatNowDate2Str("yyyy-MM-dd HH:mm:ss.SSS");
        //System.out.println(s);

        System.out.println(nowDate());

    }


}
