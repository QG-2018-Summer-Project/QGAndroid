package com.mobile.qg.qgtaxi.route.entity

/**
 * Created by 11234 on 2018/8/22.
 */
data class Point(var lat: Double, var lon: Double)
data class Route(var index: Int, var steps: List<Step>, var allTime: Int, var distance: Int)
data class RouteResponse(var status: String, var index: Int)
data class Routes(var routes: List<Route>)
data class Step(var path: List<Point>, var startLocation: Point, var endLocation: Point, var time: Int, var length: Int)