package com.relateddigital.relateddigital_android.model

import com.google.android.gms.location.Geofence

class GeoFenceEntity {
    var guid: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var name: String? = null
    var radius = 0
    var type: String? = null
    var distance = 0.0
    var geoid = 0
    var durationInSeconds = 0

    fun toGeofence(): Geofence {
        val mTransitionType: Int = when (type) {
            "OnEnter" -> Geofence.GEOFENCE_TRANSITION_ENTER
            "OnExit" -> Geofence.GEOFENCE_TRANSITION_EXIT
            else -> Geofence.GEOFENCE_TRANSITION_DWELL
        }
        return if (mTransitionType == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Geofence.Builder()
                .setRequestId(guid!!)
                .setTransitionTypes(mTransitionType)
                .setCircularRegion(latitude!!.toDouble(), longitude!!.toDouble(), radius.toFloat())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(durationInSeconds * 1000)
                .build()
        } else {
            Geofence.Builder()
                .setRequestId(guid!!)
                .setTransitionTypes(mTransitionType)
                .setCircularRegion(latitude!!.toDouble(), longitude!!.toDouble(), radius.toFloat())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }
    }
}