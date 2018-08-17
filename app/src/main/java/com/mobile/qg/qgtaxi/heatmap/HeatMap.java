package com.mobile.qg.qgtaxi.heatmap;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.VisibleRegion;
import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;
import com.mobile.qg.qgtaxi.entity.LatLngFactory;
import com.mobile.qg.qgtaxi.entity.PeriodLatLng;
import com.mobile.qg.qgtaxi.entity.PredictedLatLng;

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

        mDisposable = Observable.interval(0, period, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(final Long aLong) {

                        Observable.create(new ObservableOnSubscribe<TileOverlayOptions>() {
                            @Override
                            public void subscribe(ObservableEmitter<TileOverlayOptions> emitter) throws Exception {
                                VisibleRegion region = aMap.getProjection().getVisibleRegion();
                                CurrentLatLng latLng = LatLngFactory.INSTANCE.getCurrent(region.farLeft, region.nearRight);
                                Response response = HeatMapApi.getInstance().liveHeatMap(latLng);

                                try {

                                    if (response != null && response.code() == 200) {
                                        String responseData = Objects.requireNonNull(response.body()).string();
                                        HeatMapResponse heatMapResponse = new Gson().fromJson(responseData, HeatMapResponse.class);
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
     *
     * @param latLng
     */
    public void showPeriod(PeriodLatLng latLng) {
        cancel();
        HeatMapApi.getInstance().periodHeatMap(latLng, new HeatMapCallback() {
            @Override
            public void result(int status, List<HeatMapLatLng> pointSet) {
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
     * 预测车流量
     *
     * @param latLng
     */
    public void predictCount(PredictedLatLng latLng) {
        cancel();
        HeatMapApi.getInstance().predictedCount(latLng, new HeatMapCallback() {
            @Override
            public void result(int status, List<HeatMapLatLng> pointSet) {
                if (status == 2000) {
                    TileOverlayOptions options = HeatMapOverlay.getHeatMapOverlay(pointSet);
                    aMap.addTileOverlay(options);
                }
            }
        });
    }

    /**
     * 预测需求量
     *
     * @param latLng
     */
    public void predictDemand(PredictedLatLng latLng) {
        cancel();
        HeatMapApi.getInstance().predictedCount(latLng, new HeatMapCallback() {
            @Override
            public void result(int status, List<HeatMapLatLng> pointSet) {
                if (status == 2000) {
                    TileOverlayOptions options = HeatMapOverlay.getHeatMapOverlay(pointSet);
                    aMap.addTileOverlay(options);
                }
            }
        });
    }

    public void setPeriod(long period) {
        this.period = period;
    }

}
