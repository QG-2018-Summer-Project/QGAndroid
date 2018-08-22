package com.mobile.qg.qgtaxi.route;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.route.entity.FromTo;
import com.mobile.qg.qgtaxi.route.entity.RouteFactory;
import com.mobile.qg.qgtaxi.route.entity.RouteResponse;
import com.mobile.qg.qgtaxi.route.entity.Routes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by 11234 on 2018/8/17.
 * 路径推荐
 */
@Builder
public class BestRoute {

    /**
     * require setting
     */
    private AMap aMap;
    private Context context;

    /**
     * settable
     */
    private ProgressBar progressBar;
    private List<View> showAble;
    private List<View> hideAble;
    private TextView durationTv;
    private TextView distanceTv;

    /**
     * not available
     */
    private RouteSearch mSearch;
    private Disposable mDisposable;
    private boolean isShowing;

    /**
     * 添加控件，当开始路径规划时展示
     *
     * @param views 控件
     * @return 当前对象
     */
    public BestRoute willShow(View... views) {
        showAble = new ArrayList<>();
        showAble.addAll(Arrays.asList(views));
        return this;
    }

    /**
     * 添加控件，当开始路径规划时隐藏
     *
     * @param views 控件
     * @return 当前对象
     */
    public BestRoute willHide(View... views) {
        hideAble = new ArrayList<>();
        hideAble.addAll(Arrays.asList(views));
        return this;
    }

    /**
     * 开始路径规划
     *
     * @param from 起点
     * @param to   终点
     */
    private void search(final LatLonPoint from, LatLonPoint to) {

        if (mSearch == null) {
            mSearch = new RouteSearch(context);
        }

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        val fromTo = new RouteSearch.FromAndTo(from, to);
        val query = new RouteSearch.DriveRouteQuery(fromTo, RouteSearch.DRIVING_MULTI_CHOICE_AVOID_CONGESTION, null, null, "");

        mDisposable = Observable.create(new ObservableOnSubscribe<Showable>() {
            @Override
            public void subscribe(ObservableEmitter<Showable> emitter) throws Exception {
                DriveRouteResult result = mSearch.calculateDriveRoute(query);
                if (result == null || result.getPaths() == null) {
                    emitter.onNext(Showable.builder().build());
                    return;
                }

                Routes routes = RouteFactory.getRoutes(result);
                Response response = RouteApi.getInstance().searchBestRoute(routes);

                if (response != null && response.isSuccessful()) {
                    String responseData = Objects.requireNonNull(response.body()).string();
                    Log.e(TAG, "subscribe: " + responseData);
                    RouteResponse routeResponse = new Gson().fromJson(responseData, RouteResponse.class);
                    DrivingRouteOverlay overlay = new DrivingRouteOverlay(
                            aMap, result.getPaths().get(routeResponse.getIndex()),
                            result.getStartPos(), result.getTargetPos(), null);

                    int index = routeResponse.getIndex();

                    Showable showable = Showable.builder()
                            .distance(String.valueOf(routes.getRoutes().get(index).getDistance()))
                            .time(String.valueOf(routes.getRoutes().get(index).getAllTime()))
                            .overlay(overlay)
                            .build();

                    emitter.onNext(showable);
                } else {
                    emitter.onNext(Showable.builder().build());
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Showable>() {
                    @Override
                    public void accept(Showable showable) {

                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                        if (showable.overlay == null) {
                            Toast.makeText(context, "路径推荐失败。", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (View view : showAble) {
                            view.setVisibility(View.VISIBLE);
                        }

                        for (View view : hideAble) {
                            view.setVisibility(View.GONE);
                        }

                        isShowing = true;

                        aMap.clear();

                        DrivingRouteOverlay overlay = showable.getOverlay();
                        overlay.setNodeIconVisibility(true);
                        overlay.removeFromMap();
                        overlay.addToMap();
                        overlay.zoomToSpan();

                        if (distanceTv != null) {
                            distanceTv.setText(showable.getDistance() + " 米");
                        }

                        if (durationTv != null) {
                            durationTv.setText(showable.getTime() + " 秒");
                        }

                    }
                });

    }

    /**
     * 开始路径规划
     *
     * @param group 起终组
     */
    public void search(FromTo group) {
        search(group.getStartPoint(), group.getEndPoint());
    }

    /**
     * 取消规划
     */
    public void cancel() {

        aMap.clear();

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        for (View view : showAble) {
            view.setVisibility(View.GONE);
        }

        for (View view : hideAble) {
            view.setVisibility(View.VISIBLE);
        }

        isShowing = false;

    }

    /**
     * 是否在路径规划
     *
     * @return true/false
     */
    public boolean isShowing() {
        return isShowing;
    }

    /**
     * 发送内容的实体类
     */
    @Data
    @Builder
    private static class Showable {
        private DrivingRouteOverlay overlay;
        private String time;
        private String distance;
    }


}
