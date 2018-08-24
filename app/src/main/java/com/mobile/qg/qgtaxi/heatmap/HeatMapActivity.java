package com.mobile.qg.qgtaxi.heatmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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
import com.mobile.qg.qgtaxi.R;
import com.mobile.qg.qgtaxi.chart.ChartActivity;
import com.mobile.qg.qgtaxi.detail.DetailFragment;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;
import com.mobile.qg.qgtaxi.entity.LatLngFactory;
import com.mobile.qg.qgtaxi.route.RouteActivity;
import com.mobile.qg.qgtaxi.search.InputTipsActivity;
import com.mobile.qg.qgtaxi.setting.SettingActivity;
import com.mobile.qg.qgtaxi.share.ShareUtil;
import com.mobile.qg.qgtaxi.share.WeChatConstant;
import com.mobile.qg.qgtaxi.situation.SituationActivity;
import com.mobile.qg.qgtaxi.time.CalendarFragment;
import com.mobile.qg.qgtaxi.time.TimePicker;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mobile.qg.qgtaxi.constant.IntentConstant.KEY_CHART;
import static com.mobile.qg.qgtaxi.constant.IntentConstant.KEY_SITUATION;

public final class HeatMapActivity extends AppCompatActivity implements
        AMap.OnMapClickListener,
        AMap.OnPOIClickListener {

    public static final int CAMERA_ZOOM_LARGE = 16;
    public static final int CAMERA_ZOOM_NORMAL = 12;
    private static final LatLng GUANGZHOU = new LatLng(23.035219, 113.398205);
    //绿色标记
    private static final BitmapDescriptor GREEN_MARKER = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

    @BindView(R.id.map)
    protected MapView mMapView;

    @BindView(R.id.pb_heatmap)
    protected ProgressBar mProgressBar;

    private AMap mAMap;
    private HeatMap mHeatMap;

    private IWXAPI mWxApi = WXAPIFactory.createWXAPI(this, WeChatConstant.APP_ID, true);

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

        mHeatMap = HeatMap.builder()
                .aMap(mAMap)
                .context(this)
                .progressBar(mProgressBar)
                .build();

        mWxApi.registerApp(WeChatConstant.APP_ID);

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

        if (mHeatMap.isShowing()) {
            mHeatMap.cancel();
            Toast.makeText(HeatMapActivity.this, "暂停热力图", Toast.LENGTH_SHORT).show();
        } else {
            mHeatMap.showLive();
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
     * 预测车流量
     */
    @OnClick(R.id.iv_count)
    public void predictCount() {
        mHeatMap.predictCount();
    }

    /**
     * 预测需求量
     */
    @OnClick(R.id.iv_demand)
    public void predictDemand() {
        mHeatMap.predictDemand();
    }

    /**
     * 查看过去的热力图
     */
    @OnClick(R.id.iv_period)
    public void showPeriod() {
        new CalendarFragment().listener(new TimePicker.OnTimePackListener() {
            @Override
            public void onCancel() {
                ((CalendarFragment) HeatMapActivity.this.getSupportFragmentManager().findFragmentByTag("TAG")).dismiss();
            }

            @Override
            public void onCommit(String time) {
                mHeatMap.showPeriod(time);
                ((CalendarFragment) HeatMapActivity.this.getSupportFragmentManager().findFragmentByTag("TAG")).dismiss();
            }
        }).show(HeatMapActivity.this.getSupportFragmentManager(), "TAG");
    }

    /**
     * 热力图截屏
     */
    @OnClick(R.id.iv_shot)
    public void shot() {
        mAMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {
                ShareUtil.shareByBitmap(bitmap, mWxApi);
            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int i) {
            }
        });
    }

    /**
     * 异常情况
     */
    @OnClick(R.id.iv_situation)
    public void situation() {
        VisibleRegion region = mAMap.getProjection().getVisibleRegion();
        CurrentLatLng latLng = LatLngFactory.INSTANCE.getCurrent(region.farLeft, region.nearRight);

        Intent intent = new Intent(HeatMapActivity.this, SituationActivity.class);
        intent.putExtra(KEY_SITUATION, latLng);
        startActivity(intent);
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

    private static final String TAG = "HeatMapActivity";

    /**
     * 地图兴趣点点击事件 + 移动Camera中心 + 跳出底部Fragment
     *
     * @param poi 兴趣点
     */
    @Override
    public void onPOIClick(final Poi poi) {
        Log.e(TAG, "onPOIClick: ");
        moveCamera(poi.getCoordinate(), true, 0);
        new DetailFragment()
                .poi(poi)
                .listener(new DetailFragment.OnBottomItemClickListener() {
                    @Override
                    public void onChart() {
                        //调出图表
                        VisibleRegion region = mAMap.getProjection().getVisibleRegion();
                        CurrentLatLng latLng = LatLngFactory.INSTANCE.getCurrent(region.farLeft, region.nearRight);

                        Intent intent = new Intent(HeatMapActivity.this, ChartActivity.class);
                        intent.putExtra(KEY_CHART, latLng);
                        startActivity(intent);

                    }

                    @Override
                    public void onRoute() {
                        //最佳路径
                        EventBus.getDefault().postSticky(poi);
                        startActivity(new Intent(HeatMapActivity.this, RouteActivity.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHeatMap.cancel();
        mMapView.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 申请读写权限，（以保存Crash信息）
     */
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }

}