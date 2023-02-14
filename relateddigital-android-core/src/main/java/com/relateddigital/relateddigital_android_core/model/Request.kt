package com.relateddigital.relateddigital_android_core.model

import android.app.Activity
import com.relateddigital.relateddigital_android_core.geofence.GeofenceGetListCallback
import com.relateddigital.relateddigital_android_core.inapp.VisilabsCallback

data class Request(
    val domain: Domain, val queryMap: HashMap<String, String>,
    val headerMap: HashMap<String, String>, val parent: Activity?,
    val visilabsCallback: VisilabsCallback? = null,
    val geofenceGetListCallback: GeofenceGetListCallback? = null
) {
}