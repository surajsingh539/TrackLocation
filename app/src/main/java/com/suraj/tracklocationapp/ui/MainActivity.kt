package com.suraj.tracklocationapp.ui

import android.location.LocationManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.LogUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.suraj.tracklocationapp.R
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.suraj.tracklocationapp.model.UserLocation
import com.suraj.tracklocationapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import com.suraj.tracklocationapp.service.LocationService


class MainActivity : FragmentActivity(), OnMapReadyCallback, LocationListener {

    private var mMap: GoogleMap?=null
    private var locationManager: LocationManager?=null
    private lateinit var mainViewModel: MainViewModel
    private var lat: Double?= 0.0
    private var lng: Double?= 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        startTracking()
    }

    private fun init(){
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        if (mainViewModel.isServiceRunningInForeground(this, LocationService::class.java)){
            start_tracking.setImageResource(R.drawable.ic_stop)
            mainViewModel.isTracking = true
        }

        mainViewModel.getLocations().observe(this, Observer<List<UserLocation>> { user_locations ->
            LogUtils.e(user_locations)
        })
    }

    @SuppressLint("MissingPermission")
    private fun startTracking(){
        start_tracking.setOnClickListener {

            if (!mainViewModel.isTracking){
                lat?.let { it1 -> lng?.let { it2 ->
                    mainViewModel.insertAllLocations(it1,
                        it2, System.currentTimeMillis())
                } }
            }
            mainViewModel.setStartStop(start_tracking, !mainViewModel.isTracking, this)
        }

        location_history.setOnClickListener {
            startActivity(Intent(this, LocationHistory::class.java))
        }

        relocate.setOnClickListener {
            //Acquire the user's location
            val selfLocation = locationManager
                ?.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            val selfLoc = LatLng(selfLocation!!.latitude, selfLocation.longitude)
            val update = CameraUpdateFactory.newLatLngZoom(selfLoc, 15f)
            mMap?.animateCamera(update)
        }
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {

        mMap = googleMap
        mainViewModel.requestPermission(this, mMap, locationManager)
        mMap?.uiSettings?.isCompassEnabled = false
        mMap?.uiSettings?.isMyLocationButtonEnabled = false

    }

    override fun onLocationChanged(location: Location?) {
        lat = location?.latitude
        lng = location?.longitude
        val latLng = location?.latitude?.let { LatLng(it, location.longitude) }
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
        mMap?.addMarker(latLng?.let { MarkerOptions().position(it).title("You are here") })
        mMap?.animateCamera(cameraUpdate)
        locationManager?.removeUpdates(this)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }
}
