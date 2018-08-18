package com.mobile.qg.qgtaxi.time;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.qg.qgtaxi.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 11234 on 2018/8/13.
 */
public class TimePicker extends RelativeLayout implements
        CalendarView.OnPickListener {

    private final static String[] MONTH_EN = {"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public interface OnTimePackListener {
        void onCancel();

        void onCommit(String time);
    }

    private OnTimePackListener mTimePackListener;

    public void setTimePackListener(OnTimePackListener listener) {
        mTimePackListener = listener;
    }

    private TextView mMonthTv;
    private CalendarView mCalendarView;
    private RecyclerView mHourRv;
    private RecyclerView mMinuteRv;

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_picker, this);
        init();
    }

    public TimePicker(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.layout_picker, this);
        init();
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_picker, this);
        init();
    }

    private void init(){
        mMonthTv = findViewById(R.id.tv_picker_month);
        mCalendarView = findViewById(R.id.cv_picker);
        mHourRv = findViewById(R.id.rv_picker_hour);
        mMinuteRv = findViewById(R.id.rv_picker_minute);

        mMonthTv.setText(MONTH_EN[Calendar.getInstance().get(Calendar.MONTH)]);

        mCalendarView.setOnPickListener(this);

        findViewById(R.id.iv_picker_last).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.lastMonth();
            }
        });

        findViewById(R.id.iv_picker_next).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.nextMonth();
            }
        });


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

        findViewById(R.id.iv_picker_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimePackListener != null) {
                    mTimePackListener.onCancel();
                }
            }
        });

        findViewById(R.id.btn_confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimePackListener != null) {
                    mTimePackListener.onCommit(getTime());
                }
            }
        });
    }

    private String getTime() {
        String a = mCalendarView.getMark();
        String hour = ((WheelAdapter) mHourRv.getAdapter()).getText(((LinearLayoutManager) mHourRv.getLayoutManager()).findFirstVisibleItemPosition());
        String minute = ((WheelAdapter) mMinuteRv.getAdapter()).getText(((LinearLayoutManager) mMinuteRv.getLayoutManager()).findFirstVisibleItemPosition());
        String b = hour + ":" + minute + ":00";
        return a + " " + b;
    }

    private void setRecyclerView(RecyclerView recyclerView, List<String> strings) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearSnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(recyclerView);
        WheelAdapter adapter = new WheelAdapter(strings);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPick(Dater dater) {
        mMonthTv.setText(MONTH_EN[dater.getMonth() - 1]);
    }

}
