package com.relateddigital.relateddigital_android.network

import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.RelatedDigitalModel
import com.relateddigital.relateddigital_android.util.GoogleUtils
import com.relateddigital.relateddigital_android.util.PersistentTargetManager
import com.relateddigital.relateddigital_android.util.SharedPref
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

object RequestFormer {
    private const val LOG_TAG = "RequestFormer"

    private var mNrv = 0
    private var mPviv = 0
    private var mTvc = 0
    private var mLvt: String? = null

    fun formLoggerRequest(
        context: Context, model: RelatedDigitalModel?, pageName: String,
        properties: HashMap<String, String>?,
        queryMap: HashMap<String, String>, headerMap: HashMap<String, String>
    ) {
        fillCommonParameters(context, model, pageName, properties, queryMap, headerMap)
    }

    fun formInAppNotificationRequest(
        context: Context, model: RelatedDigitalModel?, pageName: String,
        properties: HashMap<String, String>?,
        queryMap: HashMap<String, String>, headerMap: HashMap<String, String>
    ) {
        fillCommonParameters(context, model, pageName, properties, queryMap, headerMap)
        addInAppNotificationExtraParameters(model, queryMap)
    }

    fun formInAppActionRequest(
        context: Context, model: RelatedDigitalModel?, pageName: String,
        properties: HashMap<String, String>?,
        queryMap: HashMap<String, String>, headerMap: HashMap<String, String>
    ) {
        fillCommonParameters(context, model, pageName, properties, queryMap, headerMap)
        addInAppActionExtraParameters(model, queryMap)
    }

    fun formInAppNotificationClickRequest(
            context: Context, model: RelatedDigitalModel?, pageName: String,
            properties: HashMap<String, String>?,
            queryMap: HashMap<String, String>, headerMap: HashMap<String, String>
    ) {
        fillCommonParameters(context, model, pageName, properties, queryMap, headerMap)
        addInAppNotificationClickExtraParameters(model, queryMap)
    }

    fun formStoryImpressionClickRequest(
        context: Context, model: RelatedDigitalModel?, pageName: String,
        properties: HashMap<String, String>?,
        queryMap: HashMap<String, String>, headerMap: HashMap<String, String>
    ) {
        fillCommonParameters(context, model, pageName, properties, queryMap, headerMap)
    }

    fun formSubJsonRequest(
        context: Context, model: RelatedDigitalModel?, pageName: String,
        properties: HashMap<String, String>?,
        queryMap: HashMap<String, String>, headerMap: HashMap<String, String>
    ) {
        fillCommonParameters(context, model, pageName, properties, queryMap, headerMap)
    }

    fun formSpinToWinPromoCodeRequest(
            context: Context, model: RelatedDigitalModel?, pageName: String,
            properties: HashMap<String, String>?,
            queryMap: HashMap<String, String>, headerMap: HashMap<String, String>
    ) {
        fillCommonParameters(context, model, pageName, properties, queryMap, headerMap)
    }

    fun formStoryActionRequest(
        context: Context, model: RelatedDigitalModel?, pageName: String,
        properties: HashMap<String, String>?,
        queryMap: HashMap<String, String>, headerMap: HashMap<String, String>
    ) {
        fillCommonParameters(context, model, pageName, properties, queryMap, headerMap)
        addInAppNotificationExtraParameters(model, queryMap)
    }

    fun formRecommendationRequest(
            context: Context, model: RelatedDigitalModel?, pageName: String,
            properties: HashMap<String, String>?,
            queryMap: HashMap<String, String>, headerMap: HashMap<String, String>
    ) {
        fillCommonParameters(context, model, pageName, properties, queryMap, headerMap)
    }

    fun formFavsResponseRequest(
            context: Context, model: RelatedDigitalModel?, pageName: String,
            properties: HashMap<String, String>?,
            queryMap: HashMap<String, String>, headerMap: HashMap<String, String>
    ) {
        fillCommonParameters(context, model, pageName, properties, queryMap, headerMap)
        addInAppNotificationExtraParameters(model, queryMap)
    }

    fun formGeofenceGetListResponseRequest(
        context: Context, model: RelatedDigitalModel?, pageName: String,
        properties: HashMap<String, String>?,
        queryMap: HashMap<String, String>, headerMap: HashMap<String, String>,
        latitude: Double, longitude: Double
    ) {
        fillCommonParameters(context, model, pageName, properties, queryMap, headerMap)
        addGeofenceGetListExtraParameters(context, model, queryMap, latitude, longitude)
    }

