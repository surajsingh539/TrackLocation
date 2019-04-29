package com.suraj.tracklocationapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "locations")
data class UserLocation (

    @field:ColumnInfo(name = "lat")
    var lat: Double,

    @field:ColumnInfo(name = "lng")
    var lng: Double,

    @field:ColumnInfo(name = "time")
    var time: Long
){

    @PrimaryKey(autoGenerate = true)
    var _id: Int = 0
}