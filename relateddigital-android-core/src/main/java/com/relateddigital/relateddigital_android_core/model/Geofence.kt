package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName

class Geofence {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("lat")
    var latitude: Double? = null

    @SerializedName("long")
    var longitude: Double? = null

    @SerializedName("rds")
    var radius: Double? = null
}