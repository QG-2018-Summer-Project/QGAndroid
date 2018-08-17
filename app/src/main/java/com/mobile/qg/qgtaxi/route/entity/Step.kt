package com.mobile.qg.qgtaxi.route.entity

/**
 * Created by 11234 on 2018/8/17.
 */
data class Step(var path: List<Point>, var startLocation: Point, var endLocation: Point, var time: Int, var length: Int)