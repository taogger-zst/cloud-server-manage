package com.taogger.common.utils;

import cn.hutool.core.date.LocalDateTimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类-localDateTime
 * @author taogger
 * @date 2022/7/8 11:34
 */
public class LocalDateTimeUtils extends LocalDateTimeUtil {

    /**
     * 格式 yyyy年MM月dd日 HH:mm:ss
     * @param dateTime
     * @return
     */
    public static String getCnYMDHms(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        String strDate2 = dtf2.format(dateTime);

        return strDate2;
    }

    /**
     * 格式 yyyy-MM-dd HH:mm
     * @param dateTime
     * @return
     */
    public static String getYMDHm(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String strDate2 = dtf2.format(dateTime);

        return strDate2;
    }

    /**
     * 格式 yyyy.MM.dd
     * @param dateTime 日期时间
     * @return 格式 yyyy.MM.dd
     */
    public static String getLinkYMD(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return dtf2.format(dateTime);
    }

    /**
     * 格式 yyyy-MM-dd
     * @param dateTime
     * @return
     */
    public static String getYMD(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dtf2.format(dateTime);
    }

    /**
     * 格式汉化 yyyy年MM月dd日
     * @author chennz
     * @date 2022/2/8 10:23
     * @param dateTime 本地时间对象
     **/
    public static String getYMDCn(LocalDateTime dateTime) {
        if (dateTime == null){
            return null;
        }
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        return dtf2.format(dateTime);
    }

    public static String getYMDHms(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dtf2.format(dateTime);
    }

    public static Date parse2YMDHms(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(date);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static String getPrevMonthEndDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            // 设置为指定日期
            c.setTime(date);
            // 指定日期月份减去一
            c.add(Calendar.MONTH, -1);
            // 指定日期月份减去一后的 最大天数
            c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
            // 获取最终的时间
            Date lastDateOfPrevMonth = c.getTime();
            return sdf.format(lastDateOfPrevMonth);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取指定天数前的时间
     * @param day
     * @return 获取指定天数前的时间
     */
    public static String getSetTimeByDay(Integer day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            // 设置为指定日期
            c.setTime(date);
            // 指定日期月份减去一
            c.add(Calendar.DATE, -day);
            // 获取最终的时间
            Date lastDateOfPrevMonth = c.getTime();
            return sdf.format(lastDateOfPrevMonth);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 时间转换
     * @return 转换后的时间
     * @throws ParseException 异常
     * @desc (type = 1)时间展示规律如下：
     * 如果是今天发布的动态则展示
     * 多少分钟前（60分钟以内）例：20分钟前
     * 多少小时前（24小时以内）例：4小时前
     * 昨天（超过24小时小于48小时）例：昨天 12:00
     * 超过48小时展示并且是今年的 例:10-15
     * 不是今年的展示 例：2019-10-15
     */
    public static String appFormatTime(LocalDateTime dateTime) {
        String oldTime = getYMDHms(dateTime);
        long NTime = System.currentTimeMillis();
        long OTime = dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        ;
        long diff = (NTime - OTime);
        StringBuffer dateString = new StringBuffer();
        if (diff < 10 * 1000) {
            dateString.append("刚刚");
        } else if (diff < 60 * 1000) {
            dateString.append(diff / 1000).append("秒前");
        } else if (diff < 60 * 60 * 1000) {
            dateString.append(diff / 1000 / 60).append("分钟前");
        } else if (diff < 60 * 60 * 48 * 1000) {
            if (IsToday(OTime)) {
                dateString.append(diff / 1000 / 60 / 60).append("小时前");
            } else if (IsYesterday(OTime)) {
                if (oldTime.substring(11, 16).startsWith("0")) {
                    dateString.append("昨天").append(oldTime.substring(12, 16));
                } else {
                    dateString.append("昨天").append(oldTime.substring(11, 16));
                }
            } else {
                if (oldTime.substring(5, 10).startsWith("0")) {
                    dateString.append(oldTime.substring(6, 10));
                } else {
                    dateString.append(oldTime.substring(5, 10));
                }
            }
        } else {
            dateString.append(getLinkYMD(dateTime));
        }
        return dateString.toString();


    }

    /**
     * 判断是否为今天(效率比较高)
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsToday(long time) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        Date date = new Date(time);
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);
            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean IsYesterday(long time) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        Date date = new Date(time);
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }
}
