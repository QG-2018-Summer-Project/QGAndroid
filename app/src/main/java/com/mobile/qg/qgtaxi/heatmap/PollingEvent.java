package com.mobile.qg.qgtaxi.heatmap;

import android.content.SharedPreferences;

import static com.mobile.qg.qgtaxi.KeyValueConstant.DEFAULT_PERIOD;
import static com.mobile.qg.qgtaxi.KeyValueConstant.KEY_PERIOD;

/**
 * Created by 11234 on 2018/8/16.
 */
public class PollingEvent {

    public PollingEvent(long period) {
        this.period = period;
    }

    public long getPeriod() {
        return period;
    }

    private long period;

    public PollingEvent(SharedPreferences sharedPreferences) {
        period = Long.parseLong(sharedPreferences.getString(KEY_PERIOD, DEFAULT_PERIOD));
    }
}
