package com.relateddigital.relateddigital_android.network

import android.app.Activity
import android.content.Context
import com.relateddigital.relateddigital_android.model.Domain
import com.relateddigital.relateddigital_android.model.RelatedDigitalModel
import com.relateddigital.relateddigital_android.model.Request
import kotlin.collections.HashMap

object RequestHandler {
    private const val LOG_TAG = "RequestHandler"

    fun createLoggerRequest(
        context: Context, model: RelatedDigitalModel, pageName: String,
        properties: HashMap<String, String>?
    ) {
        val queryMap = HashMap<String, String>()
        val headerMap = HashMap<String, String>()
        RequestFormer.formLoggerRequest(
            context = context,
            model = model,
            pageName = pageName,
            properties = properties,
            queryMap = queryMap,
            headerMap = headerMap
        )

        RequestSender.addToQueue(Request(Domain.LOG_LOGGER, queryMap, headerMap, null), model, context)
        RequestSender.addToQueue(Request(Domain.LOG_REAL_TIME, queryMap, headerMap, null), model, context)
    }

    fun createInAppNotificationRequest(
        context: Context, model: RelatedDigitalModel, pageName: String,
        properties: HashMap<String, String>?, parent: Activity? = null
    ) {
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

        RequestSender.addToQueue(Request(Domain.IN_APP_NOTIFICATION_ACT_JSON, queryMap, headerMap, parent), model, context)
    }
}