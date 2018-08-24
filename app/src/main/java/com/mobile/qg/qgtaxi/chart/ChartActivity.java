package com.mobile.qg.qgtaxi.chart;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.R;
import com.mobile.qg.qgtaxi.chart.fragment.ChangeFragment;
import com.mobile.qg.qgtaxi.chart.fragment.ChartFragmentAdapter;
import com.mobile.qg.qgtaxi.chart.fragment.CrowdFragment;
import com.mobile.qg.qgtaxi.chart.fragment.UtilizationFragment;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.mobile.qg.qgtaxi.constant.IntentConstant.KEY_CHART;

/**
 * Created by 93922 on 2018/8/17.
 * 描述：
 */

public class ChartActivity extends AppCompatActivity {

    private static final String TAG = "ChartActivity";

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.vp_chart)
    ViewPager mVpChart;

    private CurrentLatLng mLatLng;
    private String hour;

    private static final String[] titles = new String[]{"流量变化率", "出租车利用率", "拥堵率"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);

        mLatLng = (CurrentLatLng) getIntent().getSerializableExtra(KEY_CHART);
        Log.e(TAG, "onCreate: " + mLatLng);
        hour = mLatLng.getCurrentTime().substring(11, 13);//截取传进来的时间的小时部分
        initView();

    }

    private void initView() {
        for (int i = 0; i < 3; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(titles[i]));
        }
        mTabLayout.setupWithViewPager(mVpChart);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ChangeFragment());
        fragments.add(new UtilizationFragment());
        fragments.add(new CrowdFragment());
        ChartFragmentAdapter adapter = new ChartFragmentAdapter(getSupportFragmentManager(),
                fragments, Arrays.asList(titles));

        mVpChart.setAdapter(adapter);
    }

    public CurrentLatLng getLatLng() {
        return mLatLng;
    }

    /**
     * 拥挤
     */
    public void requestCrowdData() {
        ChartApi.getInstance().requestCrowdPercent(mLatLng, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
                EventBus.getDefault().post(new Crowd("", new ArrayList<Float>()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response != null && response.isSuccessful()) {

                    String jsonData = response.body().string();
                    Log.e(TAG, "onResponse: jsonData:" + jsonData);
                    Crowd crowd = new Gson().fromJson(jsonData, Crowd.class);
                    Log.e(TAG, "onResponse: chartData:" + crowd);

                    if (crowd.getStatus().equals("2000")) {
                        EventBus.getDefault().post(crowd);
                    } else {
                        Log.e(TAG, "onResponse: status!=2000");
                        EventBus.getDefault().post(new Crowd("", new ArrayList<Float>()));
                    }
                } else {
                    EventBus.getDefault().post(new Crowd("", new ArrayList<Float>()));

                }

            }
        });
    }

    /**
     * 利用率
     */
    public void requestUtilizationData() {
        ChartApi.getInstance().requestUtilizePercent(mLatLng, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: " + e);
                EventBus.getDefault().post(new Utilization("", new ArrayList<Float>()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {

                    String jsonData = response.body().string();
                    Log.e(TAG, "onResponse: jsonData:" + jsonData);
                    Utilization utilization = new Gson().fromJson(jsonData, Utilization.class);
                    Log.e(TAG, "onResponse: chartData:" + utilization);

                    if (utilization.getStatus().equals("2000")) {
                        EventBus.getDefault().post(utilization);
                    } else {
                        Log.e(TAG, "onResponse: status!=2000");
                        EventBus.getDefault().post(new Utilization("", new ArrayList<Float>()));
                    }
                } else {
                    EventBus.getDefault().post(new Utilization("", new ArrayList<Float>()));
                }
            }
        });
    }

    /**
     * 拥挤
     */
    public void requestChangeData() {
        ChartApi.getInstance().requestChangePercent(mLatLng, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "AAAonFailure: " + e);
                EventBus.getDefault().post(new Change("", new ArrayList<Float>()));

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response != null && response.isSuccessful()) {
                    String jsonData = response.body().string();
                    Log.e(TAG, "onResponse: jsonData:" + jsonData);
                    Change change = new Gson().fromJson(jsonData, Change.class);
                    Log.e(TAG, "onResponse: chartData:" + change);


                    if (change.getStatus().equals("2000")) {
                        EventBus.getDefault().post(change);
                    } else {
                        Log.e(TAG, "onResponse: status!=2000");
                        EventBus.getDefault().post(new Change("", new ArrayList<Float>()));
                    }

                } else {
                    EventBus.getDefault().post(new Change("", new ArrayList<Float>()));
                }

            }
        });
    }


}
