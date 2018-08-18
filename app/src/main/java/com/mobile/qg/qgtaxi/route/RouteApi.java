package com.mobile.qg.qgtaxi.route;

import android.util.Log;

import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.route.entity.Routes;
import com.mobile.qg.qgtaxi.route.entity.Point;
import com.mobile.qg.qgtaxi.route.entity.Route;
import com.mobile.qg.qgtaxi.route.entity.Step;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.NonNull;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by 11234 on 2018/8/17.
 */
public class RouteApi {
    /**
     * Changeable
     */
    private static String IP = "localhost";
    private static long sConnectTimeOut = 5000;


    private final static String ROOT = "http://" + IP + ":8080/qgtaxi/";
    private final static String ROAD = "roadandcar/";

    private final static String BEST = ROOT + ROAD + "querybestway";

    private static final RouteApi ourInstance = new RouteApi();

    public static RouteApi getInstance() {
        return ourInstance;
    }

    private RouteApi() {
    }

    /**
     * Sync - Post
     * OkHttp3
     *
     * @param action   POST动作
     * @param jsonBody json内容体
     */
    private Response post(@NonNull String action, @NonNull String jsonBody) {
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
     * 搜索最佳路径
     *
     * @param routes
     * @return
     */
    public Response searchBestRoute(Routes routes) {
        return post(BEST, new Gson().toJson(routes));
    }


    public static void a() {
        Point point = new Point(100, 200);
        List<Point> paths = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            paths.add(point);
        }

        Step step = new Step(paths, point, point, 2018, 2018);
        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            steps.add(step);
        }

        Route route = new Route(1, steps, 2018, 2018);
        List<Route> routes = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            routes.add(route);
        }

        Routes bestRoute = new Routes(routes);
        Log.e(TAG, "a: " + new Gson().toJson(bestRoute));


    }


}
