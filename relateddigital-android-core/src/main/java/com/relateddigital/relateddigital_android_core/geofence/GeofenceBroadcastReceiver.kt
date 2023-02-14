package com.relateddigital.relateddigital_android_core.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        GeofenceTransitionsIntentService.enqueueWork(context, intent)
    }
}