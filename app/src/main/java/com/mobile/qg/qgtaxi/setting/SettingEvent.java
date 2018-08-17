package com.mobile.qg.qgtaxi.setting;

import android.content.SharedPreferences;

import static com.mobile.qg.qgtaxi.KeyValueConstant.DEFAULT_TIMEOUT;
import static com.mobile.qg.qgtaxi.KeyValueConstant.DEFAULT_URL;
import static com.mobile.qg.qgtaxi.KeyValueConstant.KEY_TIMEOUT;
import static com.mobile.qg.qgtaxi.KeyValueConstant.KEY_URL;

/**
 * Created by 11234 on 2018/8/16.
 */
public class SettingEvent {

    private String ip;
    private long timeOut;

    public SettingEvent(String ip, long timeOut) {
        this.ip = ip;
        this.timeOut = timeOut;
    }

    public String getIp() {
        return ip;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public SettingEvent(SharedPreferences sharedPreferences) {
        ip = sharedPreferences.getString(KEY_URL, DEFAULT_URL);
        timeOut = Long.parseLong(sharedPreferences.getString(KEY_TIMEOUT, DEFAULT_TIMEOUT));
    }

}
