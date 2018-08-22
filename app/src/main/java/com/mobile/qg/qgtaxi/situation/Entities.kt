package com.mobile.qg.qgtaxi.situation

import java.io.Serializable

/**
 * Created by 11234 on 2018/8/23.
 */
data class PointSet(var lon: Double, var lat: Double, var type: Int, var reason: String) : Serializable

data class Exception(var currentTime: String)

data class SituationResponse (var status: Int,var pointSet: List<PointSet>)