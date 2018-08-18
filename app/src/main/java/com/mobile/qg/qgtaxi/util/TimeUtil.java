package com.mobile.qg.qgtaxi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 11234 on 2018/8/17.
 */
public class TimeUtil {

    private final static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private final static long ORIGIN = 1486454400000L; //2017-02-07 16:00:00
    private final static String START = "2018-8-17 16:00:00";

    public static String getTime() {

        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

//        long start = 0;
//        try {
//            start = FORMATTER.parse(START).getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        long now = new Date().getTime();
//        long offset = now - start;

        return FORMATTER.format(calendar.getTime());
    }

    public static String getPredictedTime() {
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 7);
        return FORMATTER.format(calendar.getTime());
    }

    public static long getLong(String time) {
        try {
            return FORMATTER.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ORIGIN;
    }


}
