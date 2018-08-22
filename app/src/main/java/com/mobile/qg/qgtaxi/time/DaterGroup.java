package com.mobile.qg.qgtaxi.time;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by 11234 on 2018/8/14.
 */
public class DaterGroup {

    private int date;
    private int year;
    private int month;
    private ArrayList<Dater> now;
    private ArrayList<Dater> last;
    private ArrayList<Dater> next;

    public DaterGroup getInstance() {
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        setAll(calendar);
        return this;
    }

    public DaterGroup getDemo() {
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.set(2017, 1, 1);
        setAll(calendar);
        return this;
    }

    public void setAll(Calendar c) {
        Calendar calendar = (Calendar) c.clone();
        date = c.get(Calendar.DAY_OF_MONTH);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);// -1
        now = Dater.calendarToDaters(calendar);
        calendar.add(Calendar.MONTH, -1);
        last = Dater.calendarToDaters(calendar);
        calendar.add(Calendar.MONTH, 2);
        next = Dater.calendarToDaters(calendar);
    }

    public void skip(int offset) {
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.set(year, month, date);
        calendar.add(Calendar.MONTH, offset);
        setAll(calendar);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public ArrayList<Dater> getNow() {
        return now;
    }

    public void setNow(ArrayList<Dater> now) {
        this.now = now;
    }

    public ArrayList<Dater> getLast() {
        return last;
    }

    public void setLast(ArrayList<Dater> last) {
        this.last = last;
    }

    public ArrayList<Dater> getNext() {
        return next;
    }

    public void setNext(ArrayList<Dater> next) {
        this.next = next;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
