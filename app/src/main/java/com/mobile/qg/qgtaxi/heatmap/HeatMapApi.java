package com.mobile.qg.qgtaxi.heatmap;

import android.util.Log;

import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;
import com.mobile.qg.qgtaxi.entity.PeriodLatLng;
import com.mobile.qg.qgtaxi.entity.PredictedLatLng;

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

/**
 * Created by 11234 on 2018/8/12.
 * 热力图Api
 */
public class HeatMapApi {

    /**
     * Changeable
     */
    private static String IP = "localhost";
    private static long sConnectTimeOut = 5000;


    private final static String ROOT = "http://" + IP + ":8080/qgtaxi/";
    private final static String MAPS = "maps/";

    private final static String CURRENT = ROOT + MAPS + "liveheatmap";
    private final static String PERIOD = ROOT + MAPS + "querymap";
    private static final String DEMAND = ROOT + MAPS + "demanded";
    private static final String COUNT = ROOT + MAPS + "count";

    /**
     * 单例
     */
    private static final HeatMapApi ourInstance = new HeatMapApi();

    public synchronized static HeatMapApi getInstance() {
        return ourInstance;
    }

    private HeatMapApi() {
    }

    /**
     * Sync - Post
     * OkHttp3
     *
     * @param action   POST动作
     * @param jsonBody json内容体
     */
    private Response post(@NonNull String action, @NonNull String jsonBody) {
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
        try {
            return call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public Response liveHeatMap(CurrentLatLng latLng) {
        return post(CURRENT, new Gson().toJson(latLng));
    }


    public void periodHeatMap(PeriodLatLng latLng, Callback callback) {
        post(PERIOD, new Gson().toJson(latLng), callback);
    }

    public void predictedDemand(PredictedLatLng latLng, Callback callback) {
        post(DEMAND, new Gson().toJson(latLng), callback);
    }

    public void predictedCount(PredictedLatLng latLng, Callback callback) {
        post(COUNT, new Gson().toJson(latLng), callback);
    }


    /**
     * Builder模式
     * 设置连接超时/ip地址
     */
    private final static ApiEditor API_EDITOR = new ApiEditor();

    public final static class ApiEditor {

        private String ip;
        private long timeOut;

        public ApiEditor connectTimeOut(long time) {
            timeOut = time;
            return API_EDITOR;
        }

        public ApiEditor ip(String ip) {
            this.ip = ip;
            return API_EDITOR;
        }

        public void accept() {
            sConnectTimeOut = timeOut;
            IP = ip;
        }

    }

    public ApiEditor edit() {
        return API_EDITOR;
    }


}
