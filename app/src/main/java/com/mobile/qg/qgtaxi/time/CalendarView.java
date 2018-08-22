package com.mobile.qg.qgtaxi.time;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by 11234 on 2018/8/13.
 * 自定义日历控件
 */
public class CalendarView extends View implements CalendarScroller.OnScrollListener {

    /**
     * 接口 OnPickListener
     * 点击/翻页时调用
     * 返回Dater
     */
    public interface OnPickListener {
        void onPick(Dater dater);
    }

    private OnPickListener mOnPickListener;

    public void setOnPickListener(OnPickListener listener) {
        mOnPickListener = listener;
    }

    public void nextMonth() {
        scrollToDirection(Direction.LEFT);
    }

    public void lastMonth() {
        scrollToDirection(Direction.RIGHT);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private Paint mPaint;
    private CalendarScroller mXScroller;
    private float mStartX;
    private float mStartY;
    private float mTextHeight;
    private DaterGroup mDaterGroup = new DaterGroup().getDemo();
    private Dater mMarker = Dater.getDemo();

    private final static int BLACK = Color.parseColor("#CC3C3C3C");
    private final static int GRAY = Color.parseColor("#CC999999");
    private final static int BLUE = Color.parseColor("#5555FF");
    private final static int WHILE = Color.parseColor("#FFFFFF");

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(40);
        mPaint.setTypeface(Typeface.DEFAULT);

        mXScroller = new CalendarScroller(getContext());
        mXScroller.setOnScrollListener(this);

    }

    public String getMark() {
        String year = String.valueOf(mMarker.getYear());
        String month = String.valueOf(mMarker.getMonth() < 10 ? "0" + mMarker.getMonth() : mMarker.getMonth());
        String date = String.valueOf(mMarker.getDay() < 10 ? "0" + mMarker.getDay() : mMarker.getDay());
        return year + "-" + month + "-" + date;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mStartX == 0 || mStartY == 0 || mTextHeight == 0) {
            mStartX = getWidth() / 14;
            mStartY = getHeight() / 6;
            mTextHeight = mPaint.descent() - mPaint.ascent();
        }

        draw(canvas, mDaterGroup.getNow(), 0);
        draw(canvas, mDaterGroup.getLast(), -getWidth());
        draw(canvas, mDaterGroup.getNext(), getWidth());
    }


    private void draw(Canvas canvas, ArrayList<Dater> list, int start) {
        int lines = list.size() > 35 ? 6 : 5;
        String a;
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < 7; j++) {
                Dater dater = list.get((i * 7 + j));
                a = String.valueOf(dater.getDay());
                if (i == 0 || i == lines - 1) {
                    if (mDaterGroup.getMonth() + 1 != dater.getMonth()) {
                        mPaint.setColor(GRAY);
                    } else {
                        mPaint.setColor(BLACK);
                    }
                }
                if (dater.equals(mMarker)) {
                    mPaint.setColor(BLUE);
                    canvas.drawCircle(start + mStartX + j * 2 * mStartX, mStartY * i + mTextHeight * 2 / 3, 45, mPaint);
                    mPaint.setColor(WHILE);
                    canvas.drawText(a, start + mStartX + j * 2 * mStartX - mPaint.measureText(a) / 2, mStartY * i + mTextHeight, mPaint);
                    mPaint.setColor(BLACK);
                } else {
                    canvas.drawText(a, start + mStartX + j * 2 * mStartX - mPaint.measureText(a) / 2, mStartY * i + mTextHeight, mPaint);
                }
            }
        }
    }

    boolean isScrolling;
    private double PosX = 0;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                PosX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                double nowX = event.getX();

                if (isScrolling) {
                    mXScroller.abortAnimation();
                }

                scrollBy((int) (PosX - nowX), 0);
                PosX = nowX;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                PosX = 0;

                if (getScrollX() > getWidth() / 4) {
                    scrollToDirection(Direction.LEFT);
                } else if (-getScrollX() > getWidth() / 4) {
                    scrollToDirection(Direction.RIGHT);
                } else {
                    scrollToDirection(Direction.CENTER);

                    if (getScrollX() < 10 && getScrollX() > -10) {
                        callbackClick(event.getX(), event.getY());
                    }

                }
                break;
        }
        return true;
    }


    enum Direction {
        LEFT, RIGHT, CENTER
    }

    private void scrollToDirection(Direction direction) {
        isScrolling = true;
        switch (direction) {
            case LEFT:
                mXScroller.startScroll(getScrollX(), 0, getWidth() - getScrollX(), 0, 500);
                invalidate();
                break;
            case RIGHT:
                mXScroller.startScroll(getScrollX(), 0, -getWidth() - getScrollX(), 0, 500);
                invalidate();
                break;
            case CENTER:
                mXScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 500);
                invalidate();
                break;
        }

    }

    private void callbackClick(float X, float Y) {

        int index = (int) (6 * Y / getHeight()) * 7 + (int) (7 * X / getWidth());

        ArrayList<Dater> now = mDaterGroup.getNow();
        if (index < now.size()) {
            Dater dater = now.get(index);

            if (mOnPickListener != null) {
                mOnPickListener.onPick(dater);
            }

            mMarker = dater;
            mDaterGroup.setDate(dater.getDay());

            int clickMonth = dater.getMonth();
            int showMonth = mDaterGroup.getMonth() + 1;
            int offset = clickMonth - showMonth;
            if (offset > 0) {
                scrollToDirection(Direction.LEFT);
            } else if (offset < 0) {
                scrollToDirection(Direction.RIGHT);
            } else {
                invalidate();
            }


        }

    }

    @Override
    public void computeScroll() {
        mXScroller.computeScrollOffset();
        super.computeScroll();
    }

    @Override
    public void onFinish(Direction direction) {
        isScrolling = false;
        switch (direction) {
            case LEFT:
                mDaterGroup.skip(-1);
                invalidate();
                scrollTo(0, 0);
                break;
            case RIGHT:
                mDaterGroup.skip(1);
                invalidate();
                scrollTo(0, 0);
                break;
            case CENTER:
                break;
        }
        if (mOnPickListener != null) {
            mOnPickListener.onPick(new Dater(mDaterGroup.getYear(), mDaterGroup.getMonth() + 1, mDaterGroup.getDate()));
        }

    }

    @Override
    public void onScroll(int currentX) {
        isScrolling = true;
        scrollTo(currentX, 0);
        postInvalidate();
    }

}
