package com.relateddigital.relateddigital_android.network.requestHandler


import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Domain
import com.relateddigital.relateddigital_android.model.Request
import com.relateddigital.relateddigital_android.network.RequestFormer
import com.relateddigital.relateddigital_android.network.RequestSender


object SubsJsonRequest {
    private const val LOG_TAG = "SubsJsonRequest"

    fun createSubsJsonRequest(
        context: Context,
        type: String,
        actId: String,
        auth: String,
        email: String
    ) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(LOG_TAG, "Too much server load, ignoring the request!")
            return
        }

        val properties = HashMap<String, String>()
        val queryMap = HashMap<String, String>()
        val headerMap = HashMap<String, String>()
        properties[Constants.REQUEST_TYPE_KEY] = type
        properties[Constants.REQUEST_SUBS_ACTION_ID_KEY] = actId
        properties[Constants.REQUEST_AUTH_KEY] = auth
        properties[Constants.REQUEST_SUBS_EMAIL_KEY] = email
        RequestFormer.formSubJsonRequest(
            context = context,
            model = RelatedDigital.getRelatedDigitalModel(context),
            pageName = Constants.PAGE_NAME_REQUEST_VAL,
            properties = properties,
            queryMap = queryMap,
            headerMap = headerMap
        )

        RequestSender.addToQueue(
            Request(
                Domain.LOG_S, queryMap, headerMap,
                null
            ), RelatedDigital.getRelatedDigitalModel(context), context
        )

    }

}