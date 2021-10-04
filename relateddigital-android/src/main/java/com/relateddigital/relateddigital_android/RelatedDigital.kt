package com.relateddigital.relateddigital_android

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.inapp.InAppButtonInterface
import com.relateddigital.relateddigital_android.model.LoadBalanceCookie
import com.relateddigital.relateddigital_android.model.RelatedDigitalModel
import com.relateddigital.relateddigital_android.model.VisilabsParameter
import com.relateddigital.relateddigital_android.network.RequestFormer
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.SharedPref
import java.util.*

object RelatedDigital {
    private var model: RelatedDigitalModel? = null
    private var inAppButtonInterface: InAppButtonInterface? = null
    private const val LOG_TAG: String = "RelatedDigital"

    @JvmStatic
    fun init(context: Context,
             organizationId: String,
             profileId: String,
             dataSource: String) {

        model = createInitialModel(context)

        val modelStr = SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY, "")

        if (modelStr.isNotEmpty()) {
            model!!.fill(Gson().fromJson(modelStr, RelatedDigitalModel::class.java))
        }

        model!!.setOrganizationId(context, organizationId)
        model!!.setProfileId(context, profileId)
        model!!.setDataSource(context, dataSource)

        model!!.setCookie(context, LoadBalanceCookie())
        if (model!!.getCookieId().isNullOrEmpty()) {
            model!!.setCookieId(context, null)
        }

