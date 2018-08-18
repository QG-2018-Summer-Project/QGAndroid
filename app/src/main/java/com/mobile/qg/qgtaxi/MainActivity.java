package com.mobile.qg.qgtaxi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
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
import com.mobile.qg.qgtaxi.chart.ChartActivity;
import com.mobile.qg.qgtaxi.chart.ChartApi;
import com.mobile.qg.qgtaxi.detail.DetailFragment;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;
import com.mobile.qg.qgtaxi.heatmap.HeatMap;
import com.mobile.qg.qgtaxi.heatmap.HeatMapApi;
import com.mobile.qg.qgtaxi.heatmap.PollingEvent;
import com.mobile.qg.qgtaxi.route.RouteActivity;
import com.mobile.qg.qgtaxi.search.InputTipsActivity;
import com.mobile.qg.qgtaxi.setting.*;
import com.mobile.qg.qgtaxi.time.CalendarFragment;
import com.mobile.qg.qgtaxi.time.TimePicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class MainActivity extends AppCompatActivity implements
        AMap.OnMapClickListener,
        AMap.OnPOIClickListener {

    private static final String TAG = "MainActivity";

    public static final int CAMERA_ZOOM_LARGE = 15;
    public static final int CAMERA_ZOOM_NORMAL = 11;
    private static final LatLng GUANGZHOU = new LatLng(23.035219, 113.398205);
    //绿色标记
    private static final BitmapDescriptor GREEN_MARKER = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

    @BindView(R.id.map)
    protected MapView mMapView;

    private AMap mAMap;
    private HeatMap mHeatMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        mMapView.onCreate(savedInstanceState);

        mAMap = mMapView.getMap();
        mAMap.getUiSettings().setScaleControlsEnabled(true);
        mAMap.setOnMapClickListener(this);
        mAMap.setOnPOIClickListener(this);

        moveCamera(GUANGZHOU, false, CAMERA_ZOOM_NORMAL);

        requestPermission();

        setApi(new SettingEvent(PreferenceManager.getDefaultSharedPreferences(this)));

        mHeatMap = HeatMap.builder()
                .aMap(mAMap)
                .isHeatMapping(true)
                .isStop(false)
                .period(new PollingEvent(PreferenceManager.getDefaultSharedPreferences(this)).getPeriod())
                .build();

    }

    /**
     * 搜索
     */
    @OnClick(R.id.search)
    public void onSearch() {
        startActivity(new Intent(this, InputTipsActivity.class));
    }

    /**
     * 设置
     */
    @OnClick(R.id.iv_setting)
    public void toSet() {
        startActivity(new Intent(this, SettingActivity.class));
    }

    /**
     * 路径推荐
     */
    @OnClick(R.id.iv_route)
    public void toRoute() {
        startActivity(new Intent(this, RouteActivity.class));
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

    /**
     * 定位
     */
    @OnClick(R.id.iv_aim)
    public void aim() {
        moveCamera(GUANGZHOU, true, 0);
    }

    /**
     * 设置后返回事件（ip/连接超时）
     *
     * @param event ip/超时事件
     */
    @Subscribe()
    public void setApi(SettingEvent event) {
        Log.e(TAG, "setApi: " + event.getIp());
        HeatMapApi.getInstance()
                .edit()
                .connectTimeOut(event.getTimeOut())
                .ip(event.getIp())
                .accept();
        ChartApi.getInstance()
                .edit()
                .connectTimeOut(event.getTimeOut())
                .ip(event.getIp())
                .accept();

    }

    /**
     * 设置后返回事件（热力图轮询间隔）
     *
     * @param event 轮询事件
     */
    @Subscribe()
    public void setHeatMapPeriod(PollingEvent event) {
        Log.e(TAG, "setHeatMapPeriod: " + event.getPeriod());
        mHeatMap.setPeriod(event.getPeriod());
    }

    /**
     * 搜索地点 + 移动Camera中心 + 地图缩进
     *
     * @param tip 输入提示
     */
    @Subscribe()
    public void getTips(Tip tip) {
        LatLonPoint point = tip.getPoint();
        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
        moveCamera(latLng, true, CAMERA_ZOOM_LARGE);
    }

    /**
     * 移动屏幕到某一经纬度
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
     * 地图点击事件 + 移动Camera中心
     *
     * @param latLng 经纬度
     */
    @Override
    public void onMapClick(LatLng latLng) {
        moveCamera(latLng, true, 0);
    }

    /**
     * 地图兴趣点点击事件 + 移动Camera中心 + 跳出底部Fragment
     *
     * @param poi 兴趣点
     */
    @Override
    public void onPOIClick(final Poi poi) {
        moveCamera(poi.getCoordinate(), true, 0);
        new DetailFragment()
                .poi(poi)
                .listener(new DetailFragment.OnBottomItemClickListener() {
                    @Override
                    public void onChart() {
                        //调出图表

                        VisibleRegion region = mAMap.getProjection().getVisibleRegion();
                        CurrentLatLng latLng = new CurrentLatLng(
                                region.farLeft.latitude, region.farLeft.longitude,
                                region.nearRight.latitude, region.nearRight.longitude, "2017-02-07 16:00:00");

                        Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                        intent.putExtra(KeyValueConstant.KEY_CHART_ACTIVITY, latLng);
                        startActivity(intent);


                    }

                    @Override
                    public void onRoute() {
                        //最佳路径
                        EventBus.getDefault().postSticky(poi);
                        startActivity(new Intent(MainActivity.this, RouteActivity.class));
                    }

                    @Override
                    public void onPrevious() {
                        new CalendarFragment().listener(new TimePicker.OnTimePackListener() {
                            @Override
                            public void onCancel() {
                                ((CalendarFragment) MainActivity.this.getSupportFragmentManager().findFragmentByTag("TAG")).dismiss();
                            }

                            @Override
                            public void onCommit(String s) {
                                mHeatMap.showPeriod(s);
                                ((CalendarFragment) MainActivity.this.getSupportFragmentManager().findFragmentByTag("TAG")).dismiss();
                            }
                        }).show(MainActivity.this.getSupportFragmentManager(), "TAG");
                    }

                    @Override
                    public void onPredictCount() {
                        mHeatMap.predictCount();
                        ((DetailFragment) getSupportFragmentManager().findFragmentByTag("POI")).dismiss();
                    }

                    @Override
                    public void onPredictDemand() {
                        mHeatMap.predictDemand();
                        ((DetailFragment) getSupportFragmentManager().findFragmentByTag("POI")).dismiss();
                    }
                })
                .show(getSupportFragmentManager(), "POI");
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
     * 申请读写权限，（以保存Crash信息）
     */
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

}
