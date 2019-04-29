package com.suraj.tracklocationapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.suraj.tracklocationapp.model.UserLocation

@Dao
interface LocationDao {

    /**
     * Get all locations.
     */
    @get:Query("SELECT * FROM locations")
    val getAllLocations: LiveData<List<UserLocation>>

    /**
     * Insert all locations.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllLocations(userLocation: List<UserLocation>)
}