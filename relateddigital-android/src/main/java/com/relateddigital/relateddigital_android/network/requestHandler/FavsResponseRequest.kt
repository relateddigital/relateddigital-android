package com.relateddigital.relateddigital_android.network.requestHandler

import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.inapp.VisilabsCallback
import com.relateddigital.relateddigital_android.model.Domain
import com.relateddigital.relateddigital_android.model.Request
import com.relateddigital.relateddigital_android.network.RequestFormer
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.RequestSender
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.PersistentTargetManager
import com.relateddigital.relateddigital_android.util.StringUtils
import java.util.HashMap

object FavsResponseRequest {

    private const val LOG_TAG = "FavsResponseRequest"

    fun createFavsResponseRequest(
        context: Context,
        actionId: String? = null,
        actionType: String? = null,
        visilabsCallback: VisilabsCallback,
        properties: HashMap<String, String>? = null
    ) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(LOG_TAG, "Too much server load, ignoring the request!")
            return
        }

        if(actionId.isNullOrEmpty() && actionType.isNullOrEmpty()) {
            Log.e(LOG_TAG, "actionId and actionType cannot be null at the same time!")
            return
        }

        RequestFormer.updateSessionParameters(context, Constants.PAGE_NAME_REQUEST_VAL)

        val propertiesLoc = HashMap<String, String>()
        val queryMap = HashMap<String, String>()
        val headerMap = HashMap<String, String>()

        if(!actionId.isNullOrEmpty()) {
            propertiesLoc[Constants.REQUEST_ACTION_ID_KEY] = actionId
        } else {
            propertiesLoc[Constants.REQUEST_ACTION_TYPE_KEY] = actionType!!
        }

        if (properties != null && properties.size > 0) {
            AppUtils.cleanParameters(properties)
            for ((key, value) in properties.entries) {
                if (!StringUtils.isNullOrWhiteSpace(key) && !StringUtils.isNullOrWhiteSpace(value)) {
                    propertiesLoc[key] = value
                }
            }
        }

        val parameters = PersistentTargetManager.getParameters(context)
        for ((key, value) in parameters) {
            if (!StringUtils.isNullOrWhiteSpace(key) && !StringUtils.isNullOrWhiteSpace(value)
                && properties != null && !properties.containsKey(key)) {
                propertiesLoc[key] = value
            }
        }

        RequestFormer.formFavsResponseRequest(
            context = context,
            model = RelatedDigital.getRelatedDigitalModel(context),
            pageName = Constants.PAGE_NAME_REQUEST_VAL,
            properties = propertiesLoc,
            queryMap = queryMap,
            headerMap = headerMap
        )

        RequestSender.addToQueue(
            Request(
                Domain.IN_APP_FAVS_RESPONSE_MOBILE, queryMap, headerMap,
                null, visilabsCallback
            ), RelatedDigital.getRelatedDigitalModel(context), context
        )
    }
}