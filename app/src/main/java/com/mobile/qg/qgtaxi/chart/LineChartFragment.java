package com.mobile.qg.qgtaxi.chart;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.mobile.qg.qgtaxi.share.ShareUtil;
import com.mobile.qg.qgtaxi.share.WeChatConstant;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by 93922 on 2018/8/16.
 * 描述：线图，展示地区流量变化率
 */


@SuppressLint("ValidFragment")
public class LineChartFragment extends Fragment {

    private static final String TAG = "ChartFragment";
    private ArrayList<Entry> data;/*点数据*/
    private ArrayList<Float> yData;/*y轴数据*/
    private String           mLocation;/*位置，本来想加在图表下方的标签的，但是后来考虑到四个坐标点写出来太长，就暂时就取消了*/
    private String           mTime;/*时间，单位为时，也取消了*/
    private IWXAPI           api;

    private Unbinder unbinder;
    @BindView(R.id.lc)
    LineChart mLineChart;

    @OnClick(R.id.btn_share_changepercent)
    public void share() {
        ShareUtil.shareByView(mLineChart,api);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linechart, container, false);
        unbinder = ButterKnife.bind(this, view);
        api= WXAPIFactory.createWXAPI(getActivity(), WeChatConstant.APP_ID,true);
        initChart();
        createEntryData(yData, mTime);
        setData(data);
        return view;
    }

    @SuppressLint("ValidFragment")
    public LineChartFragment(ArrayList<Float> yData,String time) {
        this.yData = yData;
        mTime=time;
        Log.e(TAG, "LineChartFragment: " + yData);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initChart() {
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
        Float min, max;
        leftAxis.setAxisMinimum((min = Collections.min(yData)) >= 10 ? min - 10 : 0);
        leftAxis.setAxisMaximum((max = Collections.max(yData)) < 100 ? max + 10 : 100);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(false);
        //不要右边的Y轴
        mLineChart.getAxisRight().setEnabled(false);

    }


    private void setData(ArrayList<Entry> data) {

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

    private void createEntryData(ArrayList<Float> yData, String time) {
        int numTime = 1;
        data = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            data.add(new Entry(numTime + i, yData.get(i)));
        }
    }


}
