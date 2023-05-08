package com.relateddigital.relateddigital_android.network

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.geofence.GeofenceGetListCallback
import com.relateddigital.relateddigital_android.inapp.VisilabsCallback
import com.relateddigital.relateddigital_android.model.*
import com.relateddigital.relateddigital_android.model.Retention
import com.relateddigital.relateddigital_android.push.EuromessageCallback
import com.relateddigital.relateddigital_android.push.RetentionType
import com.relateddigital.relateddigital_android.recommendation.VisilabsTargetFilter
import com.relateddigital.relateddigital_android.util.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object RequestHandler {
    private const val LOG_TAG = "RequestHandler"

    private var latestDeliverPushId: String = ""
    private var latestOpenPushId: String = ""
/*
    fun createLoggerRequest(
            context: Context, model: RelatedDigitalModel, pageName: String,
            properties: HashMap<String, String>?
    ) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(LOG_TAG, "Too much server load, ignoring the request!")
            return
        }

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

     fun createInAppActionRequest(
            context: Context, model: RelatedDigitalModel, pageName: String,
            properties: HashMap<String, String>?, parent: Activity? = null
    ) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(LOG_TAG, "Too much server load, ignoring the request!")
            return
        }

        val queryMap = HashMap<String, String>()
        val headerMap = HashMap<String, String>()
        RequestFormer.formInAppActionRequest(
                context = context,
                model = model,
                pageName = pageName,
                properties = properties,
                queryMap = queryMap,
                headerMap = headerMap
        )

        RequestSender.addToQueue(
                Request(Domain.IN_APP_ACTION_MOBILE, queryMap, headerMap, parent),
                model,
                context
        )
    }

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

    fun createStoryImpressionClickRequest(context: Context, report: String?) {
        if (report.isNullOrEmpty()) {
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
        val tempMultiQuery = report.split("&".toRegex()).toTypedArray()
        for (s in tempMultiQuery) {
            val tempQueryString = s.split("=".toRegex(), 2).toTypedArray()
            if (tempQueryString.size == 2) {
                properties[tempQueryString[0]] = tempQueryString[1]
            }
        }

        RequestFormer.formStoryImpressionClickRequest(
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

    fun createSpinToWinPromoCodeRequest(
            context: Context,
            visilabsCallback: VisilabsCallback,
            properties: HashMap<String, String>?
    ) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(LOG_TAG, "Too much server load, ignoring the request!")
            return
        }

        val queryMap = HashMap<String, String>()
        val headerMap = HashMap<String, String>()
        RequestFormer.formSpinToWinPromoCodeRequest(
                context = context,
                model = RelatedDigital.getRelatedDigitalModel(context),
                pageName = Constants.PAGE_NAME_REQUEST_VAL,
                properties = properties,
                queryMap = queryMap,
                headerMap = headerMap
        )

        RequestSender.addToQueue(
                Request(
                        Domain.IN_APP_SPIN_TO_WIN_PROMO_CODE, queryMap, headerMap,
                        null, visilabsCallback
                ), RelatedDigital.getRelatedDigitalModel(context), context
        )
    }

    fun createNpsWithNumbersRequest(
        context: Context,visilabsCallback: VisilabsCallback,
        properties: HashMap<String, String>?
    ) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(LOG_TAG, "Too much server load, ignoring the request!")
            return
        }

        val queryMap = HashMap<String, String>()
        val headerMap = HashMap<String, String>()
        RequestFormer.formNpsActionRequest(
            context = context,
            model = RelatedDigital.getRelatedDigitalModel(context),
            pageName = Constants.PAGE_NAME_REQUEST_VAL,
            properties = properties,
            queryMap = queryMap,
            headerMap = headerMap
        )

        RequestSender.addToQueue(
            Request(
                Domain.IN_APP_NOTIFICATION_ACT_JSON,
                queryMap,
                headerMap,null,visilabsCallback

            ), RelatedDigital.getRelatedDigitalModel(context), context
        )
    }


    fun createStoryActionRequest(
            context: Context,
            visilabsCallback: VisilabsCallback,
            properties: HashMap<String, String>?
    ) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(LOG_TAG, "Too much server load, ignoring the request!")
            return
        }

        RequestFormer.updateSessionParameters(context, Constants.STORY_ACTION_TYPE_VAL)

        val queryMap = HashMap<String, String>()
        val headerMap = HashMap<String, String>()
        RequestFormer.formStoryActionRequest(
                context = context,
                model = RelatedDigital.getRelatedDigitalModel(context),
                pageName = Constants.PAGE_NAME_REQUEST_VAL,
                properties = properties,
                queryMap = queryMap,
                headerMap = headerMap
        )

        RequestSender.addToQueue(
                Request(
                        Domain.IN_APP_STORY_MOBILE, queryMap, headerMap,
                        null, visilabsCallback
                ), RelatedDigital.getRelatedDigitalModel(context), context
        )
    }

    fun createBannerCarouselActionRequest(
        context: Context,
        visilabsCallback: VisilabsCallback,
        properties: HashMap<String, String>?
    ) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(LOG_TAG, "Too much server load, ignoring the request!")
            return
        }

        RequestFormer.updateSessionParameters(context, Constants.STORY_ACTION_TYPE_VAL)

        val queryMap = HashMap<String, String>()
        val headerMap = HashMap<String, String>()
        RequestFormer.formBannerCarouselActionRequest(
            context = context,
            model = RelatedDigital.getRelatedDigitalModel(context),
            pageName = Constants.PAGE_NAME_REQUEST_VAL,
            properties = properties,
            queryMap = queryMap,
            headerMap = headerMap
        )

        RequestSender.addToQueue(
            Request(
                Domain.IN_APP_ACTION_MOBILE, queryMap, headerMap,
                null, visilabsCallback
            ), RelatedDigital.getRelatedDigitalModel(context), context
        )
    }

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

    fun createGeofenceGetListResponseRequest(
        context: Context,
        latitude: Double,
        longitude: Double,
        geofenceGetListCallback: GeofenceGetListCallback,
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

        RequestFormer.formGeofenceGetListResponseRequest(
            context = context,
            model = RelatedDigital.getRelatedDigitalModel(context),
            pageName = Constants.PAGE_NAME_REQUEST_VAL,
            properties = propertiesLoc,
            queryMap = queryMap,
            headerMap = headerMap,
            latitude = latitude,
            longitude = longitude
        )

        RequestSender.addToQueue(
            Request(
                Domain.GEOFENCE_GET_LIST, queryMap, headerMap,
                null, null, geofenceGetListCallback
            ), RelatedDigital.getRelatedDigitalModel(context), context
        )
    }

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

    fun createSyncRequest(context: Context, callback: EuromessageCallback? = null) {
        if (Build.VERSION.SDK_INT < Constants.SDK_MIN_API_VERSION) {
            Log.e(LOG_TAG, "RelatedDigital SDK requires min API level 21!")
            return
        }

        val model = RelatedDigital.getRelatedDigitalModel(context)

        if (model.getToken().isEmpty() || (model.getGoogleAppAlias().isEmpty() && model.getHuaweiAppAlias().isEmpty()) ) {
            Log.e(LOG_TAG, "token or appKey cannot be null!")
            return
        }

        if(!model.isEqual(RelatedDigital.getPreviousModel()) && model.isValid(context)) {
            RelatedDigital.updatePreviousModel(context)

            RequestSender.sendSubscriptionRequest(context, model, RetryCounterManager.counterId, callback)
        }
    }
    fun createRegisterEmailRequest(context: Context, registerEmailModel: RelatedDigitalModel) {
        if (Build.VERSION.SDK_INT < Constants.SDK_MIN_API_VERSION) {
            Log.e(LOG_TAG, "RelatedDigital SDK requires min API level 21!")
            return
        }

        if (registerEmailModel.getToken().isEmpty() || (registerEmailModel.getGoogleAppAlias().isEmpty() && registerEmailModel.getHuaweiAppAlias().isEmpty()) ) {
            Log.e(LOG_TAG, "token or appKey cannot be null!")
            return
        }

        RequestSender.sendSubscriptionRequest(context, registerEmailModel, RetryCounterManager.counterId, null)
    }


    fun createRetentionRequest(
        context: Context, type: RetentionType,
        pushId: String?, emPushSp: String?
    ) {
        if (Build.VERSION.SDK_INT < Constants.SDK_MIN_API_VERSION) {
            Log.e(LOG_TAG, "RelatedDigital SDK requires min API level 21!")
            return
        }

        val latestPushId: String = when(type) {
            RetentionType.DELIVER, RetentionType.SILENT -> {
                latestDeliverPushId
            } else -> {
                latestOpenPushId
            }
        }

        if(!pushId.isNullOrEmpty() && pushId != latestPushId) {
            when (type) {
                RetentionType.DELIVER, RetentionType.SILENT -> {
                    latestDeliverPushId = pushId
                }
                else -> {
                    latestOpenPushId = pushId
                }
            }

            val retention = Retention()

            if(GoogleUtils.checkPlayService(context)) {
                retention.key = RelatedDigital.getRelatedDigitalModel(context).getGoogleAppAlias()
            } else {
                retention.key = RelatedDigital.getRelatedDigitalModel(context).getHuaweiAppAlias()
            }

            retention.pushId = pushId

            when (type) {
                RetentionType.DELIVER -> {
                    retention.status = "D"
                    retention.deliver = 1
                }
                RetentionType.SILENT -> {
                    retention.status = "S"
                    retention.deliver = 1
                }
                else -> {
                    retention.status = "O"
                    retention.deliver = 0
                }
            }

            retention.token = RelatedDigital.getRelatedDigitalModel(context).getToken()
            retention.actionBtn = 0
            retention.isMobile = 1

            if(!emPushSp.isNullOrEmpty()) {
                retention.emPushSp = emPushSp
            }

            RequestSender.sendRetentionRequest(context, retention, RetryCounterManager.counterId)
        }
    }*/
}