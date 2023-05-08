package com.relateddigital.relateddigital_android.network.requestHandler

import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.geofence.GeofenceGetListCallback
import com.relateddigital.relateddigital_android.model.Domain
import com.relateddigital.relateddigital_android.model.Request
import com.relateddigital.relateddigital_android.network.RequestFormer
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.RequestSender
import com.relateddigital.relateddigital_android.util.PersistentTargetManager
import com.relateddigital.relateddigital_android.util.StringUtils
import java.util.HashMap

object GeofenceGetListResponseRequest {
    private const val LOG_TAG = "GeofenceGetListResponseRequest"

    fun createGeofenceGetListResponseRequest(
        context: Context,
        latitude: Double,
        longitude: Double,
        geofenceGetListCallback: GeofenceGetListCallback,
    ) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(LOG_TAG, "Too much server load, ignoring the request!")
            return
        }

        val propertiesLoc = HashMap<String, String>()
        val queryMap = HashMap<String, String>()
        val headerMap = HashMap<String, String>()

        val parameters = PersistentTargetManager.getParameters(context)
        for ((key, value) in parameters) {
            if (!StringUtils.isNullOrWhiteSpace(key) && !StringUtils.isNullOrWhiteSpace(value)) {
                propertiesLoc[key] = value
            }
        }

        RequestFormer.formGeofenceGetListResponseRequest(
            context = context,
            model = RelatedDigital.getRelatedDigitalModel(context),
            pageName = Constants.PAGE_NAME_REQUEST_VAL,
            properties = propertiesLoc,
            queryMap = queryMap,
            headerMap = headerMap,
            latitude = latitude,
            longitude = longitude
        )

        RequestSender.addToQueue(
            Request(
                Domain.GEOFENCE_GET_LIST, queryMap, headerMap,
                null, null, geofenceGetListCallback
            ), RelatedDigital.getRelatedDigitalModel(context), context
        )
    }
}