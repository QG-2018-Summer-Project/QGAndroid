package com.mobile.qg.qgtaxi;

import android.app.Application;
import android.widget.Toast;

import com.mobile.qg.qgtaxi.base.BaseApi;

/**
 * Created by 11234 on 2018/8/11.
 * 加载CrashHandler
 * 初始化Api
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        BaseApi.edit().initDefault(this).accept();

    }

}
