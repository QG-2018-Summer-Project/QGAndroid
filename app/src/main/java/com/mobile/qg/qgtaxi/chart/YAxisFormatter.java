package com.mobile.qg.qgtaxi.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by 93922 on 2018/8/16.
 * 描述：y轴的坐标的格式，百分比类型
 */

public class YAxisFormatter implements IAxisValueFormatter {

    private DecimalFormat format;

    public YAxisFormatter() {
        format = new DecimalFormat("0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return format.format(value) + "%";
    }
}
