package com.mobile.qg.qgtaxi.route;

import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.base.BaseApi;
import com.mobile.qg.qgtaxi.route.entity.Routes;

import okhttp3.Response;

/**
 * Created by 11234 on 2018/8/19.
 */
public class RouteApi extends BaseApi {

    private final static String ROAD = "roadandcar/";

    private final static String BEST = "querybestway";

    private RouteApi() {

    }

    private static RouteApi mInstance = new RouteApi();

    public synchronized static RouteApi getInstance() {
        return mInstance;
    }

    /**
     * 搜索最佳路径
     *
     * @param routes
     * @return
     */
    public Response searchBestRoute(Routes routes) {
        return post( ROAD + BEST, new Gson().toJson(routes));
    }

}
