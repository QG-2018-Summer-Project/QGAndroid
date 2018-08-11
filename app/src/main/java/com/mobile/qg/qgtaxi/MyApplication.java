package com.mobile.qg.qgtaxi;

import android.app.Application;

/**
 * Created by 11234 on 2018/8/11.
 * 加载CrashHandler
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
    }

}
