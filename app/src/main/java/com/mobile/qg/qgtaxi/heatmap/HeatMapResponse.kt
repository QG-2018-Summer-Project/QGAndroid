package com.mobile.qg.qgtaxi.heatmap

/**
 * Created by 11234 on 2018/8/12.
 * 热力图的标准返回类
 */

data class HeatMapResponse(

        /**
         * 热力图中点的集合
         */
        var pointSet: List<HeatMapLatLng>

)
