package com.mobile.qg.qgtaxi.time;

import android.content.Context;
import android.widget.Scroller;


/**
 * Created by 11234 on 2018/8/14.
 * 自定义Scroller类
 * 方便回调滑动的状态
 */
public class CalendarScroller extends Scroller {

    public interface OnScrollListener {
        void onFinish(CalendarView.Direction direction);

        void onScroll(int currentX);

    }

    public void setOnScrollListener(OnScrollListener listener) {
        mScrollListener = listener;
    }

    private OnScrollListener mScrollListener;

    private boolean isScrolling;

    CalendarScroller(Context context) {
        super(context);
    }

    @Override
    public boolean computeScrollOffset() {
        boolean status = super.computeScrollOffset();
        if (mScrollListener == null) {
            return status;
        }
        if (isScrolling && !status) {
            isScrolling = false;
            CalendarView.Direction direction = getFinalX() > 0 ? CalendarView.Direction.RIGHT : CalendarView.Direction.LEFT;
            if (getFinalX() == 0) {
                direction = CalendarView.Direction.CENTER;
            }
            mScrollListener.onFinish(direction);
        } else if (status) {
            mScrollListener.onScroll(getCurrX());
        }
        return status;

    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        isScrolling = true;
        super.startScroll(startX, startY, dx, dy, duration);
    }

}
