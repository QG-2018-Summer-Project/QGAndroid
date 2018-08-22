package com.mobile.qg.qgtaxi.time;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by 11234 on 2018/8/13.
 */
public class Dater {

    private int year;
    private int month; //1-12
    private int day;

    Dater(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public String toString() {
        return "Dater{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                '}';
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public static Dater getInstance() {
        Calendar calendar = Calendar.getInstance();
        return new Dater(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static Dater getDemo() {
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.set(2017,1,1);
        return new Dater(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Dater) {
            Dater dater = (Dater) obj;
            return dater.getYear() == year && dater.getMonth() == month && dater.getDay() == day;
        }
        return false;
    }

    public static ArrayList<Dater> calendarToDaters(Calendar c) {

        Calendar calendar = (Calendar) c.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int weekOfMonth = calendar.get(Calendar.DAY_OF_WEEK);

        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - weekOfMonth + 2);

        ArrayList<Dater> list = new ArrayList<>();

        int maxRow = (weekOfMonth == 7) ? 42 : 35;

        for (int i = 0; i < maxRow; i++) {
            Dater dater = new Dater(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            list.add(dater);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

}
