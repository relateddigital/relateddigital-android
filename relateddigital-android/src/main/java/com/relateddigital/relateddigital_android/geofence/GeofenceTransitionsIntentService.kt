package com.relateddigital.relateddigital_android.geofence

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.util.GeoFencesUtils
import java.lang.Exception

class GeofenceTransitionsIntentService : JobIntentService() {
    var mGpsManager: GpsManager? = null
    var mTriggerList: List<Geofence>? = null
    private var mGeoFenceEvent: GeofencingEvent? = null
    override fun onCreate() {
        super.onCreate()
        GeofenceStarter.startGpsManager(this)
    }

    override fun onHandleWork(intent: Intent) {
        Log.v(TAG, "onHandleWork")
        GeofenceStarter.startGpsManager(this)
        setGeofenceEvent(intent)
    }

    private fun setGeofenceEvent(intent: Intent) {
        mGeoFenceEvent = GeofencingEvent.fromIntent(intent)
        if (mGeoFenceEvent!!.hasError()) {
            val errorCode = mGeoFenceEvent!!.errorCode
            Log.e(
                TAG,
                "Location Services error: $errorCode"
            )
        } else {
            mGpsManager = Injector.INSTANCE.gpsManager
            if (mGpsManager == null) return
            mTriggerList = mGeoFenceEvent!!.triggeringGeofences
            if (Looper.myLooper() == null) Looper.prepare()
            val mainHandler = Handler(Looper.getMainLooper())
            val myRunnable = Runnable {
                try {
                    val closestGeofence = getClosestTriggeredGoefence(
                        mGpsManager!!, mTriggerList
                    )
                    if (closestGeofence != null) {
                        Log.d(TAG, "Triggered req id : " + closestGeofence.requestId)
                        geoFenceTriggered(
                            closestGeofence.requestId,
                            mGpsManager!!.lastKnownLocation!!.latitude,
                            mGpsManager!!.lastKnownLocation!!.longitude
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            mainHandler.post(myRunnable)
            if (Looper.myLooper() == null) Looper.loop()
        }
    }

    @Throws(Exception::class)
    private fun geoFenceTriggered(geofence_guid: String, lati: Double, longi: Double) {
        Log.i(TAG, geofence_guid)
        val geofenceParts = geofence_guid.split("_").toTypedArray()
        if (geofenceParts.size > 2) {
            RequestHandler.createGeofenceTriggerRequest(this, lati, longi,
                geofenceParts[0], geofenceParts[2])
        }
    }

    private fun getClosestTriggeredGoefence(
        gpsManager: GpsManager,
        triggerList: List<Geofence>?
    ): Geofence? {
        return if (triggerList!!.isEmpty()) {
            null
        } else if (triggerList.size == 1) {
            triggerList[0]
        } else {
            var triggeredGeofence: Geofence? = null
            var minDistance = Double.MAX_VALUE
            for (geofence in triggerList) {
                for (geoFenceEntity in gpsManager.mActiveGeoFenceEntityList) {
                    val distance: Double = GeoFencesUtils.haversine(
                        gpsManager.lastKnownLocation!!.latitude,
                        gpsManager.lastKnownLocation!!.longitude,
                        geoFenceEntity.latitude!!.toDouble(),
                        geoFenceEntity.longitude!!.toDouble()
                    )
                    if (distance < minDistance) {
                        triggeredGeofence = geofence
                        minDistance = distance
                        break
                    }
                }
            }
            triggeredGeofence
        }
    }

    companion object {
        private const val TAG = "GeofenceTIService"
        fun enqueueWork(context: Context?, intent: Intent?) {
            enqueueWork(
                context!!,
                GeofenceTransitionsIntentService::class.java, 573, intent!!
            )
        }
    }
}