    fun formGeofenceTriggerRequest(
        context: Context, model: RelatedDigitalModel?, pageName: String,
        properties: HashMap<String, String>?,
        queryMap: HashMap<String, String>, headerMap: HashMap<String, String>,
        latitude: Double, longitude: Double, actId: String, geoId: String
    ) {
        fillCommonParameters(context, model, pageName, properties, queryMap, headerMap)
        addGeofenceTriggerExtraParameters(context, model, queryMap, latitude, longitude, actId, geoId)
    }

    fun updateSessionParameters(context: Context, pageName: String) {
        val dateNow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val lastEventTime: String = SharedPref.readString(
            context,
            Constants.LAST_EVENT_TIME_KEY, ""
        )
        if (lastEventTime.isEmpty()) {
            mNrv = 1
            mPviv = 1
            mTvc = 1
            mLvt = dateNow
            SharedPref.writeString(context, Constants.TVC_KEY, "1")
            SharedPref.writeString(context, Constants.LAST_EVENT_TIME_KEY, dateNow)
        } else {
            if (isPreviousSessionOver(lastEventTime, dateNow)) {
                mPviv = 1
                SharedPref.writeString(context, Constants.PVIV_KEY, mPviv.toString())
                val prevTvc: Int = SharedPref.readString(
                    context, Constants.TVC_KEY,
                    "1"
                ).toInt()
                mTvc = prevTvc + 1
                SharedPref.writeString(context, Constants.TVC_KEY, mTvc.toString())
                if (pageName != "/OM_evt.gif") {
                    mLvt = dateNow
                    SharedPref.writeString(context, Constants.LAST_EVENT_TIME_KEY, dateNow)
                } else {
                    mLvt = lastEventTime
                }
            } else {
                if (pageName != "/OM_evt.gif") {
                    val prevPviv: Int = SharedPref.readString(
                        context,
                        Constants.PVIV_KEY, "1"
                    ).toInt()
                    mPviv = prevPviv + 1
                    SharedPref.writeString(context, Constants.PVIV_KEY, mPviv.toString())
                } else {
                    mPviv = SharedPref.readString(
                        context, Constants.PVIV_KEY,
                        "1"
                    ).toInt()
                }
                mTvc = SharedPref.readString(context, Constants.TVC_KEY, "1").toInt()
                mLvt = lastEventTime
            }
            mNrv = if (mTvc > 1) {
                0
            } else {
                1
            }
        }
    }

    private fun isPreviousSessionOver(lastEventTime: String, dateNow: String): Boolean {
        val res: Boolean
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        res = try {
            val previousEventDate = simpleDateFormat.parse(lastEventTime)
            val currentEventDate = simpleDateFormat.parse(dateNow)
            val differenceInMs = currentEventDate!!.time - previousEventDate!!.time
            differenceInMs > 1800000 // 30 mins
        } catch (e: ParseException) {
            e.printStackTrace()
            false
        }
        return res
    }

