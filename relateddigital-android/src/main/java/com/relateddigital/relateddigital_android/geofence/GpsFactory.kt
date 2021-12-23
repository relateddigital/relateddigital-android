package com.relateddigital.relateddigital_android.geofence

import android.content.Context

object GpsFactory {
    private var mGpsManager: GpsManager? = null
    fun createManager(context: Context): GpsManager? {
        if (mGpsManager == null) {
            mGpsManager = GpsManager(context)
            return mGpsManager
        }
        return mGpsManager
    }
}