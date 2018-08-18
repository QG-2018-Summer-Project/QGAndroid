package com.mobile.qg.qgtaxi.chart;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobile.qg.qgtaxi.KeyValueConstant;
import com.mobile.qg.qgtaxi.R;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
    private ArrayList changeData = new ArrayList();
    private ArrayList utilizeData = new ArrayList();
    private String hour;

    private static final String[] titles = new String[]{"流量变化率", "出租车利用率", "拥堵率"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);
        mLatLng = (CurrentLatLng) getIntent().getSerializableExtra(KeyValueConstant.KEY_CHART_ACTIVITY);
        hour = mLatLng.getCurrentTime().substring(11, 13);//截取传进来的时间的小时部分
        initData();
        initView();

    }

    private void initView() {

        for (int i = 0; i < 3; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(titles[i]));
        }
        mTabLayout.setupWithViewPager(mVpChart);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new LineChartFragment(changeData, hour));
        fragments.add(new BarChartFragment(utilizeData, hour));
        fragments.add(FirstFragment.newInstance());
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(),
                fragments, Arrays.asList(titles));
        mVpChart.setAdapter(adapter);
    }

    private void initData() {

        /*请求变化率*/
        ChartApi.getInstance().requestChangePercent(mLatLng, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: " + e);
                Toast.makeText(ChartActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse:response: " + response);
                String jsonData = response.body().string();
                Log.e(TAG, "onResponse: jsonData:" + jsonData);
                ChartData chartData = new Gson().fromJson(jsonData, ChartData.class);
                Log.e(TAG, "onResponse: chartData:" + chartData);
                if (chartData.getStatus() == 500) {
                    utilizeData = (ArrayList) Arrays.asList(chartData.getData());
                } else {

                }

            }
        });

        /*请求利用率*/
        ChartApi.getInstance().requestUtilizePercent(mLatLng, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: " + e);
                Toast.makeText(ChartActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse:response: " + response);
                String jsonData = response.body().string();
                Log.e(TAG, "onResponse: jsonData:" + jsonData);
                ChartData chartData = new Gson().fromJson(jsonData, ChartData.class);
                Log.e(TAG, "onResponse: chartData:" + chartData);
                if (chartData.getStatus() == 500) {
                    changeData = (ArrayList) Arrays.asList(chartData.getData());
                } else {

                }
            }
        });
    }



    /* 本地测试用的数据*/
  /* private void initData() {
        changeData = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            float val = (float) (Math.random() * 100);
            changeData.add(val);
        }
    }*/
}
