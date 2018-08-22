package com.mobile.qg.qgtaxi.chart;

import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.base.BaseApi;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;

import okhttp3.Callback;

/**
 * Created by 11234 on 2018/8/19.
 */
public class ChartApi extends BaseApi {

    private final static String CHART = "charts/";

    private final static String CHANGE = "changepercent";//地区流量变化率
    private final static String UTILIZE = "utilizepercent";//出租车利用率
    private final static String CROWD = "crowded";//拥堵率

    private ChartApi() {

    }

    private static ChartApi mInstance = new ChartApi();

    public synchronized static ChartApi getInstance() {
        return mInstance;
    }

    public void requestChangePercent(CurrentLatLng latLng, Callback callback) {
        post(CHART + CHANGE, new Gson().toJson(latLng), callback);
    }

    public void requestUtilizePercent(CurrentLatLng latLng, Callback callback) {
        post(CHART + UTILIZE, new Gson().toJson(latLng), callback);
    }

    public void requestCrowdPercent(CurrentLatLng latLng, Callback callback) {
        post(CHART + CROWD, new Gson().toJson(latLng), callback);
    }

}
