package com.zhanggb.fictionread.util;

import android.content.ContentResolver;
import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-23
 * Time: 上午10:13
 * To change this template use File | Settings | File Templates.
 */
public class DateUtils {

    public static String format(Date date) {
        Calendar now = new GregorianCalendar();
        Calendar input = new GregorianCalendar();
        input.setTime(date);
        String pattern = null;
        if (sameYear(now, input) && sameMonth(now, input) && sameDay(now, input)) {
            pattern = "HH:mm";
        } else if (sameYear(now, input)) {
            pattern = "MM/dd";
        } else {
            pattern = "yyyy/MM/dd";
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String format(Context context, Date date) {
        Calendar now = new GregorianCalendar();
        Calendar input = new GregorianCalendar();
        input.setTime(date);
        String pattern = null;
        if (sameYear(now, input) && sameMonth(now, input) && sameDay(now, input)) {
            ContentResolver cv = context.getContentResolver();
            String strTimeFormat = android.provider.Settings.System.getString(cv, android.provider.Settings.System.TIME_12_24);
            pattern = "HH:mm";
            if (strTimeFormat != null) {
                if (strTimeFormat.equals("24")) {
                    pattern = "HH:mm";
                } else {
                    pattern = " a h:mm";
                }
            }
        } else if (sameYear(now, input)) {
            pattern = "MM/dd";
        } else {
            pattern = "yyyy/MM/dd";
        }
        return new SimpleDateFormat(pattern).format(date);
    }


    private static long interval(Calendar left, Calendar right) {
        long leftDate = left.getTime().getTime();
        long rightDate = right.getTime().getTime();
        return leftDate - rightDate;
    }

    private static boolean sameYear(Calendar left, Calendar right) {
        return left.get(Calendar.YEAR) == right.get(Calendar.YEAR);
    }

    private static boolean sameMonth(Calendar left, Calendar right) {
        return left.get(Calendar.MONTH) == right.get(Calendar.MONTH);
    }

    private static boolean sameDay(Calendar left, Calendar right) {
        return left.get(Calendar.DATE) == right.get(Calendar.DATE);
    }

    public static CharSequence longFormat(Date input) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(input);
    }

    public static String middleFormat(Date date) {
        Calendar now = new GregorianCalendar();
        Calendar input = new GregorianCalendar();
        input.setTime(date);
        String pattern = null;
        if (sameYear(now, input) && sameMonth(now, input) && sameDay(now, input)) {
            pattern = "HH:mm";
        } else if (sameYear(now, input)) {
            pattern = "MM/dd HH:mm";
        } else {
            pattern = "yyyy/MM/dd HH:mm";
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * left 是否在 right 之后
     *
     * @param left  left
     * @param right right
     * @return before?
     */
    public static boolean after(Date left, Date right) {
        return left != null && (right == null || left.after(right));
    }

    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s = encode;
                return s;
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }

    public static String isoToUtf8(String body) {
        if (getEncoding(body).equals("ISO-8859-1")) {
            try {
                body = new String(body.getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (getEncoding(body).equals("GB2312")) {
            try {
                body = new String(body.getBytes("GB2312"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return body;
    }

}
