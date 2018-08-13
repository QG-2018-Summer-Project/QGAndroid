package com.mobile.qg.qgtaxi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.VisibleRegion;
import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;
import com.mobile.qg.qgtaxi.entity.LatLngFactory;
import com.mobile.qg.qgtaxi.heatmap.HeatMapApi;
import com.mobile.qg.qgtaxi.heatmap.HeatMapCallback;
import com.mobile.qg.qgtaxi.heatmap.HeatMapLatLng;
import com.mobile.qg.qgtaxi.heatmap.HeatMapOverlay;
import com.mobile.qg.qgtaxi.heatmap.HeatMapResponse;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;

public final class MainActivity extends AppCompatActivity implements
        AMap.OnMapClickListener,
        AMap.OnCameraChangeListener {

    private static final String TAG = "MainActivity";

    //广州中心坐标
    private static final LatLng GUANGZHOU = new LatLng(23.117055, 113.275995);

    //绿色标记
    private static final BitmapDescriptor GREEN_MARKER = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

    //轮询间隔
    private static long sPollingInterval = 5000;

    @BindView(R.id.map)
    protected MapView mMapView;

    private AMap mAMap;

    @BindView(R.id.t1)
    protected TextView t1;
    @BindView(R.id.t2)
    protected TextView t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMapView.onCreate(savedInstanceState);

        mAMap = mMapView.getMap();
        mAMap.setOnMapClickListener(this);
        mAMap.setOnCameraChangeListener(this);

        changeCamera(GUANGZHOU, false);
        isHeatMapping = true;

        requestPermission();

        pollHeatMap();

    }

    /**
     * 定位到某一经纬度
     *
     * @param latLng       经纬度
     * @param shouldMarked 是否标记
     */
    private void changeCamera(LatLng latLng, boolean shouldMarked) {
        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 11, 0, 0)));
        if (shouldMarked) {
            mAMap.clear();
            mAMap.addMarker(new MarkerOptions().position(latLng).icon(GREEN_MARKER));
        }
    }

    /**
     * 显示实时热力图
     *
     * @param latLng 上传类
     */
    private void showLiveHeatMap(CurrentLatLng latLng) {
        Log.e(TAG, "showLiveHeatMap: " + latLng.toString());
        HeatMapApi.getInstance().liveHeatMap(latLng, new HeatMapCallback() {
            @Override
            public void result(int status, List<HeatMapLatLng> pointSet) {
                Log.e(TAG, "result: " + status);
                if (status == 200) {
                    mAMap.addTileOverlay(HeatMapOverlay.getHeatMapOverlay(pointSet));
                }
            }
        });
    }

    private boolean isHeatMapping;

    /**
     * RxJava2.0
     * 背压 + 轮询热力图
     */
    private void pollHeatMap() {
        Flowable.create(new FlowableOnSubscribe<TileOverlayOptions>() {
            @Override
            public void subscribe(FlowableEmitter<TileOverlayOptions> e) throws InterruptedException {

                while (!isDestroyed()) {

                    if (isHeatMapping) {
                        VisibleRegion region = mAMap.getProjection().getVisibleRegion();
                        CurrentLatLng latLng = LatLngFactory.INSTANCE.getCurrent(region.farLeft, region.nearRight);
                        Response response = HeatMapApi.getInstance().liveHeatMap(latLng);

                        try {
                            if (response != null && response.code() == 200) {
                                String responseData = Objects.requireNonNull(response.body()).string();
                                HeatMapResponse heatMapResponse = new Gson().fromJson(responseData, HeatMapResponse.class);
                                TileOverlayOptions options = HeatMapOverlay.getHeatMapOverlay(heatMapResponse.getPointSet());
                                e.onNext(options);
                            }
                        } catch (Exception ignore) {

                        }
                    }

                    Thread.sleep(sPollingInterval);

                }

                e.onComplete();

            }
        }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TileOverlayOptions>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(TileOverlayOptions tileOverlayOptions) {
                        mAMap.addTileOverlay(tileOverlayOptions);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: 结束");
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 地图点击事件
     * （移动相机，标记）
     *
     * @param latLng 经纬度
     */
    @Override
    public void onMapClick(LatLng latLng) {
        changeCamera(latLng, true);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        VisibleRegion region = mAMap.getProjection().getVisibleRegion();
        t1.setText(region.farLeft.latitude + " " + region.farLeft.longitude);
        t2.setText(region.nearRight.latitude + " " + region.nearRight.longitude);

        isHeatMapping = false;

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

        isHeatMapping = true;

    }

    /**
     * 申请读写权限，（以保存Crash信息）
     */
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

}
