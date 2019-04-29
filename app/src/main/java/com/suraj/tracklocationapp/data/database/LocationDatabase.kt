package com.suraj.tracklocationapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.suraj.tracklocationapp.data.dao.LocationDao
import com.suraj.tracklocationapp.model.UserLocation

@Database(entities = [(UserLocation::class)], version = 1)
abstract class LocationDatabase : RoomDatabase(){
    abstract fun locationDao(): LocationDao
}