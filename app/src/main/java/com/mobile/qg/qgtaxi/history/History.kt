package com.mobile.qg.qgtaxi.history

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by 11234 on 2018/8/15.
 * 历史记录实体类
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

