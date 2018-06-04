package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;


public final class DateUtil {

    private DateUtil() {

    }

    /*
     * yyyy-MM-dd
     */
    public static final String FORMAT1 = "yyyy-MM-dd";

    /*
     * yyyy.MM.dd
     */
    public static final String FORMAT2 = "yyyy.MM.dd";

    /*
     * yyyy/MM/dd
     */
    public static final String FORMAT3 = "yyyy/MM/dd";

    /*
     * yyyy-MM-dd HH:mm
     */
    public static final String FORMAT4 = "yyyy-MM-dd HH:mm";

    /*
     * yyyy.MM.dd HH:mm
     */
    public static final String FORMAT5 = "yyyy.MM.dd HH:mm";

    /*
     * yyyy/MM/dd HH:mm
     */
    public static final String FORMAT6 = "yyyy/MM/dd HH:mm";

    /*
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String FORMAT7 = "yyyy-MM-dd HH:mm:ss";

    /*
     * yyyy.MM.dd HH:mm:ss
     */
    public static final String FORMAT8 = "yyyy.MM.dd HH:mm:ss";

    /*
     * yyyy/MM/dd HH:mm:ss
     */
    public static final String FORMAT9 = "yyyy/MM/dd HH:mm:ss";

    /*
     * yyyy_MM_dd_HH_mm_ss
     */
    public static final String FORMAT10 = "yyyy_MM_dd_HH_mm_ss";

    /*
     * yy-MM-dd
     */
    public static final String FORMAT11 = "yy-MM-dd";

    /*
     * yyyyMMdd
     */
    public static final String FORMAT12 = "yyyyMMdd";

    /*
     * yyyyMMddHHmmss
     */
    public static final String FORMAT13 = "yyyyMMddHHmmss";

    /*
     * yyyyMM
     */
    public static final String FORMAT14 = "yyyyMM";

    /*
     * YYYY-MM-dd HH-mm-ss
     */
    public static final String FORMAT15 = "YYYY-MM-dd HH-mm-ss";

    /*
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ
     */
    public static final String FORMAT16 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /*
     * 返回当前日期或时间
     *
     * @param format
     * @return
     */
    public static String getCurrentDate(String format) {

        if (StringUtils.isBlank(format)) {
            format = FORMAT1;
        }

        Date date = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat(format);

        String currentTime = formatter.format(date);

        return currentTime;
    }

    /*
     * 将字符串转换为日期
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date parseStringToDate(String str, String format) {

        DateFormat formatter = null;
        Date date = null;
        if (StringUtils.isNotBlank(str)) {

            if (StringUtils.isBlank(format)) {
                formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            } else {
                formatter = new SimpleDateFormat(format);
            }


            try {
                date = formatter.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        return date;
    }

    /*
     * 日期转换为字符串
     *
     * @param date 日期
     * @param format 格式
     * @return 返回字符型日期
     */
    public static String parseDateToString(Date date, String format) {

        String result = "";
        DateFormat formatter = null;
        try {
            if (date != null) {
                if (StringUtils.isBlank(format)) {
                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                } else {
                    formatter = new SimpleDateFormat(format);
                }
                result = formatter.format(date);
            }
        } catch (Exception e) {
        }

        return result;
    }

    /*
     * 返回日期中的年份
     *
     * @param date
     *             日期
     * @return 返回年份
     */
    public static int getYear(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.YEAR);
    }

    /*
     * 返回日期中的月份
     *
     * @param date
     *             日期
     * @return 返回月份
     */
    public static int getMonth(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.MONTH) + 1;
    }

    /*
     * 返回日期中的日
     *
     * @param date
     *             日期
     * @return 返回日
     */
    public static int getDay(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.DAY_OF_MONTH);
    }

    /*
     * 返回日期中的小时
     *
     * @param date
     *             日期
     * @return 返回小时
     */
    public static int getHour(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.HOUR_OF_DAY);
    }

    /*
     * 返回日期中的分钟
     *
     * @param date
     *             日期
     * @return 返回分钟
     */
    public static int getMinute(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.MINUTE);
    }

    /**
     * 返回日期代表的毫秒
     *
     * @param date 日期
     * @return 返回毫秒
     */
    public static long getMillis(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.getTimeInMillis();
    }

    public static void main(String[] args) {
        System.out.println(getCurrentDate(FORMAT16));
    }
}