    private fun fillCommonParameters(
        context: Context, model: RelatedDigitalModel?, pageName: String,
        properties: HashMap<String, String>?,
        queryMap: HashMap<String, String>, headerMap: HashMap<String, String>
    ) {
        if (properties != null) {
            if (properties.containsKey(Constants.COOKIE_ID_REQUEST_KEY)) {
                model!!.setCookieId(context, properties[Constants.COOKIE_ID_REQUEST_KEY]!!)
                properties.remove(Constants.COOKIE_ID_REQUEST_KEY)
            }
            if (properties.containsKey(Constants.EXVISITOR_ID_REQUEST_KEY)) {
                if (model!!.getExVisitorId().isNotEmpty() && model.getExVisitorId() !=
                    properties[Constants.EXVISITOR_ID_REQUEST_KEY]
                ) {
                    model.setCookieId(context, null)
                }
                model.setExVisitorId(
                    context,
                    properties[Constants.EXVISITOR_ID_REQUEST_KEY]!!,
                    false
                )
                properties.remove(Constants.EXVISITOR_ID_REQUEST_KEY)
            }
            if (properties.containsKey(Constants.TOKEN_ID_REQUEST_KEY)) {
                if (properties[Constants.TOKEN_ID_REQUEST_KEY] != null) {
                    model!!.setToken(context, properties[Constants.TOKEN_ID_REQUEST_KEY]!!)
                }
                properties.remove(Constants.TOKEN_ID_REQUEST_KEY)
            }
            if (properties.containsKey(Constants.APP_ID_REQUEST_KEY)) {
                if (properties[Constants.APP_ID_REQUEST_KEY] != null) {
                    if (GoogleUtils.checkPlayService(context)) {
                        model!!.setGoogleAppAlias(
                            context,
                            properties[Constants.APP_ID_REQUEST_KEY]!!
                        )
                    } else {
                        model!!.setHuaweiAppAlias(
                            context,
                            properties[Constants.APP_ID_REQUEST_KEY]!!
                        )
                    }
                }
                properties.remove(Constants.APP_ID_REQUEST_KEY)
            }
        }

        try {
            PersistentTargetManager.saveParameters(context, properties)
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
        }

        val timeOfEvent = System.currentTimeMillis() / 1000

        queryMap[Constants.ORGANIZATION_ID_REQUEST_KEY] = model!!.getOrganizationId()
        queryMap[Constants.SITE_ID_REQUEST_KEY] = model.getProfileId()
        queryMap[Constants.DATE_REQUEST_KEY] = timeOfEvent.toString()
        queryMap[Constants.URI_REQUEST_KEY] = pageName
        queryMap[Constants.COOKIE_ID_REQUEST_KEY] = model.getCookieId()!!
        queryMap[Constants.CHANNEL_REQUEST_KEY] = model.getOsType()
        queryMap[Constants.MAPPL_REQUEST_KEY] = "true"
        queryMap[Constants.APP_VERSION_REQUEST_KEY] = model.getAppVersion()
        queryMap[Constants.NOTIFICATION_PERMISSION_REQUEST_KEY] = model.getPushPermissionStatus()
        queryMap[Constants.API_VERSION_REQUEST_KEY] = model.getApiVersion()
        queryMap[Constants.SDK_VERSION_REQUEST_KEY] = model.getSdkVersion()
        queryMap[Constants.NRV_REQUEST_KEY] = mNrv.toString()
        queryMap[Constants.PVIV_REQUEST_KEY] = mPviv.toString()
        queryMap[Constants.TVC_REQUEST_KEY] = mTvc.toString()
        queryMap[Constants.LVT_REQUEST_KEY] = mLvt.toString()

        if (properties != null) {
            for (i in properties.keys.indices) {
                val key = properties.keys.toTypedArray()[i]
                if(properties[key] != null) {
                    queryMap[key] = properties[key]!!
                }
            }
        }

        if (model.getAdvertisingIdentifier().isNotEmpty()) {
            queryMap[Constants.ADVERTISER_ID_REQUEST_KEY] = model.getAdvertisingIdentifier()
        }

        if (model.getExVisitorId().isNotEmpty()) {
            queryMap[Constants.EXVISITOR_ID_REQUEST_KEY] = model.getExVisitorId()
        }

        if (model.getToken().isNotEmpty()) {
            queryMap[Constants.TOKEN_ID_REQUEST_KEY] = model.getToken()
        }

        if (model.getGoogleAppAlias().isNotEmpty() || model.getHuaweiAppAlias().isNotEmpty()) {
            if (GoogleUtils.checkPlayService(context)) {
                queryMap[Constants.APP_ID_REQUEST_KEY] = model.getGoogleAppAlias()
            } else {
                queryMap[Constants.APP_ID_REQUEST_KEY] = model.getHuaweiAppAlias()
            }
        }

        if (model.getUserAgent().isNotEmpty()) {
            headerMap[Constants.USER_AGENT_REQUEST_KEY] = model.getUserAgent()
        }

        if (model.getCookie() != null) {
            val loadBalanceCookieKey = model.getCookie()!!.getLoggerCookieKey()
            val loadBalanceCookieValue = model.getCookie()!!.getLoggerCookieValue()
            val Om3rdCookieValue = model.getCookie()!!.getLoggerOM3rdCookieValue()
            var cookieString = ""
            if (loadBalanceCookieKey.isNotEmpty() && loadBalanceCookieValue.isNotEmpty()) {
                cookieString = "$loadBalanceCookieKey=$loadBalanceCookieValue"
            }
            if (Om3rdCookieValue.isNotEmpty()) {
                if (cookieString != "") {
                    cookieString = "$cookieString;"
                }
                cookieString = cookieString + Constants.OM_3_REQUEST_KEY + "=" + Om3rdCookieValue
            }
            if (cookieString != "") {
                headerMap["Cookie"] = cookieString
            }
        }
    }

