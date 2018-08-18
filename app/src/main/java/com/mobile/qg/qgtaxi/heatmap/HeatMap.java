package com.mobile.qg.qgtaxi.heatmap;

import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.VisibleRegion;
import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;
import com.mobile.qg.qgtaxi.entity.LatLngFactory;
import com.mobile.qg.qgtaxi.entity.PeriodLatLng;
import com.mobile.qg.qgtaxi.entity.PredictedLatLng;
import com.mobile.qg.qgtaxi.util.TimeUtil;

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

    private AMap aMap;
    private boolean isHeatMapping;
    private boolean isStop;
    private long period;
    private Disposable mDisposable;

    public HeatMap showLive() {

        if (isShowing()) {
            return this;
        }

        mDisposable = Observable.interval(0, period, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(final Long aLong) {

                        Observable.create(new ObservableOnSubscribe<TileOverlayOptions>() {
                            @Override
                            public void subscribe(ObservableEmitter<TileOverlayOptions> emitter) {
                                VisibleRegion region = aMap.getProjection().getVisibleRegion();
                                CurrentLatLng latLng = LatLngFactory.INSTANCE.getCurrent(region.farLeft, region.nearRight);
                                Response response = HeatMapApi.getInstance().liveHeatMap(latLng);

                                try {

                                    if (response != null && response.code() == 200) {
                                        String responseData = Objects.requireNonNull(response.body()).string();
                                        HeatMapResponse heatMapResponse = new Gson().fromJson(responseData, HeatMapResponse.class);
                                        Log.e(TAG, "subscribe: " + heatMapResponse.getPointSet().size());
                                        TileOverlayOptions options = HeatMapOverlay.getHeatMapOverlay(heatMapResponse.getPointSet());
                                        emitter.onNext(options);
                                    }

                                } catch (Exception e) {
                                    emitter.onError(e);
                                }

                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<TileOverlayOptions>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(TileOverlayOptions tileOverlayOptions) {
                                        aMap.clear();
                                        aMap.addTileOverlay(tileOverlayOptions);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        //网络错误
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                });
        return this;
    }

    public void cancel() {
        if (isShowing()) {
            mDisposable.dispose();
        }
    }

    public boolean isShowing() {
        return mDisposable != null && !mDisposable.isDisposed();
    }

    /**
     * 过去时间段的热力图
     */
    public void showPeriod(String time) {
        cancel();
        VisibleRegion region = aMap.getProjection().getVisibleRegion();
        final long start = TimeUtil.getLong(time);
        long end = start + 3600000L;
        PeriodLatLng latLng = LatLngFactory.INSTANCE.getPeriod(region.farLeft, region.nearRight, start, end);

        HeatMapApi.getInstance().periodHeatMap(latLng, new HeatMapCallback() {
            @Override
            public void result(int status, List<HeatMapLatLng> pointSet) {
                Log.e(TAG, "result: " + status + pointSet);
                if (status == 2000 && pointSet != null) {
                    TileOverlayOptions options = HeatMapOverlay.getHeatMapOverlay(pointSet);
                    aMap.addTileOverlay(options);
                } else {
                    //TODO 网络异常
                }
            }
        });
    }

    /**
     * 预测车流量
     */
    public void predictCount() {
        cancel();
        VisibleRegion region = aMap.getProjection().getVisibleRegion();
        PredictedLatLng latLng = LatLngFactory.INSTANCE.getPredict(region.farLeft, region.nearRight);
        HeatMapApi.getInstance().predictedCount(latLng, new HeatMapCallback() {
            @Override
            public void result(int status, List<HeatMapLatLng> pointSet) {
                Log.e(TAG, "result: " + status);
                if (status == 2000) {
                    TileOverlayOptions options = HeatMapOverlay.getHeatMapOverlay(pointSet);
                    aMap.addTileOverlay(options);
                } else {
                    //TODO 网络异常
                }
            }
        });
    }

    /**
     * 预测需求量
     */
    public void predictDemand() {
        cancel();
        VisibleRegion region = aMap.getProjection().getVisibleRegion();
        PredictedLatLng latLng = LatLngFactory.INSTANCE.getPredict(region.farLeft, region.nearRight);
        HeatMapApi.getInstance().predictedCount(latLng, new HeatMapCallback() {
            @Override
            public void result(int status, List<HeatMapLatLng> pointSet) {
                Log.e(TAG, "result: " + status + pointSet);
                if (status == 2000) {
                    TileOverlayOptions options = HeatMapOverlay.getHeatMapOverlay(pointSet);
                    aMap.addTileOverlay(options);
                } else {
                    //TODO 网络异常
                }
            }
        });
    }

    public void setPeriod(long period) {
        this.period = period;
    }

}
