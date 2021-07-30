package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName

class GeofenceListResponse {
    @SerializedName("actid")
    var actId: Int? = null

    @SerializedName("dis")
    var distance: Int? = null

    @SerializedName("geo")
    private var mGeofences: List<Geofence>? = null

    @SerializedName("trgevt")
    var trgevt: String? = null

    var geofences: List<Geofence>?
        get() = mGeofences
        set(geofences) {
            mGeofences = geofences
        }
}