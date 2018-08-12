package com.mobile.qg.qgtaxi.entity

import com.amap.api.maps.model.LatLng

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by 11234 on 2018/8/12.
 */
object LatLngFactory {

    private val FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    fun getCurrent(farLeft: LatLng, nearRight: LatLng): CurrentLatLng {
        return CurrentLatLng(farLeft.longitude,
                farLeft.latitude,
                nearRight.longitude,
                nearRight.latitude,
                FORMATTER.format(Date()))
    }

    fun getPeriod(farLeft: LatLng, nearRight: LatLng, start: Long, end: Long): PeriodLatLng {
        return PeriodLatLng(farLeft.longitude,
                farLeft.latitude,
                nearRight.longitude,
                nearRight.latitude,
                FORMATTER.format(Date(start)),
                FORMATTER.format(Date(end)))
    }


}