package com.mobile.qg.qgtaxi.chart.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mobile.qg.qgtaxi.R;
import com.mobile.qg.qgtaxi.chart.Change;
import com.mobile.qg.qgtaxi.chart.ChartActivity;
import com.mobile.qg.qgtaxi.chart.XAxisFormatter;
import com.mobile.qg.qgtaxi.chart.YAxisFormatter;
import com.mobile.qg.qgtaxi.share.ShareUtil;
import com.mobile.qg.qgtaxi.share.WeChatConstant;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by 93922 on 2018/8/16.
 * 描述：线图，展示地区流量变化率
 */

public class ChangeFragment extends BaseChartFragment {

    private static final String TAG = "ChartFragment";

    private IWXAPI api;
    private Unbinder unbinder;

    @BindView(R.id.srl_lc)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.lc)
    LineChart mLineChart;

    @OnClick(R.id.btn_share_changepercent)
    public void share() {
        ShareUtil.shareByView(mLineChart, api);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linechart, container, false);
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
        ((ChartActivity) getActivity()).requestChangeData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnChange(Change change) {

        swipeRefresh.setRefreshing(false);
        List<Float> floats = change.getPercents();
        if (floats.size() == 0) {
            return;
        }

        final ArrayList<Float> yData = (ArrayList<Float>) floats;
        Log.e(TAG, "onResponse: " + yData);

        initChart(yData);
        createEntryData(yData);
        setData(createEntryData(yData));
    }

    private void initChart(ArrayList<Float> yData) {
        // 取消右下角的默认描述,
        mLineChart.getDescription().setEnabled(false);
        // 不支持点击,支持的话点击之后是一个十字线锁定,后期有时间加上MarkView再回来支持点击
        mLineChart.setTouchEnabled(false);
        mLineChart.setDragDecelerationFrictionCoef(0.9f);
        // 不要支持缩放和拖动
        mLineChart.setDragEnabled(false);
        mLineChart.setScaleEnabled(false);
        //背景不要网格
        mLineChart.setDrawGridBackground(false);
        mLineChart.setHighlightPerDragEnabled(true);
        mLineChart.setPinchZoom(true);
        // 设置背景颜色(白色)
        mLineChart.setBackgroundColor(Color.WHITE);

        //x动画时间
        mLineChart.animateX(1200);
        mLineChart.setNoDataText("获取的数据为空！");
        mLineChart.setHighlightPerDragEnabled(false);

        //标签
        Legend legend = mLineChart.getLegend();

        //设置左下的标签说明
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);
        legend.setTextColor(0xff43484b);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        //x轴
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.BLACK);
        XAxisFormatter formatter = new XAxisFormatter();
        xAxis.setValueFormatter(formatter);
        xAxis.setDrawGridLines(false);
        //x轴数值差
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6);

        //左边y轴
        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setValueFormatter(new YAxisFormatter());
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        /*动态设置y轴范围*/
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(Collections.max(yData) + 20);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(false);
        //不要右边的Y轴
        mLineChart.getAxisRight().setEnabled(false);

    }

    @Override
    protected void setData(ArrayList<Entry> data) {
        LineDataSet set;

        if (mLineChart.getData() != null && mLineChart.getData().getDataSetCount() > 0) {
            set = (LineDataSet) mLineChart.getData().getDataSets();
            set.setValues(data);
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {

            set = new LineDataSet(data, "出租车利用率");
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.isDrawValuesEnabled();
            set.setColor(ColorTemplate.getHoloBlue());
            set.setCircleColor(Color.BLUE);
            set.setLineWidth(2f);
            set.setCircleRadius(3f);
            set.setFillAlpha(65);
            set.setFillColor(ColorTemplate.getHoloBlue());
            set.setDrawCircleHole(false);

            LineData lineData = new LineData(set);
            lineData.setValueTextColor(Color.BLACK);
            lineData.setValueTextSize(9f);

            mLineChart.setData(lineData);
        }
    }

    @Override
    protected void endRefresh() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh() {
        ((ChartActivity) getActivity()).requestChangeData();
    }

}
