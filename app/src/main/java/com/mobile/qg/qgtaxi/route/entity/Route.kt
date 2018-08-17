package com.mobile.qg.qgtaxi.route.entity

/**
 * Created by 11234 on 2018/8/17.
 */
data class Route(

        var index: Int,
        var steps: List<Step>,
        var allTime: String,
        var distance: String

)