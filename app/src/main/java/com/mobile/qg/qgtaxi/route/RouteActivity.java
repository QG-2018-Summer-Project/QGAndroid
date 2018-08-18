package com.mobile.qg.qgtaxi.route;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Tip;
import com.mobile.qg.qgtaxi.R;
import com.mobile.qg.qgtaxi.route.entity.FromToGroup;
import com.mobile.qg.qgtaxi.search.InputTipsActivity;
import com.mobile.qg.qgtaxi.search.SearchEvent;
import com.mobile.qg.qgtaxi.util.AMapUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mobile.qg.qgtaxi.KeyValueConstant.KEY_ROUTE;
import static com.mobile.qg.qgtaxi.KeyValueConstant.VALUE_ROUTE_END;
import static com.mobile.qg.qgtaxi.KeyValueConstant.VALUE_ROUTE_START;

public class RouteActivity extends AppCompatActivity {

    @BindView(R.id.map)
    protected MapView mMapView;

    @BindView(R.id.tv_route_start)
    protected TextView mStartTv;

    @BindView(R.id.tv_route_end)
    protected TextView mEndTv;

    @BindView(R.id.pb_route)
    protected ProgressBar mRoutePb;

    @BindView(R.id.cd_route_head)
    protected CardView mHeadCd;

    @BindView(R.id.btn_route_start)
    protected Button mStartBtn;

    @BindView(R.id.rl_route_bottom)
    protected RelativeLayout mBottomRl;

    @BindView(R.id.tv_route_distance)
    protected TextView mDistanceTv;

    @BindView(R.id.tv_route_time)
    protected TextView mTimeTv;

    private AMap aMap;
    private static final LatLonPoint GUANGZHOU = new LatLonPoint(23.035219, 113.398205);

    private BestRoute mBestRoute;
    private FromToGroup mFromToGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        ButterKnife.bind(this);

        mFromToGroup = new FromToGroup().start(GUANGZHOU, "我的位置");
        EventBus.getDefault().register(this);

        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();

        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(AMapUtil.convertToLatLng(GUANGZHOU), 17, 0, 0)));
        aMap.getUiSettings().setZoomControlsEnabled(false);

        mBestRoute = BestRoute.builder()
                .aMap(aMap)
                .context(this)
                .progressBar(mRoutePb)
                .distanceTv(mDistanceTv)
                .durationTv(mTimeTv)
                .build()
                .willHide(mHeadCd, mStartBtn)
                .willShow(mBottomRl);

    }

    private void refreshTextView() {
        String start = mFromToGroup.getStartName();
        if (start != null && !start.equals("")) {
            mStartTv.setText(start);
        }

        String end = mFromToGroup.getEndName();
        if (end != null && !end.equals("")) {
            mEndTv.setText(end);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onPoi(Poi poi) {
        mFromToGroup.end(AMapUtil.convertToLatLonPoint(poi.getCoordinate()), poi.getName());
        refreshTextView();
        EventBus.getDefault().removeStickyEvent(poi);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearch(SearchEvent event) {

        if (event.getPurpose() == SearchEvent.Purpose.START) {
            mFromToGroup.start(event.getPoint(), event.getName());
        } else if (event.getPurpose() == SearchEvent.Purpose.END) {
            mFromToGroup.end(event.getPoint(), event.getName());
        }
        refreshTextView();

    }


    @OnClick(R.id.tv_route_start)
    public void getStart() {
        Intent intent = new Intent(this, InputTipsActivity.class);
        intent.putExtra(KEY_ROUTE, VALUE_ROUTE_START);
        startActivity(intent);
    }

    @OnClick(R.id.tv_route_end)
    public void getEnd() {
        Intent intent = new Intent(this, InputTipsActivity.class);
        intent.putExtra(KEY_ROUTE, VALUE_ROUTE_END);
        startActivity(intent);
    }

    @OnClick({R.id.iv_route_reverse})
    public void reverse() {
        if (mFromToGroup.reverse()) {
            refreshTextView();
        }

    }

    @OnClick(R.id.btn_route_end)
    public void cancelRoute() {
        mBestRoute.cancel();
        aMap.animateCamera(CameraUpdateFactory.newLatLng(AMapUtil.convertToLatLng(mFromToGroup.getStartPoint())));

    }

    @OnClick(R.id.btn_route_start)
    public void startRoute() {
        if (mFromToGroup.available()) {
            mBestRoute.search(mFromToGroup);
            Toast.makeText(this, "开始路径规划", Toast.LENGTH_SHORT).show();
        } else {
            if (mFromToGroup.getStartPoint() == null) {
                Toast.makeText(this, "请输入起点", Toast.LENGTH_SHORT).show();
            } else if (mFromToGroup.getEndPoint() == null) {
                Toast.makeText(this, "请输入终点", Toast.LENGTH_SHORT).show();
            }
        }
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
        mMapView.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mBestRoute != null && mBestRoute.isShowing()) {
            mBestRoute.cancel();
        } else {
            super.onBackPressed();
        }
    }
}
