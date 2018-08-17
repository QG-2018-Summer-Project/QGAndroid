package com.mobile.qg.qgtaxi.entity

/**
 * Created by 11234 on 2018/8/16.
 * 预测
 */
data class PredictedLatLng(

        /**
         * 左上角经度
         */
        var leftTopLon: Double,

        /**
         * 左上角纬度
         */
        var leftTopLat: Double,

        /**
         * 右下角纬度
         */

        var rightBottomLon: Double,

        /**
         * 右下角角纬度
         */

        var rightBottomLat: Double,

        /**
         * 预测时间
         * yyyy-MM-dd hh:mm:ss
         */

        var predictedTime: String


)
