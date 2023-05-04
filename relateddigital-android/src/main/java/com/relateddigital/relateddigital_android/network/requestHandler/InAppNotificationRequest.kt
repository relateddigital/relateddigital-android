package com.relateddigital.relateddigital_android.network.requestHandler

import android.app.Activity
import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.model.Domain
import com.relateddigital.relateddigital_android.model.RelatedDigitalModel
import com.relateddigital.relateddigital_android.model.Request
import com.relateddigital.relateddigital_android.network.RequestFormer
import com.relateddigital.relateddigital_android.network.RequestSender
import java.util.HashMap

object InAppNotificationRequest {

    private const val LOG_TAG = "InAppNotificationRequest"


    fun createInAppNotificationRequest(
        context: Context, model: RelatedDigitalModel, pageName: String,
        properties: HashMap<String, String>?, parent: Activity? = null
    ) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(LOG_TAG, "Too much server load, ignoring the request!")
            return
        }

        val queryMap = HashMap<String, String>()
        val headerMap = HashMap<String, String>()
        RequestFormer.formInAppNotificationRequest(
            context = context,
            model = model,
            pageName = pageName,
            properties = properties,
            queryMap = queryMap,
            headerMap = headerMap
        )

        RequestSender.addToQueue(
            Request(
                Domain.IN_APP_NOTIFICATION_ACT_JSON,
                queryMap,
                headerMap,
                parent
            ), model, context
        )
    }
}