package com.mobile.qg.qgtaxi.entity

/**
 * Created by 11234 on 2018/8/12.
 * 当前时间 某一区域/点的标准上传类
 */
data class CurrentLatLng(

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
         * 字符串型时间
         * yyyy-MM-dd hh:mm:ss
         */

        var currentTime: String

)