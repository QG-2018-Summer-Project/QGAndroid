package com.mobile.qg.qgtaxi.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lombok.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.DEFAULT_PERIOD;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.DEFAULT_PORT;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.DEFAULT_TIMEOUT;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.DEFAULT_URL;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.KEY_PERIOD;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.KEY_PORT;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.KEY_TIMEOUT;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.KEY_URL;

/**
 * Created by 11234 on 2018/8/19.
 */
public class BaseApi {

    static int sConnectTimeOut = 100;
    static int sPeriod = 5;
    static String sIP = "localhost";
    static String sPort = "8080";

    protected static String getRootUrl() {
        return "http://" + sIP + ":" + sPort + "/qgtaxi/";
    }

    public static int getPeriod() {
        return sPeriod;
    }

    /**
     * Sync - Post
     * OkHttp3
     *
     * @param action   POST动作
     * @param jsonBody json内容体
     */
    protected Response post(@NonNull String action, @NonNull String jsonBody) {
        action = getRootUrl() + action;
        Log.e(TAG, "post: " + jsonBody);
        Log.e(TAG, "post: " + action);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(sConnectTimeOut, TimeUnit.SECONDS)
                .readTimeout(sConnectTimeOut, TimeUnit.SECONDS)
                .writeTimeout(sConnectTimeOut, TimeUnit.SECONDS)
                .build();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
        Request request = new Request.Builder()
                .url(action)
                .post(requestBody)
                .build();

        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Async - Post
     * OkHttp3
     *
     * @param action   POST动作
     * @param jsonBody json内容体
     * @param callback 回调
     */
    protected void post(@NonNull String action, @NonNull String jsonBody, Callback callback) {
        action = getRootUrl() + action;
        Log.e(TAG, "post: " + jsonBody);
        Log.e(TAG, "post: " + action);


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(sConnectTimeOut, TimeUnit.SECONDS)
                .readTimeout(sConnectTimeOut, TimeUnit.SECONDS)
                .writeTimeout(sConnectTimeOut, TimeUnit.SECONDS)
                .build();
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
        Request request = new Request.Builder()
                .url(action)
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }


    /**
     * Builder模式
     * 设置连接超时/ip地址
     */
    private final static ApiEditor API_EDITOR = new ApiEditor();

    public final static class ApiEditor {

        private String ip;
        private String port;
        private int timeOut;
        private int period;

        public ApiEditor initDefault(Context context) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            ip = sharedPreferences.getString(KEY_URL, DEFAULT_URL);
            port = sharedPreferences.getString(KEY_PORT, DEFAULT_PORT);
            timeOut = Integer.parseInt(sharedPreferences.getString(KEY_TIMEOUT, DEFAULT_TIMEOUT));
            period = Integer.parseInt(sharedPreferences.getString(KEY_PERIOD, DEFAULT_PERIOD));
            return API_EDITOR;
        }

        public ApiEditor timeout(int time) {
            timeOut = time;
            return API_EDITOR;
        }

        public ApiEditor ip(String ip) {
            this.ip = ip;
            return API_EDITOR;
        }

        public ApiEditor port(String port) {
            this.port = port;
            return API_EDITOR;
        }

        public ApiEditor period(int period) {
            this.period = period;
            return API_EDITOR;
        }

        public void accept() {
            sConnectTimeOut = timeOut;
            sPeriod = period;
            sIP = ip;
            sPort = port;
        }

    }

    public static ApiEditor edit() {
        return API_EDITOR;
    }

}