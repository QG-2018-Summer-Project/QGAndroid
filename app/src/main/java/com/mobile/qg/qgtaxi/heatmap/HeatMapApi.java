package com.mobile.qg.qgtaxi.heatmap;

import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.base.BaseApi;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;
import com.mobile.qg.qgtaxi.entity.PeriodLatLng;
import com.mobile.qg.qgtaxi.entity.PredictedLatLng;

import okhttp3.Response;

/**
 * Created by 11234 on 2018/8/19.
 */
public class HeatMapApi extends BaseApi {

    private final static String MAP = "maps/";

    private final static String CURRENT = "liveheatmap";
    private final static String PERIOD = "querymap";
    private static final String DEMAND = "demanded";
    private static final String COUNT = "count";

    private HeatMapApi() {

    }

    private static HeatMapApi mInstance = new HeatMapApi();

    public synchronized static HeatMapApi getInstance() {
        return mInstance;
    }

    public Response liveHeatMap(CurrentLatLng latLng) {
        return post(MAP + CURRENT, new Gson().toJson(latLng));
    }

    public Response periodHeatMap(PeriodLatLng latLng) {
        return post(MAP + PERIOD, new Gson().toJson(latLng));
    }

    public Response predictedDemand(PredictedLatLng latLng) {
        return post(MAP + DEMAND, new Gson().toJson(latLng));
    }

    public Response predictedCount(PredictedLatLng latLng) {
        return post(MAP + COUNT, new Gson().toJson(latLng));
    }

}
