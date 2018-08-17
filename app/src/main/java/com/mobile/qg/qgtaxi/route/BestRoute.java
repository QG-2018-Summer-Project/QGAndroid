package com.mobile.qg.qgtaxi.route;

import android.content.Context;
import android.util.Log;

import com.amap.api.maps.AMap;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.route.entity.RouteFactory;
import com.mobile.qg.qgtaxi.route.entity.RouteResponse;
import com.mobile.qg.qgtaxi.route.entity.Routes;

import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import lombok.Builder;
import lombok.val;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


/**
 * Created by 11234 on 2018/8/17.
 */
@Builder
public class BestRoute {

    /**
     * require setting
     */
    private AMap aMap;
    private Context context;

    private RouteSearch search;
    private DriveRouteResult mResult;
    private Disposable mDisposable;

//    public void search(final LatLonPoint from, LatLonPoint to) {
//
//        if (search == null) {
//            search = new RouteSearch(context);
//        }
//
//        if (mDisposable != null && !mDisposable.isDisposed()) {
//            mDisposable.dispose();
//        }
//
//        val fromTo = new RouteSearch.FromAndTo(from, to);
//        val query = new RouteSearch.DriveRouteQuery(fromTo, RouteSearch.DRIVING_SINGLE_DEFAULT, null, null, "");
//        mDisposable = Observable.create(new ObservableOnSubscribe<DriveRouteResult>() {
//            /**
//             * 调用高德Api获取路径
//             * @param emitter
//             * @throws Exception
//             */
//            @Override
//            public void subscribe(ObservableEmitter<DriveRouteResult> emitter) {
//
//                try {
//                    DriveRouteResult result = search.calculateDriveRoute(query);
//                    Log.e(TAG, "subscribe: " + result);
//                    if (result != null) {
//                        emitter.onNext(result);
//                    }
//
//                } catch (Exception ignore) {
//                    emitter.onComplete();
//                }
//
//            }
//
//        }).map(new Function<DriveRouteResult, Routes>() {
//            /**
//             * 高德搜索结果转Routes实体类
//             * @param routeResult
//             * @return
//             * @throws Exception
//             */
//            @Override
//            public Routes apply(DriveRouteResult routeResult) {
//
//                if (routeResult.getPaths() != null) {
//                    mResult = routeResult;
//                }
//                return RouteFactory.getRoutes(routeResult);
//
//            }
//        }).map(new Function<Routes, DrivingRouteOverlay>() {
//            /**
//             * Routes交由数据挖掘分析最佳路径
//             * @param routes
//             * @return
//             */
//            @Override
//            public DrivingRouteOverlay apply(Routes routes) {
//                Log.e(TAG, "apply: " + routes);
//
//                try {
//                    Response response = RouteApi.getInstance().searchBestRoute(routes);
//                    if (response != null && response.code() == 2000) {
//                        String responseData = Objects.requireNonNull(response.body()).string();
//                        RouteResponse routeResponse = new Gson().fromJson(responseData, RouteResponse.class);
//                        return new DrivingRouteOverlay(
//                                context, aMap, mResult.getPaths().get(routeResponse.getIndex()),
//                                mResult.getStartPos(), mResult.getTargetPos(), null
//                        );
//                    }
//                } catch (Exception ignore) {
//                }
//                return null;
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<DrivingRouteOverlay>() {
//                    /**
//                     * 显示路径
//                     * @param overlay
//                     */
//                    @Override
//                    public void accept(DrivingRouteOverlay overlay) {
//                        overlay.setNodeIconVisibility(true);
//                        overlay.removeFromMap();
//                        overlay.addToMap();
//                        overlay.zoomToSpan();
//                    }
//                });
//    }

    public void search(final LatLonPoint from, LatLonPoint to) {

        if (search == null) {
            search = new RouteSearch(context);
        }

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }

        val fromTo = new RouteSearch.FromAndTo(from, to);
        val query = new RouteSearch.DriveRouteQuery(fromTo, RouteSearch.DRIVING_SINGLE_DEFAULT, null, null, "");

        mDisposable = Observable.create(new ObservableOnSubscribe<DrivingRouteOverlay>() {
            @Override
            public void subscribe(ObservableEmitter<DrivingRouteOverlay> emitter) throws Exception {
                DriveRouteResult result = search.calculateDriveRoute(query);
                if (result == null || result.getPaths() == null) {
                    return;
                }

                Routes routes = RouteFactory.getRoutes(result);

                Response response = RouteApi.getInstance().searchBestRoute(routes);
                if (response != null && response.code() == 2000) {
                    String responseData = Objects.requireNonNull(response.body()).string();
                    RouteResponse routeResponse = new Gson().fromJson(responseData, RouteResponse.class);
                    DrivingRouteOverlay overlay = new DrivingRouteOverlay(
                            context, aMap, mResult.getPaths().get(routeResponse.getIndex()),
                            mResult.getStartPos(), mResult.getTargetPos(), null
                    );
                    emitter.onNext(overlay);
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DrivingRouteOverlay>() {
                    @Override
                    public void accept(DrivingRouteOverlay drivingRouteOverlay) {
                        drivingRouteOverlay.setNodeIconVisibility(true);
                        drivingRouteOverlay.removeFromMap();
                        drivingRouteOverlay.addToMap();
                        drivingRouteOverlay.zoomToSpan();
                    }
                });

    }

}
