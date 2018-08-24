package com.mobile.qg.qgtaxi.chart.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mobile.qg.qgtaxi.R;
import com.mobile.qg.qgtaxi.chart.ChartActivity;
import com.mobile.qg.qgtaxi.chart.Utilization;
import com.mobile.qg.qgtaxi.chart.XAxisFormatter;
import com.mobile.qg.qgtaxi.share.ShareUtil;
import com.mobile.qg.qgtaxi.share.WeChatConstant;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by 93922 on 2018/8/17.
 * 描述：柱状图，出租车利用率
 */
public class UtilizationFragment extends BaseChartFragment {

    private static final String TAG = "BarChartFragment";
    private  ArrayList<Float> yData;
    private Unbinder unbinder;
    private IWXAPI api;

    @BindView(R.id.srl_bc)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.bc)
    BarChart mBarChart;

    @OnClick(R.id.btn_share_utilizepercent)
    public void share() {
        ShareUtil.shareByView(mBarChart, api);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barchart, container, false);
        unbinder = ButterKnife.bind(this, view);
        api = WXAPIFactory.createWXAPI(getActivity(), WeChatConstant.APP_ID, true);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(this);

        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    protected void readyRequest() {
        swipeRefresh.setRefreshing(true);
        ((ChartActivity) getActivity()).requestUtilizationData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUtilization(Utilization utilization) {
        swipeRefresh.setRefreshing(false);
        List<Float> floats = utilization.getPercents();
        if (floats.size()==0){
            return;
        }
        final ArrayList<Float> yData = (ArrayList<Float>) floats;
        Log.e(TAG, "onResponse: " + yData);
        initChart();
        setBarData(createBarEntryData(yData));
    }

    private void initChart() {
        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawValueAboveBar(true);
        //不要缩放
        mBarChart.setDragEnabled(false);
        mBarChart.setScaleEnabled(false);
        mBarChart.setBackgroundColor(Color.WHITE);
        mBarChart.getDescription().setEnabled(true);
        mBarChart.getDescription().setText("%");
        mBarChart.getDescription().setTextSize(12.0f);
        mBarChart.getDescription().setPosition(60, 50);
        mBarChart.getDescription().setTextColor(Color.BLACK);
        mBarChart.setPinchZoom(false);

        mBarChart.setDrawGridBackground(false);
        mBarChart.setNoDataText("获取的数据为空！");
        mBarChart.setHighlightFullBarEnabled(false);
        mBarChart.animateY(2000);

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        XAxisFormatter formatter = new XAxisFormatter();
        xAxis.setValueFormatter(formatter);
        xAxis.setDrawAxisLine(true);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6);

        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.setAxisMaximum(100);
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend legend = mBarChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(4f);
    }

    @Override
    protected void setData(ArrayList<Entry> data) {
    }

    @Override
    protected void endRefresh() {
        swipeRefresh.setRefreshing(false);
    }

    private void setBarData(ArrayList<BarEntry> data) {

        BarDataSet set;
        if (mBarChart.getData() != null &&
                mBarChart.getData().getDataSetCount() > 0) {
            set = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            set.setValues(data);
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(data, "出租车利用率");
            //设置有四种颜色
            set.setColors(ColorTemplate.getHoloBlue());
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set);
            BarData barData = new BarData(dataSets);
            barData.setValueTextSize(10f);
            barData.setBarWidth(0.9f);
            //设置数据
            mBarChart.setData(barData);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh() {
        ((ChartActivity) getActivity()).requestUtilizationData();
    }

}
