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
    private String IP = "localhost";
    private long sConnectTimeOut = 10000;
    private String ROOT = "http://" + IP + ":8080/qgtaxi/maps/";

    private final static String CURRENT = "liveheatmap";
    private final static String PERIOD = "querymap";
    private static final String DEMAND = "demanded";
    private static final String COUNT = "count";

    /**
     * 单例
     */
    private static HeatMapApi ourInstance = new HeatMapApi();

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
        Log.e(TAG, "post: " + action);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(sConnectTimeOut, TimeUnit.MILLISECONDS)
                .build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
        Request request = new Request.Builder()
                .url(action)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            Log.e(TAG, "post: " + response.code());
            Log.e(TAG, "post: " + response.isSuccessful());
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "post: fa" + e.toString());
            return null;
        }
//        Call call = client.newCall(request);
//        try {
//            return call.execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
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
        return post(ROOT + CURRENT, new Gson().toJson(latLng));
    }


    public void periodHeatMap(PeriodLatLng latLng, Callback callback) {
        post(ROOT + PERIOD, new Gson().toJson(latLng), callback);
    }

    public void predictedDemand(PredictedLatLng latLng, Callback callback) {
        post(ROOT + DEMAND, new Gson().toJson(latLng), callback);
    }

    public void predictedCount(PredictedLatLng latLng, Callback callback) {
        post(ROOT + COUNT, new Gson().toJson(latLng), callback);
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
            getInstance().sConnectTimeOut = timeOut;
            getInstance().IP = ip;
            getInstance().ROOT = "http://" + ip + ":8080/qgtaxi/maps/";
        }

    }

    public ApiEditor edit() {
        return API_EDITOR;
    }


}