    private fun addInAppNotificationExtraParameters(
        model: RelatedDigitalModel?, queryMap: HashMap<String, String>
    ) {
        if(model!!.getVisitorData().isNotEmpty()) {
            queryMap[Constants.VISITOR_DATA_REQUEST_KEY] = model.getVisitorData()
        }

        if(model.getVisitData().isNotEmpty()) {
            queryMap[Constants.VISIT_DATA_REQUEST_KEY] = model.getVisitData()
        }
    }

    private fun addInAppActionExtraParameters(
        model: RelatedDigitalModel?, queryMap: HashMap<String, String>
    ) {
        if(model!!.getVisitorData().isNotEmpty()) {
            queryMap[Constants.VISITOR_DATA_REQUEST_KEY] = model.getVisitorData()
        }

        if(model.getVisitData().isNotEmpty()) {
            queryMap[Constants.VISIT_DATA_REQUEST_KEY] = model.getVisitData()
        }

        queryMap[Constants.REQUEST_ACTION_TYPE_KEY] = Constants.REQUEST_ACTION_TYPE_VAL
    }

    private fun addInAppNotificationClickExtraParameters(
            model: RelatedDigitalModel?, queryMap: HashMap<String, String>
    ) {
        queryMap[Constants.DOMAIN_REQUEST_KEY] = model!!.getDataSource() + "_Android"
    }

    private fun addGeofenceGetListExtraParameters(
        context: Context, model: RelatedDigitalModel?, queryMap: HashMap<String, String>,
        latitude: Double, longitude: Double
    ) {
        val df = DecimalFormat("0.0000000000000")

        if (latitude > 0) {
            val latitudeString: String = df.format(latitude)
            queryMap[Constants.GEOFENCE_LATITUDE_KEY] = latitudeString
        }

        if (longitude > 0) {
            val longitudeString: String = df.format(longitude)
            queryMap[Constants.GEOFENCE_LONGITUDE_KEY] = longitudeString
        }

        queryMap[Constants.GEOFENCE_ACT_KEY] = Constants.GEOFENCE_ACT_VALUE

        if(!model!!.getToken().isNullOrEmpty()) {
            queryMap[Constants.TOKEN_ID_REQUEST_KEY] = model.getToken()
        }

        if (GoogleUtils.checkPlayService(context)) {
            if(!model.getGoogleAppAlias().isNullOrEmpty()) {
                queryMap[Constants.APP_ID_REQUEST_KEY] = model.getGoogleAppAlias()
            }
        } else {
            if(!model.getHuaweiAppAlias().isNullOrEmpty()) {
                queryMap[Constants.APP_ID_REQUEST_KEY] = model.getHuaweiAppAlias()
            }
        }

    }

    private fun addGeofenceTriggerExtraParameters(
        context: Context, model: RelatedDigitalModel?, queryMap: HashMap<String, String>,
        latitude: Double, longitude: Double, actId: String, geoId: String
    ) {
        val df = DecimalFormat("0.0000000000000")

        if (latitude > 0) {
            val latitudeString: String = df.format(latitude)
            queryMap[Constants.GEOFENCE_LATITUDE_KEY] = latitudeString
        }

        if (longitude > 0) {
            val longitudeString: String = df.format(longitude)
            queryMap[Constants.GEOFENCE_LONGITUDE_KEY] = longitudeString
        }

        queryMap[Constants.GEOFENCE_ACT_KEY] = Constants.GEOFENCE_PROCESS_VALUE

        queryMap[Constants.GEOFENCE_ACT_ID_KEY] = actId

        queryMap[Constants.GEOFENCE_GEO_ID_KEY] = geoId

        if(!model!!.getToken().isNullOrEmpty()) {
            queryMap[Constants.TOKEN_ID_REQUEST_KEY] = model.getToken()
        }

        if (GoogleUtils.checkPlayService(context)) {
            if(!model.getGoogleAppAlias().isNullOrEmpty()) {
                queryMap[Constants.APP_ID_REQUEST_KEY] = model.getGoogleAppAlias()
            }
        } else {
            if(!model.getHuaweiAppAlias().isNullOrEmpty()) {
                queryMap[Constants.APP_ID_REQUEST_KEY] = model.getHuaweiAppAlias()
            }
        }
    }
}