        initVisilabsParameters()
    }

    private fun createInitialModel(context: Context): RelatedDigitalModel {
        return RelatedDigitalModel(
                organizationId = "",
                profileId = "",
                dataSource = "",
                appVersion = AppUtils.getAppVersion(context),
                osType = AppUtils.getOsType(),
                osVersion = AppUtils.getOsVersion(),
                sdkVersion = AppUtils.getSdkVersion(),
                deviceType = AppUtils.getDeviceType(),
                deviceName = AppUtils.getDeviceName(),
                carrier = AppUtils.getCarrier(context),
                identifierForVendor = AppUtils.getIdentifierForVendor(context),
                local = AppUtils.getLocal(context),
                userAgent = AppUtils.getUserAgent(),
                cookieId = AppUtils.getCookieId(context),
                visitorData = "",
                visitData = ""
        )
    }

    fun getRelatedDigitalModel(): RelatedDigitalModel? {
        return model
    }

    @JvmStatic
    fun setIsPushNotificationEnabled(context: Context,
                                     isPushNotificationEnabled: Boolean,
                                     googleAppAlias: String,
                                     huaweiAppAlias: String,
                                     token: String) {
        if (model != null) {
            model!!.setIsPushNotificationEnabled(context, isPushNotificationEnabled)
            model!!.setGoogleAppAlias(context, googleAppAlias)
            model!!.setHuaweiAppAlias(context, huaweiAppAlias)
            model!!.setToken(context, token)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setIsPushNotificationEnabled(context, isPushNotificationEnabled)
                model!!.setGoogleAppAlias(context, googleAppAlias)
                model!!.setHuaweiAppAlias(context, huaweiAppAlias)
                model!!.setToken(context, token)
            } else {
                model = createInitialModel(context)
                model!!.setGoogleAppAlias(context, googleAppAlias)
                model!!.setHuaweiAppAlias(context, huaweiAppAlias)
                model!!.setToken(context, token)
            }
        }
    }

    @JvmStatic
    fun setIsInAppNotificationEnabled(context: Context, isInAppNotificationEnabled: Boolean) {
        if (model != null) {
            model!!.setIsInAppNotificationEnabled(context, isInAppNotificationEnabled)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setIsInAppNotificationEnabled(context, isInAppNotificationEnabled)
            } else {
                model = createInitialModel(context)
                model!!.setIsInAppNotificationEnabled(context, isInAppNotificationEnabled)
            }
        }
    }

    @JvmStatic
    fun setIsGeofenceEnabled(context: Context, isGeofenceEnabled: Boolean) {
        if (model != null) {
            model!!.setIsGeofenceEnabled(context, isGeofenceEnabled)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setIsGeofenceEnabled(context, isGeofenceEnabled)
            } else {
                model = createInitialModel(context)
                model!!.setIsGeofenceEnabled(context, isGeofenceEnabled)
            }
        }
    }

    @JvmStatic
    fun setGoogleAppAlias(context: Context, googleAppAlias: String) {
        if (model != null) {
            model!!.setGoogleAppAlias(context, googleAppAlias)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setGoogleAppAlias(context, googleAppAlias)
            } else {
                model = createInitialModel(context)
                model!!.setGoogleAppAlias(context, googleAppAlias)
            }
        }
    }

    @JvmStatic
    fun setHuaweiAppAlias(context: Context, huaweiAppAlias: String) {
        if (model != null) {
            model!!.setHuaweiAppAlias(context, huaweiAppAlias)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setHuaweiAppAlias(context, huaweiAppAlias)
            } else {
                model = createInitialModel(context)
                model!!.setHuaweiAppAlias(context, huaweiAppAlias)
            }
        }
    }

    @JvmStatic
    fun setOrganizationId(context: Context, organizationId: String) {
        if (model != null) {
            model!!.setOrganizationId(context, organizationId)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setOrganizationId(context, organizationId)
            } else {
                model = createInitialModel(context)
                model!!.setOrganizationId(context, organizationId)
            }
        }
    }

    @JvmStatic
    fun setProfileId(context: Context, profileId: String) {
        if (model != null) {
            model!!.setProfileId(context, profileId)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setProfileId(context, profileId)
            } else {
                model = createInitialModel(context)
                model!!.setProfileId(context, profileId)
            }
        }
    }

    @JvmStatic
    fun setDataSource(context: Context, dataSource: String) {
        if (model != null) {
            model!!.setDataSource(context, dataSource)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setDataSource(context, dataSource)
            } else {
                model = createInitialModel(context)
                model!!.setDataSource(context, dataSource)
            }
        }
    }

    @JvmStatic
    fun setRequestTimeoutInSecond(context: Context, requestTimeoutInSecond: Int) {
        if (model != null) {
            model!!.setRequestTimeoutInSecond(context, requestTimeoutInSecond)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setRequestTimeoutInSecond(context, requestTimeoutInSecond)
            } else {
                model = createInitialModel(context)
                model!!.setRequestTimeoutInSecond(context, requestTimeoutInSecond)
            }
        }
    }

    @JvmStatic
    fun setMaxGeofenceCount(context: Context, maxGeofenceCount: Int) {
        if (model != null) {
            model!!.setMaxGeofenceCount(context, maxGeofenceCount)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setMaxGeofenceCount(context, maxGeofenceCount)
            } else {
                model = createInitialModel(context)
                model!!.setMaxGeofenceCount(context, maxGeofenceCount)
            }
        }
    }

    @JvmStatic
    fun setAdvertisingIdentifier(context: Context, advertisingIdentifier: String) {
        if (model != null) {
            model!!.setAdvertisingIdentifier(context, advertisingIdentifier)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setAdvertisingIdentifier(context, advertisingIdentifier)
            } else {
                model = createInitialModel(context)
                model!!.setAdvertisingIdentifier(context, advertisingIdentifier)
            }
        }
    }

    @JvmStatic
    fun setExVisitorId(context: Context, exVisitorId: String) {
        if (model != null) {
            model!!.setExVisitorId(context, exVisitorId, false)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setExVisitorId(context, exVisitorId, false)
            } else {
                model = createInitialModel(context)
                model!!.setExVisitorId(context, exVisitorId, false)
            }
        }
    }

    @JvmStatic
    fun setToken(context: Context, token: String) {
        if (model != null) {
            model!!.setToken(context, token)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setToken(context, token)
            } else {
                model = createInitialModel(context)
                model!!.setToken(context, token)
            }
        }
    }

    @JvmStatic
    fun setInAppButtonInterface(inAppButtonInterface: InAppButtonInterface?) {
        this.inAppButtonInterface = inAppButtonInterface
    }

    @JvmStatic
    fun clearCookieId(context: Context) {
        if (model != null) {
            model!!.setCookieId(context, "")
        }
        AppUtils.clearCookieId(context)
    }

    @JvmStatic
    fun getIsPushNotificationEnabled(context: Context): Boolean {
        return if (model != null) {
            model!!.getIsPushNotificationEnabled()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getIsPushNotificationEnabled()
            } else {
                Log.e(LOG_TAG, "isPushNotificationEnabled has never been set before!!")
                false
            }
        }
    }

    @JvmStatic
    fun getIsInAppNotificationEnabled(context: Context): Boolean {
        return if (model != null) {
            model!!.getIsInAppNotificationEnabled()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getIsInAppNotificationEnabled()
            } else {
                Log.e(LOG_TAG, "isInAppNotificationEnabled has never been set before!!")
                false
            }
        }
    }

    @JvmStatic
    fun getIsGeofenceEnabled(context: Context): Boolean {
        return if (model != null) {
            model!!.getIsGeofenceEnabled()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getIsGeofenceEnabled()
            } else {
                Log.e(LOG_TAG, "isGeofenceEnabled has never been set before!!")
                false
            }
        }
    }

    @JvmStatic
    fun getGoogleAppAlias(context: Context): String {
        return if (model != null) {
            model!!.getGoogleAppAlias()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getGoogleAppAlias()
            } else {
                Log.e(LOG_TAG, "googleAppAlias has never been set before!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getHuaweiAppAlias(context: Context): String {
        return if (model != null) {
            model!!.getHuaweiAppAlias()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getHuaweiAppAlias()
            } else {
                Log.e(LOG_TAG, "huaweiAppAlias has never been set before!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getOrganizationId(context: Context): String {
        return if (model != null) {
            model!!.getOrganizationId()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getOrganizationId()
            } else {
                Log.e(LOG_TAG, "organizationId has never been set before!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getProfileId(context: Context): String {
        return if (model != null) {
            model!!.getProfileId()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getProfileId()
            } else {
                Log.e(LOG_TAG, "profileId has never been set before!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getDataSource(context: Context): String {
        return if (model != null) {
            model!!.getDataSource()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getDataSource()
            } else {
                Log.e(LOG_TAG, "dataSource has never been set before!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getRequestTimeoutInSecond(context: Context): Int {
        return if (model != null) {
            model!!.getRequestTimeoutInSecond()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getRequestTimeoutInSecond()
            } else {
                Log.e(LOG_TAG, "requestTimeoutInSecond has never been set before!!")
                30
            }
        }
    }

    @JvmStatic
    fun getMaxGeofenceCount(context: Context): Int {
        return if (model != null) {
            model!!.getMaxGeofenceCount()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getMaxGeofenceCount()
            } else {
                Log.e(LOG_TAG, "maxGeofenceCount has never been set before!!")
                100
            }
        }
    }

    @JvmStatic
    fun getAppVersion(context: Context): String {
        return if (model != null) {
            model!!.getAppVersion()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getAppVersion()
            } else {
                AppUtils.getAppVersion(context)
            }
        }
    }

    @JvmStatic
    fun getOsType(context: Context): String {
        return if (model != null) {
            model!!.getOsType()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getOsType()
            } else {
                AppUtils.getOsType()
            }
        }
    }

    @JvmStatic
    fun getOsVersion(context: Context): String {
        return if (model != null) {
            model!!.getOsVersion()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getOsVersion()
            } else {
                AppUtils.getOsVersion()
            }
        }
    }

    @JvmStatic
    fun getSdkVersion(context: Context): String {
        return if (model != null) {
            model!!.getSdkVersion()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getSdkVersion()
            } else {
                AppUtils.getSdkVersion()
            }
        }
    }

    @JvmStatic
    fun getDeviceType(context: Context): String {
        return if (model != null) {
            model!!.getDeviceType()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getDeviceType()
            } else {
                AppUtils.getDeviceType()
            }
        }
    }

    @JvmStatic
    fun getDeviceName(context: Context): String {
        return if (model != null) {
            model!!.getDeviceName()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getDeviceName()
            } else {
                AppUtils.getDeviceName()
            }
        }
    }

    @JvmStatic
    fun getCarrier(context: Context): String {
        return if (model != null) {
            model!!.getCarrier()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getCarrier()
            } else {
                AppUtils.getCarrier(context)
            }
        }
    }

    @JvmStatic
    fun getIdentifierForVendor(context: Context): String {
        return if (model != null) {
            model!!.getIdentifierForVendor()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getIdentifierForVendor()
            } else {
                AppUtils.getIdentifierForVendor(context)
            }
        }
    }

    @JvmStatic
    fun getAdvertisingIdentifier(context: Context): String {
        return if (model != null) {
            model!!.getAdvertisingIdentifier()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getAdvertisingIdentifier()
            } else {
                Log.e(LOG_TAG, "advertisingIdentifier has never been set before!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getLocal(context: Context): String {
        return if (model != null) {
            model!!.getLocal()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getLocal()
            } else {
                AppUtils.getLocal(context)
            }
        }
    }

    @JvmStatic
    fun getExVisitorId(context: Context): String {
        return if (model != null) {
            model!!.getExVisitorId()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getExVisitorId()
            } else {
                Log.e(LOG_TAG, "exVisitorId has never been set before!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getToken(context: Context): String {
        return if (model != null) {
            model!!.getToken()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getToken()
            } else {
                Log.e(LOG_TAG, "token has never been set before!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getCookieId(context: Context): String? {
        return if (model != null) {
            model!!.getCookieId()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getCookieId()
            } else {
                Log.e(LOG_TAG, "no cookieID is available!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getUserAgent(context: Context): String {
        return if (model != null) {
            model!!.getUserAgent()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getUserAgent()
            } else {
                AppUtils.getUserAgent()
            }
        }
    }

    @JvmStatic
    fun getVisitorData(context: Context): String {
        return if (model != null) {
            model!!.getVisitorData()
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getVisitorData()
            } else {
                Log.e(LOG_TAG, "No visitor data gotten yet!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getInAppButtonInterface(): InAppButtonInterface? {
        return this.inAppButtonInterface
    }

    @JvmStatic
    fun logout(context: Context) {
        if (model != null) {
            model!!.setExVisitorId(context, "", true)
            model!!.setCookieId(context, null)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setExVisitorId(context, "", true)
                model!!.setCookieId(context, null)
            } else {
                model = createInitialModel(context)
                model!!.setExVisitorId(context, "", true)
                model!!.setCookieId(context, null)
            }
        }
    }

    @JvmStatic
    fun customEvent(context: Context, pageName: String, properties: HashMap<String, String>?,
                    parent: Activity? = null) {
        if (pageName.isEmpty()) {
            Log.e(LOG_TAG, "pageName cannot be empty!!!")
            return
        }

        if (Build.VERSION.SDK_INT < Constants.SDK_MIN_API_VERSION) {
            Log.e(LOG_TAG, "Related Digital SDK requires min API level 21!")
            return
        }

        RequestFormer.updateSessionParameters(context, pageName)

        if (model!!.getIsInAppNotificationEnabled()) {
            RequestHandler.createInAppNotificationRequest(context, model!!, pageName, properties, parent)
            RequestHandler.createInAppActionRequest(context, model!!, pageName, properties, parent)
        }
        RequestHandler.createLoggerRequest(context, model!!, pageName, properties)
    }

    private fun initVisilabsParameters() {
        val visilabsParameters = ArrayList<VisilabsParameter>()
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_VOSS_KEY,
                Constants.TARGET_PREF_VOSS_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_VCNAME_KEY,
                Constants.TARGET_PREF_VCNAME_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_VCMEDIUM_KEY,
                Constants.TARGET_PREF_VCMEDIUM_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_VCSOURCE_KEY,
                Constants.TARGET_PREF_VCSOURCE_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_VSEG1_KEY,
                Constants.TARGET_PREF_VSEG1_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_VSEG2_KEY,
                Constants.TARGET_PREF_VSEG2_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_VSEG3_KEY,
                Constants.TARGET_PREF_VSEG3_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_VSEG4_KEY,
                Constants.TARGET_PREF_VSEG4_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_VSEG5_KEY,
                Constants.TARGET_PREF_VSEG5_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_BD_KEY,
                Constants.TARGET_PREF_BD_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_GN_KEY,
                Constants.TARGET_PREF_GN_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_LOC_KEY,
                Constants.TARGET_PREF_LOC_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_VPV_KEY,
                Constants.TARGET_PREF_VPV_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_LPVS_KEY,
                Constants.TARGET_PREF_LPVS_STORE_KEY, 10, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_LPP_KEY,
                Constants.TARGET_PREF_LPP_STORE_KEY,
                1, object : ArrayList<String>() {
            init {
                add(Constants.TARGET_PREF_PPR_KEY)
            }
        }))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_VQ_KEY,
                Constants.TARGET_PREF_VQ_STORE_KEY, 1, null))
        visilabsParameters.add(VisilabsParameter(Constants.TARGET_PREF_VRDOMAIN_KEY,
                Constants.TARGET_PREF_VRDOMAIN_STORE_KEY, 1, null))
        Constants.VISILABS_PARAMETERS = visilabsParameters
    }
}