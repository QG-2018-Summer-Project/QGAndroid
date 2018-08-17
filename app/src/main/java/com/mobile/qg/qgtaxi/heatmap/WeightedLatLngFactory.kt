package com.mobile.qg.qgtaxi.heatmap

import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.WeightedLatLng

import java.util.ArrayList

/**
 * Created by 11234 on 2018/8/12.
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
