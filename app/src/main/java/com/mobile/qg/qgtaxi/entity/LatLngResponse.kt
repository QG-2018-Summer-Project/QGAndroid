package com.mobile.qg.qgtaxi.entity

/**
 * Created by 11234 on 2018/8/12.
 * 热力图中 点的标准返回类
 */
data class LatLngResponse(

        /**
         * 经度
         */
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