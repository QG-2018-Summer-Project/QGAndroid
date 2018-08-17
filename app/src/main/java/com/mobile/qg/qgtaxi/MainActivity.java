package com.mobile.qg.qgtaxi;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.VisibleRegion;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Tip;
import com.mobile.qg.qgtaxi.detail.DetailFragment;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;
import com.mobile.qg.qgtaxi.heatmap.HeatMap;
import com.mobile.qg.qgtaxi.heatmap.HeatMapApi;
import com.mobile.qg.qgtaxi.heatmap.PollingEvent;
import com.mobile.qg.qgtaxi.route.RouteActivity;
import com.mobile.qg.qgtaxi.route.RouteApi;
import com.mobile.qg.qgtaxi.search.InputTipsActivity;
import com.mobile.qg.qgtaxi.setting.*;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class MainActivity extends AppCompatActivity implements
        AMap.OnMapClickListener,
        AMap.OnCameraChangeListener,
        AMap.OnPOIClickListener {

    private static final String TAG = "MainActivity";

    //广州中心坐标
    private static final LatLng GUANGZHOU = new LatLng(23.035219, 113.398205);

    //绿色标记
    private static final BitmapDescriptor GREEN_MARKER = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

    @BindView(R.id.map)
    protected MapView mMapView;

    private AMap mAMap;

    @BindView(R.id.t1)
    protected TextView t1;
    @BindView(R.id.t2)
    protected TextView t2;

    @OnClick(R.id.search)
    public void onSearch() {
        startActivity(new Intent(this, InputTipsActivity.class));
    }

    @OnClick(R.id.iv_setting)
    public void toSet() {
        startActivity(new Intent(this, SettingActivity.class));
    }

    @OnClick(R.id.iv_route)
    public void toRoute() {
        startActivity(new Intent(this, RouteActivity.class));
    }

    @OnClick(R.id.iv_aim)
    public void aim() {

        moveCamera(GUANGZHOU, true, 0);

    }


    /**
     * 切换普通地图/热力图模式
     */
    @OnClick(R.id.iv_change)
    public void changeHeatMapMode() {
        if (mHeatMap == null) {
            mHeatMap = HeatMap.builder()
                    .aMap(mAMap)
                    .isHeatMapping(true)
                    .isStop(false)
                    .period(new PollingEvent(PreferenceManager.getDefaultSharedPreferences(this)).getPeriod())
                    .build()
                    .showLive();
            Toast.makeText(MainActivity.this, "开始绘制热力图", Toast.LENGTH_SHORT).show();
        } else {
            if (mHeatMap.isShowing()) {
                mHeatMap.cancel();
                Toast.makeText(MainActivity.this, "暂停热力图", Toast.LENGTH_SHORT).show();
            } else {
                mHeatMap.showLive();
                Toast.makeText(MainActivity.this, "开始绘制热力图", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //热力图
    private HeatMap mHeatMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        mMapView.onCreate(savedInstanceState);

        mAMap = mMapView.getMap();
        mAMap.setOnMapClickListener(this);
        mAMap.setOnCameraChangeListener(this);
        mAMap.setOnPOIClickListener(this);

        moveCamera(GUANGZHOU, false, CAMERA_ZOOM_NORMAL);

        requestPermission();

        setApi(new SettingEvent(PreferenceManager.getDefaultSharedPreferences(this)));


    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void setApi(SettingEvent event) {
        HeatMapApi.getInstance()
                .edit()
                .connectTimeOut(event.getTimeOut())
                .ip(event.getIp())
                .accept();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void setHeatMapPeriod(PollingEvent event) {
        mHeatMap.setPeriod(event.getPeriod());
    }

    public static final int CAMERA_ZOOM_LARGE = 15;
    public static final int CAMERA_ZOOM_NORMAL = 11;

    /**
     * 定位到某一经纬度
     *
     * @param latLng       经纬度
     * @param shouldMarked 是否标记
     * @param zoom         地图缩进
     */
    private void moveCamera(LatLng latLng, boolean shouldMarked, int zoom) {
        if (zoom == 0) {
            mAMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        } else {
            mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, zoom, 0, 0)));
        }
        if (shouldMarked) {
            mAMap.clear();
            mAMap.addMarker(new MarkerOptions().position(latLng).icon(GREEN_MARKER));
        }
    }

    /**
     * 搜索地点 + 移动Camera中心 + 地图缩进
     *
     * @param tip
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getTips(Tip tip) {
        LatLonPoint point = tip.getPoint();
        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
        moveCamera(latLng, true, CAMERA_ZOOM_LARGE);
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
        EventBus.getDefault().unregister(this);
    }


    private boolean requireShow = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (mHeatMap != null && requireShow) {
            requireShow = false;
            mHeatMap.showLive();
            Toast.makeText(MainActivity.this, "开始绘制热力图", Toast.LENGTH_SHORT).show();
        }
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHeatMap != null && mHeatMap.isShowing()) {
            requireShow = true;
            mHeatMap.cancel();
            Toast.makeText(MainActivity.this, "暂停热力图", Toast.LENGTH_SHORT).show();
        }
        mMapView.onPause();
    }

    /**
     * 地图点击事件 + 移动Camera中心
     *
     * @param latLng 经纬度
     */
    @Override
    public void onMapClick(LatLng latLng) {
        moveCamera(latLng, true, 0);
    }

    /**
     * （测试用）监听画面区域变化
     *
     * @param cameraPosition
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        VisibleRegion region = mAMap.getProjection().getVisibleRegion();
        t1.setText(region.farLeft.latitude + " " + region.farLeft.longitude);
        t2.setText(region.nearRight.latitude + " " + region.nearRight.longitude);
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }

    /**
     * 申请读写权限，（以保存Crash信息）
     */
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onPOIClick(final Poi poi) {
        moveCamera(poi.getCoordinate(), true, 0);
        new DetailFragment()
                .poi(poi)
                .listener(new DetailFragment.OnBottomItemClickListener() {
                    @Override
                    public void onChart() {
                        //调出图表



                    }

                    @Override
                    public void onRoute() {
                        //最佳路径
                        EventBus.getDefault().postSticky(poi);
                        startActivity(new Intent(MainActivity.this, RouteActivity.class));
                    }
                })
                .show(getSupportFragmentManager(), "POI");
    }
}
