package com.relateddigital.relateddigital_android.geofence

import com.relateddigital.relateddigital_android.model.GeofenceListResponse

/**
 * An VisilabsGeofenceGetListCallback will be used to execute code after a Visilabs Geofence GetList API request finishes running on a background thread.
 *
 */
interface GeofenceGetListCallback {
    /**
     * Will be run if the target call is successful
     *
     * @param response the response data
     * @param url requested url
     */
    fun success(response: List<GeofenceListResponse?>?, url: String)

    /**
     * Will be run if the target call is failed
     *
     * @param t for error message i.e. t.getMessage()
     * @param url requested url
     */
    fun fail(t: Throwable, url: String)
}