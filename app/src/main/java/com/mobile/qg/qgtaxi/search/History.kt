package com.mobile.qg.qgtaxi.search

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.help.Tip

/**
 * Created by 11234 on 2018/8/15.
 */
@Entity(tableName = "history")
data class History(

        @PrimaryKey
        @ColumnInfo(name = "id")
        var id: String,

        @ColumnInfo(name = "name")
        var name: String,

        @ColumnInfo(name = "address")
        var address: String,

        @ColumnInfo(name = "latitude")
        var lat: Double,

        @ColumnInfo(name = "longitude")
        var lng: Double

)

