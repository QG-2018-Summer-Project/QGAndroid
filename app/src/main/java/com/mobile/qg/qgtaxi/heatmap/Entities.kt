package com.mobile.qg.qgtaxi.heatmap

import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.WeightedLatLng
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * 热力图中点的标准返回类
 */
data class HeatMapLatLng(@SerializedName(value = "lon", alternate = ["lng"]) var lon: Double, var lat: Double, var weight: Int)

/**
 * 热力图的标准返回类
 */
data class HeatMapResponse(var status: Int, var pointSet: List<HeatMapLatLng>)

/**
 * 权重经纬度的工厂类
 */
object WeightedLatLngFactory {

    private fun getWeightedLatLng(heatMapLatLng: HeatMapLatLng): WeightedLatLng {
        return WeightedLatLng(LatLng(heatMapLatLng.lat, heatMapLatLng.lon), heatMapLatLng.weight.toDouble())
    }

    fun getWeightedList(listHeatMap: List<HeatMapLatLng>): List<WeightedLatLng> {

        val weightedList = ArrayList<WeightedLatLng>(listHeatMap.size)

        for (response in listHeatMap) {
            weightedList.add(getWeightedLatLng(response))
        }
        return weightedList
    }

}
