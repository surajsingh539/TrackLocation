package com.suraj.tracklocationapp.viewmodel

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.location.LocationManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.clustering.ClusterManager
import com.suraj.tracklocationapp.data.repositories.MapRepository
import com.suraj.tracklocationapp.model.UserCluster
import com.suraj.tracklocationapp.model.UserLocation
import com.suraj.tracklocationapp.ui.LocationHistory
import com.suraj.tracklocationapp.ui.MainActivity
import com.suraj.tracklocationapp.utils.Constants

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var mapRepository: MapRepository = MapRepository()
    var isTracking = false

    fun getLocations(): LiveData<List<UserLocation>> {
        return mapRepository.getLocations()
    }

    fun setStartStop(start_tracking: FloatingActionButton, track: Boolean, activity: MainActivity) {

        isTracking = track
        mapRepository.setStartStop(start_tracking, track, activity)
    }

    fun requestPermission(activity: MainActivity, mMap: GoogleMap?, locationManager: LocationManager?) {

        mapRepository.requestPermission(activity, mMap, locationManager, Constants.GPS_TIME_INTERVAL, Constants.GPS_DISTANCE)
    }

    fun insertAllLocations(lat: Double, long: Double, time: Long){
        mapRepository.setUserLocations(lat, long, time)
    }

    fun setUpClusterer(mMap: GoogleMap?, mClusterManager: ClusterManager<UserCluster>,
                       lat: Double, lng: Double, list: List<UserLocation>, locationHistory: LocationHistory) {
        mapRepository.setUpClusterer(mMap, mClusterManager, lat, lng, list, locationHistory)
    }

    fun isServiceRunningInForeground(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                if (service.foreground) {
                    return true
                }

            }
        }
        return false
    }
}