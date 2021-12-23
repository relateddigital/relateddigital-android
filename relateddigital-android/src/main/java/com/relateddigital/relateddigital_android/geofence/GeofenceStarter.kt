package com.relateddigital.relateddigital_android.geofence

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import java.util.*

object GeofenceStarter {
    private var geofencePermissionTimer: Timer? = null
    private var gpsManager: GpsManager? = null

    fun startGpsManager(context: Context) {
        val per =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (per != PackageManager.PERMISSION_GRANTED) {
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