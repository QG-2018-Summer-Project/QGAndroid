package com.mobile.qg.qgtaxi.route;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.LatLonPoint;
import com.mobile.qg.qgtaxi.R;
import com.mobile.qg.qgtaxi.route.entity.FromToGroup;
import com.mobile.qg.qgtaxi.util.AMapUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RouteActivity extends AppCompatActivity {

    @BindView(R.id.map)
    protected MapView mMapView;

    @BindView(R.id.tv_route_start)
    protected TextView mStartTv;

    @BindView(R.id.tv_route_end)
    protected TextView mEndTv;

    private AMap aMap;
    private LatLonPoint mStartPoint = new LatLonPoint(39.993266, 116.473193);//首开广场
    private LatLonPoint mEndPoint = new LatLonPoint(39.917337, 116.397056);//故宫博物院
    private static final LatLonPoint GUANGZHOU = new LatLonPoint(23.035219, 113.398205);

    private BestRoute mBestRoute;
    private FromToGroup mFromToGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);


        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();

        mBestRoute = BestRoute.builder()
                .aMap(aMap)
                .context(this)
                .build();

        mFromToGroup = new FromToGroup().start(GUANGZHOU, "我的位置");

    }

    @Subscribe(sticky = true)
    public void onPoi(Poi poi) {
        mEndTv.setText(poi.getName());

        mFromToGroup.end(AMapUtil.convertToLatLonPoint(poi.getCoordinate()), poi.getName());

        EventBus.getDefault().removeStickyEvent(poi);
    }

    @OnClick({R.id.iv_route_reverse})
    public void reverse() {
        mFromToGroup.reverse();
    }

    @OnClick(R.id.btn_route_start)
    public void startRoute() {

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


}
