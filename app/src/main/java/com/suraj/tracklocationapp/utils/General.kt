package com.suraj.tracklocationapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.suraj.tracklocationapp.service.LocationService
import java.text.SimpleDateFormat
import java.util.*
import android.location.Geocoder


object General {

    fun setToolbar(activity: AppCompatActivity, toolbar: Toolbar) {
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.title = null
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun startService(activity: Activity, intentAction: String){
        val intent = Intent(activity, LocationService::class.java)
        intent.action = intentAction
        ContextCompat.startForegroundService(activity, intent)
    }

    fun getDateFormat(time: Long, pattern: String): String {
        val date = Date(time)
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    fun getAddressName(lat: Double, lng: Double, context: Context) : String{

        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        return  addresses[0].subLocality
    }
}