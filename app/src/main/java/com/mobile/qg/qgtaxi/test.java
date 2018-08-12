package com.mobile.qg.qgtaxi;

import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by 93922 on 2018/8/11.
 * 描述：
 */

public class test {

    public static String testGson(Object o) {
        return new Gson().toJson(o);
    }

    public static void print(String s) {
        Log.e("Test Gson.", "print: " + s);
    }


}
