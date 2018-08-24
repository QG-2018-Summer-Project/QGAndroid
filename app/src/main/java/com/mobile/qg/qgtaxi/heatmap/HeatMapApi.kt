package com.mobile.qg.qgtaxi.heatmap

import com.google.gson.Gson
import com.mobile.qg.qgtaxi.base.BaseApi
import com.mobile.qg.qgtaxi.entity.CurrentLatLng
import com.mobile.qg.qgtaxi.entity.PeriodLatLng
import com.mobile.qg.qgtaxi.entity.PredictedLatLng

import okhttp3.Response

/**
 * Created by 11234 on 2018/8/19.
 */
class HeatMapApi private constructor() : BaseApi() {

    fun liveHeatMap(latLng: CurrentLatLng): Response? {
        return post(MAP + CURRENT, Gson().toJson(latLng))
    }

    fun periodHeatMap(latLng: PeriodLatLng): Response? {
        return post(MAP + PERIOD, Gson().toJson(latLng))
    }

    fun predictedDemand(latLng: PredictedLatLng): Response? {
        return post(MAP + DEMAND, Gson().toJson(latLng))
    }

    fun predictedCount(latLng: PredictedLatLng): Response? {
        return post(MAP + COUNT, Gson().toJson(latLng))
    }

    companion object {

        private const val MAP = "maps/"

        private const val CURRENT = "liveheatmap"
        private const val PERIOD = "querymap"
        private const val DEMAND = "demanded"
        private const val COUNT = "count"

        @get:Synchronized
        val instance = HeatMapApi()
    }

}
