package com.mobile.qg.qgtaxi.situation;

import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.base.BaseApi;

import okhttp3.Callback;

/**
 * Created by 93922 on 2018/8/19.
 * 描述：
 */
public class SituationApi extends BaseApi {

    private final static String CHART = "charts/";
    private final static String EXCEPTION = "exception";//异常

    private SituationApi() {

    }

    private static SituationApi ourInstance = new SituationApi();

    public synchronized static SituationApi getInstance() {
        return ourInstance;
    }

    public void requestSituation(Exception exception, Callback callback) {
        post(CHART + EXCEPTION, new Gson().toJson(exception), callback);
    }

}
