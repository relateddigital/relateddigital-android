package com.relateddigital.relateddigital_android.network.requestHandler

import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Domain
import com.relateddigital.relateddigital_android.model.Request
import com.relateddigital.relateddigital_android.network.RequestFormer
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.RequestSender
import com.relateddigital.relateddigital_android.util.PersistentTargetManager
import com.relateddigital.relateddigital_android.util.StringUtils
import java.util.HashMap


object GeofenceTriggerRequest {
    private const val LOG_TAG = "GeofenceTriggerRequest"

    fun createGeofenceTriggerRequest(
        context: Context,
        latitude: Double,
        longitude: Double,
        actId: String,
        geoId: String
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

        RequestFormer.formGeofenceTriggerRequest(
            context = context,
            model = RelatedDigital.getRelatedDigitalModel(context),
            pageName = Constants.PAGE_NAME_REQUEST_VAL,
            properties = propertiesLoc,
            queryMap = queryMap,
            headerMap = headerMap,
            latitude = latitude,
            longitude = longitude,
            actId = actId,
            geoId = geoId
        )

        RequestSender.addToQueue(
            Request(
                Domain.GEOFENCE_TRIGGER, queryMap, headerMap, null
            ), RelatedDigital.getRelatedDigitalModel(context), context
        )
    }
}