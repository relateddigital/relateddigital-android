package com.relateddigital.relateddigital_android.geofence

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager

class GeofenceAlarm : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        @SuppressLint("InvalidWakeLockTag") val wl =
            pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "")
        wl.acquire(10000)
        val gpsManager: GpsManager? = Injector.INSTANCE.gpsManager
        if (gpsManager == null) {
            GeofenceStarter.startGpsManager(context)
        } else {
            gpsManager.startGpsService()
        }
        wl.release()
    }

    fun setAlarmCheckIn(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(context, GeofenceAlarm::class.java)
        val pi: PendingIntent = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(context, 0, i, 0)
        }
        val fifteenMinutes = 15 * 60 * 1000
        alarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            0,
            fifteenMinutes.toLong(),
            pi
        )
    }

    companion object {
        var singleton = GeofenceAlarm()
    }
}