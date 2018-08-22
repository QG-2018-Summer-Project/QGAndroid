package com.mobile.qg.qgtaxi.entity

/**
 * Created by 11234 on 2018/8/12.
 * 指定时间区间内 某一区域/点的标准上传类
 */
data class PeriodLatLng(

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
         * 开始时间
         * yyyy-MM-dd hh:mm:ss
         */

        var startTime: String,

        /**
         * 结束时间
         * yyyy-MM-dd hh:mm:ss
         */

        var endTime: String

)