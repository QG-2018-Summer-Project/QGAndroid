package com.mobile.qg.qgtaxi.chart;

import android.util.Log;

import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;
import com.mobile.qg.qgtaxi.heatmap.HeatMapApi;

import java.util.concurrent.TimeUnit;

import lombok.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by 93922 on 2018/8/17.
 * 描述：
 */

public class ChartApi {

    private static final String TAG = "ChartApi";

    private static long sConnectTimeOut = 5000;
    private static String IP = "192.168.1.100";
    private static String ROOT = "http://" + IP + ":8080/qgtaxi/charts/";

    private final static String CHANGE = "changepercent";//地区流量变化率
    private final static String UTILIZE = "utilizepercent";//出租车利用率

    private static ChartApi mInstance = new ChartApi();

    public synchronized static ChartApi getInstance() {
        return mInstance;
    }

    private ChartApi() {

    }


    /**
     * Async - Post
     * OkHttp3
     *
     * @param action   POST动作
     * @param jsonBody json内容体
     * @param callback 回调
     */
    private void post(@NonNull String action, @NonNull String jsonBody, Callback callback) {
        Log.e(TAG, "post: " + jsonBody);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(sConnectTimeOut, TimeUnit.MILLISECONDS)
                .build();
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
        Request request = new Request.Builder()
                .url(action)
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void requestChangePercent(CurrentLatLng latLng, Callback callback) {
        Log.e(TAG, "requestChangePercent: ");
        post(ROOT + CHANGE, new Gson().toJson(latLng), callback);
    }


    public void requestUtilizePercent(CurrentLatLng latLng, Callback callback) {
        post(ROOT + UTILIZE, new Gson().toJson(latLng), callback);
    }


    /**
     * Builder模式
     * 设置连接超时/ip地址
     */
    private final static HeatMapApi.ApiEditor API_EDITOR = new HeatMapApi.ApiEditor();

    public final static class ApiEditor {

        private String ip;
        private long timeOut;

        public HeatMapApi.ApiEditor connectTimeOut(long time) {
            timeOut = time;
            return API_EDITOR;
        }

        public HeatMapApi.ApiEditor ip(String ip) {
            this.ip = ip;
            return API_EDITOR;
        }

        public void accept() {
            sConnectTimeOut = timeOut;
            IP = ip;
            ROOT = "http://" + ip + ":8080/qgtaxi/maps/";
        }

    }

    public HeatMapApi.ApiEditor edit() {
        return API_EDITOR;
    }

}
