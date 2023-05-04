package com.relateddigital.relateddigital_android.network.requestHandler

import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Domain
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.model.Request
import com.relateddigital.relateddigital_android.network.RequestFormer
import com.relateddigital.relateddigital_android.network.RequestSender
import java.util.HashMap

object InAppActionClickRequest {
    private const val LOG_TAG = "InAppActionClickRequest"


    fun createInAppActionClickRequest(context: Context, report: MailSubReport?) {
        if (report?.click.isNullOrEmpty()) {
            Log.e(LOG_TAG, "report click is null or empty.")
            return
        }

        if (RelatedDigital.isBlocked(context)) {
            Log.w(LOG_TAG, "Too much server load, ignoring the request!")
            return
        }

        val queryMap = HashMap<String, String>()
        val headerMap = HashMap<String, String>()
        val properties = HashMap<String, String>()
        val tempMultiQuery: List<String> = report!!.click!!.split("&")
        for (s in tempMultiQuery) {
            val tempQueryString = s.split("=".toRegex(), 2).toTypedArray()
            if (tempQueryString.size == 2) {
                properties[tempQueryString[0]] = tempQueryString[1]
            }
        }

        RequestFormer.formInAppNotificationClickRequest(
            context = context,
            model = RelatedDigital.getRelatedDigitalModel(context),
            pageName = Constants.PAGE_NAME_REQUEST_VAL,
            properties = properties,
            queryMap = queryMap,
            headerMap = headerMap
        )

        RequestSender.addToQueue(
            Request(
                Domain.LOG_LOGGER, queryMap, headerMap,
                null
            ), RelatedDigital.getRelatedDigitalModel(context), context
        )
        RequestSender.addToQueue(
            Request(
                Domain.LOG_REAL_TIME, queryMap, headerMap,
                null
            ), RelatedDigital.getRelatedDigitalModel(context), context
        )
    }
}