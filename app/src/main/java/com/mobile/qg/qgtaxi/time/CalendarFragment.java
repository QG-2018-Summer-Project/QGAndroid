package com.mobile.qg.qgtaxi.time;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.qg.qgtaxi.R;

/**
 * Created by 11234 on 2018/8/18.
 */
public class CalendarFragment extends DialogFragment {

    private TimePicker.OnTimePackListener listener;

    public CalendarFragment listener(TimePicker.OnTimePackListener listener) {
        this.listener = listener;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        TimePicker timePicker = view.findViewById(R.id.time_picker);
        timePicker.setTimePackListener(listener);

        return view;
    }
}
