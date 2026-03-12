package com.relateddigital.relateddigital_android.network.requestHandler

import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants.Companion.LOG_TAG
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.model.Domain
import com.relateddigital.relateddigital_android.model.Request
import com.relateddigital.relateddigital_android.network.RequestFormer
import com.relateddigital.relateddigital_android.network.RequestSender
import java.util.HashMap

object InAppNotificationClickRequest {

    private const val LOG_TAG = "InAppNotificationClickRequest"

    fun createInAppNotificationClickRequest(
        context: Context, inAppMessage: InAppMessage?, rating: String?
    ) {
        if (inAppMessage == null || inAppMessage.mActionData!!.mQs.isNullOrEmpty()) {
            Log.w(LOG_TAG, "Notification or query string is null or empty.")
            return
        }

        if (RelatedDigital.isBlocked(context)) {
            Log.w(LOG_TAG, "Too much server load, ignoring the request!")
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
        val model = RelatedDigital.getRelatedDigitalModel(context)

        RequestFormer.formInAppNotificationClickRequest(
            context = context,
            model = model,
            pageName = com.relateddigital.relateddigital_android.constants.Constants.PAGE_NAME_REQUEST_VAL,
            properties = properties,
            queryMap = queryMap,
            headerMap = headerMap
        )

        RequestSender.addToQueue(
            Request(Domain.LOG_LOGGER, queryMap, headerMap, null),
            model,
            context
        )
        RequestSender.addToQueue(
            Request(Domain.LOG_REAL_TIME, queryMap, headerMap, null),
            model,
            context
        )
    }
}