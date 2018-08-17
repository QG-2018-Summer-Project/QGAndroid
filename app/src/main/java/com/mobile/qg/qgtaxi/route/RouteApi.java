package com.mobile.qg.qgtaxi.route;

import android.util.Log;

import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.route.entity.BestRoute;
import com.mobile.qg.qgtaxi.route.entity.Point;
import com.mobile.qg.qgtaxi.route.entity.Route;
import com.mobile.qg.qgtaxi.route.entity.Step;

import java.util.ArrayList;
import java.util.List;

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

    public static void a() {
        Point point = new Point(100, 200);
        List<Point> paths = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            paths.add(point);
        }

        Step step = new Step(paths, point, point, "time");
        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            steps.add(step);
        }

        Route route = new Route(1, steps, "allTime", "distance");
        List<Route> routes = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            routes.add(route);
        }

        BestRoute bestRoute = new BestRoute(routes);
        Log.e(TAG, "a: " + new Gson().toJson(bestRoute));


    }


}
