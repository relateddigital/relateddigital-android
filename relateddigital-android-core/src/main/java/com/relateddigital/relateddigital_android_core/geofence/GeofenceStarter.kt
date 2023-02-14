package com.relateddigital.relateddigital_android_core.geofence

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import java.util.*

object GeofenceStarter {
    private var geofencePermissionTimer: Timer? = null
    private var gpsManager: GpsManager? = null

    fun startGpsManager(context: Context) {
        val accessFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val accessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!(accessFineLocationPermission || accessCoarseLocationPermission)) {
            geofencePermissionTimer?.cancel()
            geofencePermissionTimer = Timer("startGpsManager", false)
            val task: TimerTask = object : TimerTask() {
                override fun run() {
                    startGpsManager(context)
                }
            }
            geofencePermissionTimer!!.schedule(task, 5000)
            return
        }
        gpsManager = GpsFactory.createManager(context)
        gpsManager!!.start()
    }
}