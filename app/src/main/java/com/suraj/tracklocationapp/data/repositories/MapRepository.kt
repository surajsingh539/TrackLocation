package com.suraj.tracklocationapp.data.repositories

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.LiveData
import com.blankj.utilcode.util.ToastUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.clustering.ClusterManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.suraj.tracklocationapp.R
import com.suraj.tracklocationapp.application.MyApplication
import com.suraj.tracklocationapp.model.UserCluster
import com.suraj.tracklocationapp.model.UserLocation
import com.suraj.tracklocationapp.ui.LocationHistory
import com.suraj.tracklocationapp.ui.MainActivity
import com.suraj.tracklocationapp.utils.Constants
import com.suraj.tracklocationapp.utils.General


class MapRepository {

    private var list: ArrayList<UserLocation> = ArrayList()

    fun setUserLocations(lat: Double, long : Double, time: Long){
        list.add(UserLocation(lat, long, time))
        MyApplication.database!!.locationDao().insertAllLocations(list)
    }

    fun getLocations(): LiveData<List<UserLocation>> {

        return MyApplication.database!!.locationDao().getAllLocations
    }

    fun setStartStop(start_tracking: FloatingActionButton, track: Boolean, activity: MainActivity){
        if (!track) {
            General.startService(activity, Constants.STOP_FOREGROUND_ACTION)
            start_tracking.setImageResource(R.drawable.ic_play)
            ToastUtils.showLong("Stopped tracking location")
        } else {
            General.startService(activity, Constants.START_FOREGROUND_ACTION)
            start_tracking.setImageResource(R.drawable.ic_stop)
            ToastUtils.showLong("Your location is getting tracked")
        }
    }

    fun requestPermission(activity: MainActivity, mMap: GoogleMap?, locationManager: LocationManager?, MIN_TIME: Long, MIN_DISTANCE: Float){

        Dexter.withActivity(activity)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .withListener(object : MultiplePermissionsListener {
                @SuppressLint("MissingPermission")
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        mMap?.isMyLocationEnabled = true
                        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, activity)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    fun setUpClusterer(mMap: GoogleMap?, mClusterManager: ClusterManager<UserCluster>,
                               lat: Double, lng: Double, list: List<UserLocation>, locationHistory: LocationHistory) {
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 8f))
        val mClusterManage: ClusterManager<UserCluster> = mClusterManager
        mClusterManage.setOnClusterClickListener(locationHistory)
        mClusterManage.setOnClusterItemClickListener(locationHistory)
        mMap?.setOnCameraIdleListener(mClusterManage)
        mMap?.setOnMarkerClickListener(mClusterManage)
        mMap?.setOnInfoWindowClickListener(mClusterManage)

        addItems(lat, lng, list, mClusterManage)
    }

    private fun addItems(lat: Double, lng: Double, list: List<UserLocation>, mClusterManager: ClusterManager<UserCluster>) {

        var latitude = lat
        var longitude = lng

        for (i in list.indices) {
            val offset = i / 60.0
            latitude += offset
            longitude += offset
            val offsetItem = UserCluster(latitude, longitude)
            mClusterManager.addItem(offsetItem)
        }
    }
}