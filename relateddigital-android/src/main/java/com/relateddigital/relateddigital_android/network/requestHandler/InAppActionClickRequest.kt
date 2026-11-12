package com.relateddigital.relateddigital_android.network.requestHandler

import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Domain
import com.relateddigital.relateddigital_android.model.Request
import com.relateddigital.relateddigital_android.network.RequestFormer
import com.relateddigital.relateddigital_android.network.RequestSender
import java.util.HashMap

object InAppActionClickRequest {
    private const val LOG_TAG = "InAppActionClickRequest"


    fun createInAppActionClickRequest(context: Context, report: MailSubReport?, parent: android.app.Activity? = null) {
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

        val model = RelatedDigital.getRelatedDigitalModel(context)

        RequestFormer.formInAppNotificationClickRequest(
            context = context,
            model = model,
            pageName = Constants.PAGE_NAME_REQUEST_VAL,
            properties = properties,
            queryMap = queryMap,
            headerMap = headerMap
        )

        RequestSender.addToQueue(
            Request(
                Domain.LOG_LOGGER, queryMap, headerMap,
                null
            ), model, context
        )
        RequestSender.addToQueue(
            Request(
                Domain.LOG_REAL_TIME, queryMap, headerMap,
                null
            ), model, context
        )

        if (model.getIsInAppNotificationEnabled()) {
            val targetParent = parent ?: com.relateddigital.relateddigital_android.util.ActivityUtils.parentActivity
            InAppNotificationRequest.createInAppNotificationRequest(
                context,
                model,
                Constants.PAGE_NAME_REQUEST_VAL,
                properties,
                targetParent
            )
            InAppActionRequest.createInAppActionRequest(
                context,
                model,
                Constants.PAGE_NAME_REQUEST_VAL,
                properties,
                targetParent
            )
        }
    }

    fun createInAppActionImressionRequest(context: Context, report: MailSubReport?) {
        if (report?.impression.isNullOrEmpty()) {
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
        val tempMultiQuery: List<String> = report!!.impression!!.split("&")
        for (s in tempMultiQuery) {
            val tempQueryString = s.split("=".toRegex(), 2).toTypedArray()
            if (tempQueryString.size == 2) {
                properties[tempQueryString[0]] = tempQueryString[1]
            }
        }

        RequestFormer.formInAppNotificationImpressionRequest(
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