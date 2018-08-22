package com.mobile.qg.qgtaxi.time;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.qg.qgtaxi.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 11234 on 2018/8/13.
 */
public class TimePicker extends RelativeLayout implements
        CalendarView.OnPickListener {

    private final static String[] MONTH_EN = {"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private final static String[] MONTH_CN = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
    private final static String[] AM_PM = {"AM", "PM"};

    public interface OnTimePackListener {
        void onCancel();

        void onCommit(String time);
    }

    private OnTimePackListener mTimePackListener;

    public void setTimePackListener(OnTimePackListener listener) {
        mTimePackListener = listener;
    }

    @BindView(R.id.tv_picker_month)
    protected TextView mMonthTv;

    @BindView(R.id.cv_picker)
    protected CalendarView mCalendarView;

    @BindView(R.id.rv_picker_hour)
    protected RecyclerView mHourRv;

    @BindView(R.id.rv_picker_minute)
    protected RecyclerView mMinuteRv;

    @BindView(R.id.tv_picker_am)
    protected TextView mAmPmTv;

    @BindView(R.id.tv_picker_time)
    protected TextView mTimeTv;

    @OnClick(R.id.iv_picker_last)
    protected void last() {
        mCalendarView.lastMonth();
    }

    @OnClick(R.id.iv_picker_next)
    protected void next() {
        mCalendarView.nextMonth();
    }

    @OnClick(R.id.iv_picker_close)
    protected void close() {
        if (mTimePackListener != null) {
            mTimePackListener.onCancel();
        }
    }

    @OnClick(R.id.btn_confirm)
    protected void confirm() {
        if (mTimePackListener != null) {
            mTimePackListener.onCommit(getTime());
        }
    }

    @OnClick(R.id.tv_picker_am)
    protected void transfer() {
        mAmPmTv.setText(mAmPmTv.getText().toString().equals(AM_PM[0]) ? AM_PM[1] : AM_PM[0]);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_picker, this);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

        mMonthTv.setText(MONTH_CN[1]);

        mCalendarView.setOnPickListener(this);

        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            hours.add(i < 10 ? "0" + String.valueOf(i) : String.valueOf(i));
        }
        setRecyclerView(mHourRv, hours);

        List<String> minutes = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            minutes.add(i < 10 ? "0" + String.valueOf(i) : String.valueOf(i));
        }
        setRecyclerView(mMinuteRv, minutes);

        mTimeTv.setText(mCalendarView.getMark());

    }

    private String getTime() {
        String a = mCalendarView.getMark();
        String hour = ((WheelAdapter) mHourRv.getAdapter()).getText(((LinearLayoutManager) mHourRv.getLayoutManager()).findFirstVisibleItemPosition());
        if (mAmPmTv.getText().toString().equals(AM_PM[1])) {
            hour = String.valueOf(Integer.parseInt(hour) + 12);
        }
        String minute = ((WheelAdapter) mMinuteRv.getAdapter()).getText(((LinearLayoutManager) mMinuteRv.getLayoutManager()).findFirstVisibleItemPosition());
        String b = hour + ":" + minute + ":00";
        return a + " " + b;
    }

    private void setRecyclerView(RecyclerView recyclerView, List<String> strings) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearSnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(new WheelAdapter(strings));
    }

    @Override
    public void onPick(Dater dater) {
        mMonthTv.setText(MONTH_CN[dater.getMonth() - 1]);
        mTimeTv.setText(mCalendarView.getMark());
    }

}
