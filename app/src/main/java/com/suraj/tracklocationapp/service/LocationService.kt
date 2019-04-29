package com.suraj.tracklocationapp.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.blankj.utilcode.util.LogUtils
import com.suraj.tracklocationapp.R
import com.suraj.tracklocationapp.application.MyApplication
import com.suraj.tracklocationapp.model.UserLocation
import android.os.Handler
import com.blankj.utilcode.util.ToastUtils
import com.suraj.tracklocationapp.ui.MainActivity
import com.suraj.tracklocationapp.utils.Constants
import com.suraj.tracklocationapp.utils.Constants.GPS_DISTANCE
import com.suraj.tracklocationapp.utils.Constants.GPS_TIME_INTERVAL
import com.suraj.tracklocationapp.utils.Constants.START_FOREGROUND_ACTION
import com.suraj.tracklocationapp.utils.Constants.STOP_FOREGROUND_ACTION


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LocationService : Service(), LocationListener {

    private var locationManager: LocationManager?=null
    var gpslocation: Location? = null
    val handler = Handler()
    
    override fun onCreate() {
        super.onCreate()
        obtainLocation()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if (intent.action == START_FOREGROUND_ACTION) {
            val stopSelf = Intent(this, LocationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopSelf.action = STOP_FOREGROUND_ACTION
            }
            val stopIntent = PendingIntent.getService(
                this,
                0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT)

            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0)

            val notification = NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setContentTitle("Track Location")
                .setContentText("Capturing your location")
                .setSmallIcon(R.drawable.ic_location)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_location, "STOP", stopIntent)
                .build()

            startForeground(1, notification)
        }
        else if (intent.action == STOP_FOREGROUND_ACTION) {
            handler.removeCallbacksAndMessages(null)
            stopForeground(true)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager?.removeUpdates(this)
        locationManager= null
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val list: ArrayList<UserLocation> = ArrayList()
                list.add(UserLocation(location.latitude, location.longitude, System.currentTimeMillis()))
                MyApplication.database!!.locationDao().insertAllLocations(list)
                obtainLocation()
                ToastUtils.showLong("Your location is getting saved")
                handler.postDelayed(this, GPS_TIME_INTERVAL)
            }
        }, GPS_TIME_INTERVAL)
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }

    @SuppressLint("MissingPermission")
    fun obtainLocation(){
        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        if(locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!){
            gpslocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    GPS_TIME_INTERVAL, GPS_DISTANCE, this)
        }
    }
}

