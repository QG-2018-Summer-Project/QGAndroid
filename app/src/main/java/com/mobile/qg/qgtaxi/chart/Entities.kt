package com.mobile.qg.qgtaxi.chart

/**
 * Created by 93922 on 2018/8/22.
 * 描述：
 */
data class Change(var status: String, var percents: List<Float>)

data class Utilization(var status: String, var percents: List<Float>)

data class Crowd(var status: String, var percents: List<Float>)
