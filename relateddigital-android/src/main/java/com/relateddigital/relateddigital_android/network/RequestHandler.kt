package com.relateddigital.relateddigital_android.network

import android.app.Activity
import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Domain
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.model.RelatedDigitalModel
import com.relateddigital.relateddigital_android.model.Request

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

    fun createInAppNotificationClickRequest(
            context: Context, inAppMessage: InAppMessage?, rating: String?
    ) {
        if (inAppMessage == null || inAppMessage.mActionData!!.mQs.isNullOrEmpty()) {
            Log.w(LOG_TAG, "Notification or query string is null or empty.")
            return
        }

        val properties = HashMap<String, String>()
        val tempMultiQuery: List<String> = inAppMessage.mActionData!!.mQs!!.split("&")
        for (s in tempMultiQuery) {
            val tempQueryString = s.split("=").toTypedArray()
            if (tempQueryString.size == 2) {
                properties[tempQueryString[0]] = tempQueryString[1]
            }
        }

        if (!rating.isNullOrEmpty()) {
            val tempMultiQuery2 = rating.split("&").toTypedArray()
            for (s in tempMultiQuery2) {
                val tempQueryString2 = s.split("=").toTypedArray()
                if (tempQueryString2.size == 2) {
                    properties[tempQueryString2[0]] = tempQueryString2[1]
                }
            }
        }

        val queryMap = HashMap<String, String>()
        val headerMap = HashMap<String, String>()
        RequestFormer.formInAppNotificationClickRequest(
                context = context,
                model = RelatedDigital.getRelatedDigitalModel(),
                pageName = Constants.PAGE_NAME_REQUEST_VAL,
                properties = properties,
                queryMap = queryMap,
                headerMap = headerMap
        )

        RequestSender.addToQueue(Request(Domain.LOG_LOGGER, queryMap, headerMap,
                null), RelatedDigital.getRelatedDigitalModel()!!, context)
        RequestSender.addToQueue(Request(Domain.LOG_REAL_TIME, queryMap, headerMap,
                null), RelatedDigital.getRelatedDigitalModel()!!, context)
    }
}