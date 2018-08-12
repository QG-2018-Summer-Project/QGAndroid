package com.mobile.qg.qgtaxi.heatmap

import com.google.gson.annotations.SerializedName

/**
 * Created by 11234 on 2018/8/12.
 * 热力图中点的标准返回类
 */
data class HeatMapLatLng(

        /**
         * 经度
         */
        @SerializedName(value = "lon", alternate = ["lng"])
        var lon: Double,

        /**
         * 纬度
         */
        var lat: Double,

        /**
         * 权值
         */
        var weight: Int

)