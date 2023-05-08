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
import com.relateddigital.relateddigital_android.recommendation.VisilabsTargetFilter
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.PersistentTargetManager
import com.relateddigital.relateddigital_android.util.StringUtils
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

object RecommendationRequest {
    private const val LOG_TAG = "RecommendationRequest"


    fun createRecommendationRequest(
        context: Context,
        zoneId: String,
        productCode: String,
        visilabsCallback: VisilabsCallback,
        properties: HashMap<String, String>? = null,
        filters: List<VisilabsTargetFilter>? = null,
    ) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(LOG_TAG, "Too much server load, ignoring the request!")
            return
        }

        RequestFormer.updateSessionParameters(context, Constants.PAGE_NAME_REQUEST_VAL)

        val propertiesLoc = HashMap<String, String>()
        val queryMap = HashMap<String, String>()
        val headerMap = HashMap<String, String>()

        if(zoneId.isNotEmpty()) {
            propertiesLoc[Constants.ZONE_ID_KEY] = zoneId
        }

        if(productCode.isNotEmpty()) {
            propertiesLoc[Constants.BODY_KEY] = productCode
        }

        if (properties != null && properties.size > 0) {
            AppUtils.cleanParameters(properties)
            for ((key, value) in properties.entries) {
                if (!StringUtils.isNullOrWhiteSpace(key) && !StringUtils.isNullOrWhiteSpace(value)) {
                    propertiesLoc[key] = value
                }
            }
        }

        try {
            if (filters != null && filters.isNotEmpty()) {
                val jsonArray = JSONArray()
                for (filter in filters) {
                    if (!StringUtils.isNullOrWhiteSpace(filter.attribute) && !StringUtils.isNullOrWhiteSpace(filter.filterType)
                        && !StringUtils.isNullOrWhiteSpace(filter.value)) {
                        val filterJSON = JSONObject()
                        filterJSON.put("attr", filter.attribute)
                        filterJSON.put("ft", filter.filterType)
                        filterJSON.put("fv", filter.value)
                        jsonArray.put(filterJSON)
                    }
                }
                val jsonString: String = jsonArray.toString()
                propertiesLoc[Constants.FILTER_KEY] = jsonString
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
        }

        val parameters = PersistentTargetManager.getParameters(context)
        for ((key, value) in parameters) {
            if (!StringUtils.isNullOrWhiteSpace(key) && !StringUtils.isNullOrWhiteSpace(value)
                && properties != null && !properties.containsKey(key)) {
                propertiesLoc[key] = value
            }
        }

        RequestFormer.formRecommendationRequest(
            context = context,
            model = RelatedDigital.getRelatedDigitalModel(context),
            pageName = Constants.PAGE_NAME_REQUEST_VAL,
            properties = propertiesLoc,
            queryMap = queryMap,
            headerMap = headerMap
        )

        RequestSender.addToQueue(
            Request(
                Domain.IN_APP_RECOMMENDATION_JSON, queryMap, headerMap,
                null, visilabsCallback
            ), RelatedDigital.getRelatedDigitalModel(context), context
        )
    }
}