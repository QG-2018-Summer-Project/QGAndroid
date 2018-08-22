package com.mobile.qg.qgtaxi.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by 93922 on 2018/8/16.
 * 描述：x轴坐标格式,显示时间，单位：时
 */

public class XAxisFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        /*因为最早的数据是三小时前的*/
        /*考虑到前一天的时间不应该是负数，这里判断是否+24*/
        int numHour = (numHour = (int) value - 3) < 0 ? numHour + 24 : numHour;
        return numHour + "时";
    }

}
