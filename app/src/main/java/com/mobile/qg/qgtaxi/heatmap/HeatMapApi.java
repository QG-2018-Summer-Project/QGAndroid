package com.mobile.qg.qgtaxi.heatmap;

import android.util.Log;

import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;
import com.mobile.qg.qgtaxi.entity.PeriodLatLng;

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

    private final static String IP = "localhost";
    private final static String ROOT = "http://" + IP + ":8080/qgtaxi/";
    private final static String MAPS = "maps/";

    private final static String CURRENT_HEATMAP = ROOT + MAPS + "liveheatmap";
    private final static String PERIOD_HEATMAP = ROOT + MAPS + "querymap";

    private static final long CONNECT_TIMEOUT = 5000;

    /**
     * 单例
     */
    private static final HeatMapApi ourInstance = new HeatMapApi();

    public static HeatMapApi getInstance() {
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
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
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
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
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
        return post(CURRENT_HEATMAP, new Gson().toJson(latLng));
    }

    public Response periodHeatMap(PeriodLatLng latLng) {
        return post(PERIOD_HEATMAP, new Gson().toJson(latLng));
    }

    public void liveHeatMap(CurrentLatLng latLng, Callback callback) {
        post(CURRENT_HEATMAP, new Gson().toJson(latLng), callback);
    }

    public void periodHeatMap(PeriodLatLng latLng, Callback callback) {
        post(PERIOD_HEATMAP, new Gson().toJson(latLng), callback);
    }

}
