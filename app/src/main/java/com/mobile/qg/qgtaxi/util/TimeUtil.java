package com.mobile.qg.qgtaxi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 11234 on 2018/8/17.
 */
public class TimeUtil {

    private final static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private final static String START = "2017-02-07 16:00:00";
    private final static String NOW = "2018-8-17 16:00:00";

    public static String getTime() {
        long start = 0;
        try {
            start = FORMATTER.parse("2017-02-07 16:00:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long now = new Date().getTime();
        long offset = now - start;

        return FORMATTER.format(new Date(start + offset));
    }

    public static String getDeadTime() {
        return START;
    }

}
