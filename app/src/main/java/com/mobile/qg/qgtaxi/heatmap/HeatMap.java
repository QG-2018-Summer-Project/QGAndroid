package com.mobile.qg.qgtaxi.heatmap;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.VisibleRegion;
import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.base.BaseApi;
import com.mobile.qg.qgtaxi.entity.LatLngFactory;
import com.mobile.qg.qgtaxi.util.TimeUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lombok.Builder;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by 11234 on 2018/8/16.
 * 调用API的类
 * 在AMap上绘制热力图
 */
@Builder
public class HeatMap {

    /**
     * require setting
     */
    private AMap aMap;
    private Context context;

    /**
     * settable
     */
    private ProgressBar progressBar;

    /**
     * not available
     */
    private Disposable mDisposable;

    /**
     * 画热力图
     * 操作ProgressBar
     *
     * @return 观察者
     */
    private Observer<List<HeatMapLatLng>> getObserver() {
        return new Observer<List<HeatMapLatLng>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<HeatMapLatLng> pointSet) {

                TileOverlayOptions options = HeatMapOverlay.getHeatMapOverlay(pointSet);
                aMap.clear();
                aMap.addTileOverlay(options);

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onError(Throwable e) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(context, "绘制热力图失败，请重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(context, "绘制热力图失败，请检查网络连接", Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * 发送经纬度列表
     *
     * @param response 请求
     * @param emitter  发送器
     * @throws IOException io异常
     */
    private void sendResponse(Response response, ObservableEmitter<List<HeatMapLatLng>> emitter) throws IOException {
        Log.e(TAG, "sendResponse: " + response);
        if (response != null && response.isSuccessful()) {
            String responseData = Objects.requireNonNull(response.body()).string();
            HeatMapResponse heatMapResponse = new Gson().fromJson(responseData, HeatMapResponse.class);
            Log.e(TAG, "sendResponse: " + heatMapResponse);
            if (heatMapResponse.getStatus() == 2000) {
                emitter.onNext(heatMapResponse.getPointSet());
            } else {
                emitter.onComplete();
            }
        } else {
            emitter.onComplete();
        }
    }

    /**
     * 展示实时热力图
     */
    public void showLive() {
        Toast.makeText(context, "开始绘制热力图", Toast.LENGTH_SHORT).show();
        mDisposable = Observable.interval(0, BaseApi.getPeriod(), TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(final Long aLong) {

                        Observable.create(new ObservableOnSubscribe<List<HeatMapLatLng>>() {
                            @Override
                            public void subscribe(ObservableEmitter<List<HeatMapLatLng>> emitter) throws IOException {

                                VisibleRegion region = aMap.getProjection().getVisibleRegion();
                                sendResponse(HeatMapApi.Companion.getInstance()
                                        .liveHeatMap(LatLngFactory.INSTANCE.getCurrent(region.farLeft, region.nearRight)), emitter);
                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(getObserver());

                    }
                });

    }

    /**
     * 过去时间段的热力图
     */
    public void showPeriod(final String time) {
        cancel();

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        Observable.create(new ObservableOnSubscribe<List<HeatMapLatLng>>() {
            @Override
            public void subscribe(ObservableEmitter<List<HeatMapLatLng>> emitter) throws IOException {

                VisibleRegion region = aMap.getProjection().getVisibleRegion();
                long start = TimeUtil.getLong(time);
                long end = start + 1000 * 60 * 10;

                sendResponse(HeatMapApi.Companion.getInstance()
                        .periodHeatMap(LatLngFactory.INSTANCE.getPeriod(region.farLeft, region.nearRight, start, end)), emitter);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver());
    }

    /**
     * 预测车流量
     */
    public void predictCount() {

        cancel();

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        Observable.create(new ObservableOnSubscribe<List<HeatMapLatLng>>() {
            @Override
            public void subscribe(ObservableEmitter<List<HeatMapLatLng>> emitter) throws IOException {
                VisibleRegion region = aMap.getProjection().getVisibleRegion();
                sendResponse(HeatMapApi.Companion.getInstance()
                        .predictedCount(LatLngFactory.INSTANCE.getPredict(region.farLeft, region.nearRight)), emitter);
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver());
    }

    /**
     * 预测需求量
     */
    public void predictDemand() {

        cancel();

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        Observable.create(new ObservableOnSubscribe<List<HeatMapLatLng>>() {
            @Override
            public void subscribe(ObservableEmitter<List<HeatMapLatLng>> emitter) throws IOException {
                VisibleRegion region = aMap.getProjection().getVisibleRegion();
                sendResponse(HeatMapApi.Companion.getInstance()
                        .predictedDemand(LatLngFactory.INSTANCE.getPredict(region.farLeft, region.nearRight)), emitter);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver());
    }

    /**
     * 取消正在查询的事件
     */
    public void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * @return 是否正在查询
     */
    public boolean isShowing() {
        return mDisposable != null && !mDisposable.isDisposed();
    }
}
