package com.suraj.tracklocationapp.application

import android.app.Application
import androidx.room.Room
import com.blankj.utilcode.util.Utils
import com.suraj.tracklocationapp.data.database.LocationDatabase
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build



class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.init(applicationContext)

        database = Room.databaseBuilder(
            applicationContext,
            LocationDatabase::class.java, "location_db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        createNotificationChannel()
    }

    companion object {
        var database: LocationDatabase? = null
        const val CHANNEL_ID = "ServiceChannel"
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}