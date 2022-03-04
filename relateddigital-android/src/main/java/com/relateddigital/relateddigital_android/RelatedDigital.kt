package com.relateddigital.relateddigital_android

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.appTracker.AppTracker
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.geofence.GeofenceStarter
import com.relateddigital.relateddigital_android.inapp.InAppButtonInterface
import com.relateddigital.relateddigital_android.inapp.VisilabsCallback
import com.relateddigital.relateddigital_android.locationPermission.LocationPermissionHandler
import com.relateddigital.relateddigital_android.model.*
import com.relateddigital.relateddigital_android.network.RequestFormer
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.push.EuromessageCallback
import com.relateddigital.relateddigital_android.push.PushMessageInterface
import com.relateddigital.relateddigital_android.push.RetentionType
import com.relateddigital.relateddigital_android.recommendation.VisilabsTargetFilter
import com.relateddigital.relateddigital_android.remoteConfig.RemoteConfigHelper
import com.relateddigital.relateddigital_android.util.*
import org.json.JSONObject
import java.util.*


object RelatedDigital {
    private var model: RelatedDigitalModel? = null
    private var inAppButtonInterface: InAppButtonInterface? = null
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null
    private const val LOG_TAG: String = "RelatedDigital"
    private var previousModel: RelatedDigitalModel? = null

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

        if(model!!.getIsGeofenceEnabled()) {
            GeofenceStarter.startGpsManager(context)
        }

        initVisilabsParameters()

        createRemoteConfigJob(context)
    }

    private fun createInitialModel(context: Context): RelatedDigitalModel {
        return RelatedDigitalModel(
            organizationId = "",
            profileId = "",
            dataSource = "",
            appVersion = AppUtils.getAppVersion(context),
            pushPermissionStatus = AppUtils.getNotificationPermissionStatus(context),
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

    fun getRelatedDigitalModel(context: Context): RelatedDigitalModel {
        if(model == null) {
            model = createInitialModel(context)

            val modelStr = SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY, "")

            if (modelStr.isNotEmpty()) {
                model!!.fill(Gson().fromJson(modelStr, RelatedDigitalModel::class.java))
            }
        }
        return model!!
    }

    @JvmStatic
    fun setIsPushNotificationEnabled(
        context: Context,
        isPushNotificationEnabled: Boolean,
        googleAppAlias: String,
        huaweiAppAlias: String,
        token: String,
        notificationSmallIcon: Int = 0,
        notificationSmallIconDarkMode: Int = 0,
        isNotificationLargeIcon: Boolean = false,
        notificationLargeIcon: Int = 0,
        notificationLargeIconDarkMode: Int = 0,
        notificationPushIntent: String = "",
        notificationChannelName: String = "",
        notificationColor: String = ""
    ) {
        if (model != null) {
            model!!.setIsPushNotificationEnabled(context, isPushNotificationEnabled)
            model!!.setGoogleAppAlias(context, googleAppAlias)
            model!!.setHuaweiAppAlias(context, huaweiAppAlias)
            model!!.setToken(context, token)
        } else {
            if (SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNotEmpty()) {
                model = Gson().fromJson(
                    SharedPref.readString(
                        context,
                        Constants.RELATED_DIGITAL_MODEL_KEY
                    ), RelatedDigitalModel::class.java
                )
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

        if(notificationSmallIcon != 0) {
            if (AppUtils.isIconResourceAvailable(
                    context,
                    notificationSmallIcon
                )
            ) {
                SharedPref.writeInt(
                    context,
                    Constants.NOTIFICATION_TRANSPARENT_SMALL_ICON,
                    notificationSmallIcon
                )
            } else {
                Log.i(LOG_TAG, "Resource (notification small icon) could not be found" +
                        " : $notificationSmallIcon")
            }
        }

        if(notificationSmallIconDarkMode != 0) {
            if (AppUtils.isIconResourceAvailable(
                    context,
                    notificationSmallIconDarkMode
                )
            ) {
                SharedPref.writeInt(
                    context,
                    Constants.NOTIFICATION_TRANSPARENT_SMALL_ICON_DARK_MODE,
                    notificationSmallIconDarkMode
                )
            } else {
                Log.i(LOG_TAG, "Resource (notification small icon dark mode) could not be found" +
                        " : $notificationSmallIconDarkMode")
            }
        }

        SharedPref.writeBoolean(
            context,
            Constants.NOTIFICATION_USE_LARGE_ICON,
            isNotificationLargeIcon
        )

        if(notificationLargeIcon != 0) {
            if (AppUtils.isIconResourceAvailable(
                    context,
                    notificationLargeIcon
                )
            ) {
                SharedPref.writeInt(
                    context,
                    Constants.NOTIFICATION_LARGE_ICON,
                    notificationLargeIcon
                )
            } else {
                Log.i(LOG_TAG, "Resource (notification large icon) could not be found" +
                        " : $notificationLargeIcon")
            }
        }

        if(notificationLargeIconDarkMode != 0) {
            if (AppUtils.isIconResourceAvailable(
                    context,
                    notificationLargeIconDarkMode
                )
            ) {
                SharedPref.writeInt(
                    context,
                    Constants.NOTIFICATION_LARGE_ICON_DARK_MODE,
                    notificationLargeIconDarkMode
                )
            } else {
                Log.i(LOG_TAG, "Resource (notification large icon dark mode) could not be found" +
                        " : $notificationLargeIconDarkMode")
            }
        }

        if(notificationPushIntent.isNotEmpty()) {
            SharedPref.writeString(
                context,
                Constants.INTENT_NAME,
                notificationPushIntent
            )
        }

        if(notificationChannelName.isNotEmpty()) {
            SharedPref.writeString(
                context,
                Constants.CHANNEL_NAME,
                notificationChannelName
            )
        }

        if(notificationColor.isNotEmpty()) {
            SharedPref.writeString(
                context,
                Constants.NOTIFICATION_COLOR,
                notificationColor
            )
        }

        registerToFCM(context)

        if(model!!.getPushPermissionStatus() == "granted") {
            model!!.add(context, "pushPermit", "Y")
        } else {
            model!!.add(context, "pushPermit", "N")
        }

        sync(context)
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

        if(isGeofenceEnabled) {
            GeofenceStarter.startGpsManager(context)
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
    fun getPreviousModel(): RelatedDigitalModel? {
        return previousModel
    }

    @JvmStatic
    fun updatePreviousModel(context: Context) {
        previousModel = RelatedDigitalModel(
            organizationId = "",
            profileId = "",
            dataSource = "",
            appVersion = AppUtils.getAppVersion(context),
            pushPermissionStatus = AppUtils.getNotificationPermissionStatus(context),
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
        previousModel!!.copyFrom(context, model!!)
    }

    @JvmStatic
    fun signUp(context: Context, exVisitorId: String, properties: HashMap<String, String>? = null,
              parent: Activity? = null) {
        if (StringUtils.isNullOrWhiteSpace(exVisitorId)) {
            Log.e(LOG_TAG,
                "Attempted to use null or empty exVisitorID. Ignoring."
            )
        } else {
            if (properties == null) {
                val propertiesLoc = HashMap<String, String>()
                propertiesLoc[Constants.EXVISITOR_ID_REQUEST_KEY] = exVisitorId
                propertiesLoc[Constants.SIGN_UP_REQUEST_KEY] = exVisitorId
                propertiesLoc[Constants.B_SIGN_UP_KEY_REQUEST_KEY] = "SignUp"
                customEvent(context, "SignUpPage", propertiesLoc, parent)
            } else {
                properties[Constants.EXVISITOR_ID_REQUEST_KEY] = exVisitorId
                properties[Constants.SIGN_UP_REQUEST_KEY] = exVisitorId
                properties[Constants.B_SIGN_UP_KEY_REQUEST_KEY] = "SignUp"
                customEvent(context, "SignUpPage", properties, parent)
            }
        }
    }

    @JvmStatic
    fun login(context: Context, exVisitorId: String, properties: HashMap<String, String>? = null,
              parent: Activity? = null) {
        if (StringUtils.isNullOrWhiteSpace(exVisitorId)) {
            Log.e(LOG_TAG,
                "Attempted to use null or empty exVisitorID. Ignoring."
            )
        } else {
            if (properties == null) {
                val propertiesLoc = HashMap<String, String>()
                propertiesLoc[Constants.EXVISITOR_ID_REQUEST_KEY] = exVisitorId
                propertiesLoc[Constants.LOGIN_REQUEST_KEY] = exVisitorId
                propertiesLoc[Constants.B_LOGIN_KEY_REQUEST_KEY] = "Login"
                customEvent(context, "LoginPage", propertiesLoc, parent)
            } else {
                properties[Constants.EXVISITOR_ID_REQUEST_KEY] = exVisitorId
                properties[Constants.LOGIN_REQUEST_KEY] = exVisitorId
                properties[Constants.B_LOGIN_KEY_REQUEST_KEY] = "Login"
                customEvent(context, "LoginPage", properties, parent)
            }
        }
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
    fun sendPushNotificationOpenReport(context: Context, message: Message) {
        if (model!!.getIsPushNotificationEnabled()) {
            RequestHandler.createRetentionRequest(
                context, RetentionType.OPEN,
                message.pushId, message.emPushSp
            )
        } else {
            Log.e(LOG_TAG, "Push notification is not enabled." +
                    "Call RelatedDigital.setIsPushNotificationEnabled() first")
        }
    }

    @JvmStatic
    fun sendCampaignParameters(context: Context, properties: HashMap<String, String>) {
        if(properties.isEmpty()) {
            Log.e(LOG_TAG, "The map cannot be empty!")
            return
        } else {
            customEvent(context, Constants.PAGE_NAME_REQUEST_VAL, properties)
        }
    }

    @JvmStatic
    fun trackRecommendationClick(context: Context, qs: String?) {
        if (model != null) {
            if (model!!.getIsInAppNotificationEnabled()) {
                val parameters = HashMap<String, String>()
                if (!qs.isNullOrEmpty()) {
                    val tempMultiQuery = qs.split("&".toRegex()).toTypedArray()
                    for (s in tempMultiQuery) {
                        val tempQueryString = s.split("=".toRegex(), 2).toTypedArray()
                        if (tempQueryString.size == 2) {
                            parameters[tempQueryString[0]] = tempQueryString[1]
                        }
                    }
                }
                RequestHandler.createLoggerRequest(
                    context, model!!, Constants.PAGE_NAME_REQUEST_VAL,
                    parameters
                )
            } else {
                Log.e(
                    LOG_TAG, "In-app notification is not enabled." +
                            "Call RelatedDigital.setIsInAppNotificationEnabled() first"
                )
            }
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
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

        if(model != null) {
            if (model!!.getIsInAppNotificationEnabled()) {
                if(parent != null) {
                    RequestHandler.createInAppNotificationRequest(
                        context,
                        model!!,
                        pageName,
                        properties,
                        parent
                    )
                    RequestHandler.createInAppActionRequest(
                        context,
                        model!!,
                        pageName,
                        properties,
                        parent
                    )
                }
            } else {
                Log.e(
                    LOG_TAG, "In-app notification is not enabled." +
                            "Call RelatedDigital.setIsInAppNotificationEnabled() first"
                )
            }
            RequestHandler.createLoggerRequest(context, model!!, pageName, properties)
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun sync(context: Context, callback: EuromessageCallback? = null) {
        if(model != null) {
            if(model!!.getIsPushNotificationEnabled()) {
                RequestHandler.createSyncRequest(context, callback)
            } else {
                Log.e(
                    LOG_TAG, "Push notification is not enabled." +
                            "Call RelatedDigital.setIsPushNotificationEnabled() first"
                )
            }
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    private fun syncForRegisterEmail(context: Context, registerEmailModel: RelatedDigitalModel) {
        RequestHandler.createRegisterEmailRequest(context, registerEmailModel)
    }

    @JvmStatic
    fun setEmailPermit(context: Context, emailPermit: EmailPermit) {
        if(model != null) {
            if(emailPermit == EmailPermit.ACTIVE) {
                model!!.add(context, Constants.EMAIL_PERMIT_KEY, "Y")
            } else {
                model!!.add(context, Constants.EMAIL_PERMIT_KEY, "X")
            }
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun setGsmPermit(context: Context, gsmPermit: GsmPermit) {
        if(model != null) {
            if(gsmPermit == GsmPermit.ACTIVE) {
                model!!.add(context, Constants.GSM_PERMIT_KEY, "Y")
            } else {
                model!!.add(context, Constants.GSM_PERMIT_KEY, "X")
            }
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun setTwitterId(context: Context, twitterId: String) {
        if(model != null) {
            model!!.add(context, Constants.TWITTER_KEY, twitterId)
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun setEmail(context: Context, email: String) {
        if(model != null) {
            model!!.add(context, Constants.EMAIL_KEY, email)
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun setFacebookId(context: Context, facebookId: String) {
        if(model != null) {
            model!!.add(context, Constants.FACEBOOK_KEY, facebookId)
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun setRelatedDigitalUserId(context: Context, relatedDigitalUserId: String) {
        if(model != null) {
            model!!.add(context, Constants.RELATED_DIGITAL_USER_KEY, relatedDigitalUserId)
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun setPhoneNumber(context: Context, msisdn: String) {
        if(model != null) {
            model!!.add(context, Constants.MSISDN_KEY, msisdn)
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun setUserProperty(context: Context, key: String, value: String) {
        if(model != null) {
            model!!.add(context, key, value)
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun removeUserProperty(context: Context, key: String) {
        if(model != null) {
            model!!.remove(context, key)
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun removeUserProperties(context: Context) {
        if(model != null) {
            model!!.removeAll(context)
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun registerEmail(
        context: Context, email: String, emailPermit: EmailPermit,
        isCommercial: Boolean, callback: EuromessageCallback? = null
    ) {
        if(model != null) {
            if(model!!.getIsPushNotificationEnabled()) {
                setEmail(context, email)
                setEmailPermit(context, emailPermit)
                val registerEmailModel = createInitialModel(context)
                registerEmailModel.copyFrom(context, model!!)
                registerEmailModel.addWithoutSaving(Constants.CONSENT_SOURCE_KEY, Constants.CONSENT_SOURCE_VALUE)
                if (isCommercial) {
                    registerEmailModel.addWithoutSaving(
                        Constants.RECIPIENT_TYPE_KEY,
                        Constants.RECIPIENT_TYPE_TACIR
                    )
                } else {
                    registerEmailModel.addWithoutSaving(
                        Constants.RECIPIENT_TYPE_KEY,
                        Constants.RECIPIENT_TYPE_BIREYSEL
                    )
                }

                registerEmailModel.addWithoutSaving(
                    Constants.CONSENT_TIME_KEY,
                    AppUtils.getCurrentTurkeyDateString() as String
                )

                syncForRegisterEmail(context, registerEmailModel)
            } else {
                Log.e(
                    LOG_TAG, "Push notification is not enabled." +
                            "Call RelatedDigital.setIsPushNotificationEnabled() first"
                )
            }
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    private fun registerToFCM(context: Context?) {
        FirebaseApp.initializeApp(context!!)
    }

    /**
     * This method returns the list of push messages sent in the last 30 days.
     * The messages are ordered in terms of their timestamps e.g. most recent one is at index 0.
     * activity : Activity
     * callback : PushMessageInterface
     */
    @JvmStatic
    fun getPushMessages(activity: Activity, callback: PushMessageInterface) {
        object : Thread(Runnable {
            val payloads: String = SharedPref.readString(
                activity.applicationContext,
                Constants.PAYLOAD_SP_KEY
            )
            if (payloads.isNotEmpty()) {
                try {
                    val pushMessages: MutableList<Message> = ArrayList<Message>()
                    val jsonObject = JSONObject(payloads)
                    val jsonArray = jsonObject.getJSONArray(Constants.PAYLOAD_SP_ARRAY_KEY)
                    for (i in 0 until jsonArray.length()) {
                        val currentObject = jsonArray.getJSONObject(i)
                        val currentMessage: Message = Gson().fromJson(
                            currentObject.toString(),
                            Message::class.java
                        )
                        pushMessages.add(currentMessage)
                    }
                    val orderedPushMessages: List<Message> = PayloadUtils.orderPushMessages(
                        activity.applicationContext,
                        pushMessages
                    )
                    activity.runOnUiThread { callback.success(orderedPushMessages) }
                } catch (e: Exception) {
                    SharedPref.writeString(
                        activity.applicationContext,
                        Constants.PAYLOAD_SP_KEY,
                        ""
                    )
                    val element = Throwable().stackTrace[0]
                    LogUtils.formGraylogModel(
                        activity.applicationContext,
                        "e",
                        "De-serializing JSON string of push message : " + e.message,
                        element.className + "/" + element.methodName + "/" + element.lineNumber
                    )
                    activity.runOnUiThread { callback.fail(e.message!!) }
                }
            } else {
                activity.runOnUiThread {
                    callback.fail(
                        "There is not any push notification sent " +
                                "(or saved) in the last 30 days"
                    )
                }
            }
        }) {}.start()
    }

    /**
     * This method is used to send the list of the
     * applications installed from a store in the device to the server.
     * With Android 11, to get the list of the apps installed
     * in the device, you have 2 options:
     * 1-) You can add the package names of the applications
     * that you are interested in into the AndroidManifest.xml file
     * like below:
     * <manifest package="com.example.myApp">
     * <queries>
     * <package android:name="com.example.app1"></package>
     * <package android:name="com.example.app2"></package>
    </queries> *
     * ...
    </manifest> *
     * 2-) You can add the permission below to the
     * AndroidManifest.xml files like below:
     * <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" tools:ignore="QueryAllPackagesPermission"></uses-permission>
     *
     * For the 2nd method: Google might expect you to
     * explain why you need this permission when you upload
     * the app to Play Store.
     * https://developer.android.com/training/basics/intents/package-visibility
     */
    @JvmStatic
    fun sendTheListOfAppsInstalled(context: Context) {
        if(model != null) {
            AppTracker.sendTheListOfAppsInstalled(context)
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun sendLocationPermission(context: Context) {
        if(model != null) {
            LocationPermissionHandler.sendLocationPermissionToTheServer(context)
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun getFavorites(
        context: Context,
        actionId: String? = null,
        actionType: String? = null,
        visilabsCallback: VisilabsCallback,
        properties: HashMap<String, String>? = null
    ) {
        if (model != null) {
            if (model!!.getIsInAppNotificationEnabled()) {
                RequestHandler.createFavsResponseRequest(
                    context, actionId,
                    actionType, visilabsCallback, properties)
            } else {
                Log.e(
                    LOG_TAG, "In-app notification is not enabled." +
                            "Call RelatedDigital.setIsInAppNotificationEnabled() first"
                )
            }
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
    }

    @JvmStatic
    fun getRecommendations(
        context: Context,
        zoneId: String,
        productCode: String,
        visilabsCallback: VisilabsCallback,
        properties: HashMap<String, String>? = null,
        filters: List<VisilabsTargetFilter>? = null,
    ) {
        if (model != null) {
            if (model!!.getIsInAppNotificationEnabled()) {
                RequestHandler.createRecommendationRequest(context, zoneId,
                    productCode, visilabsCallback, properties, filters)
            } else {
                Log.e(
                    LOG_TAG, "In-app notification is not enabled." +
                            "Call RelatedDigital.setIsInAppNotificationEnabled() first"
                )
            }
        } else {
            Log.e(LOG_TAG, "Call RelatedDigital.init() first")
        }
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

    private fun createRemoteConfigJob(context: Context) {
        mHandler = Handler(Looper.getMainLooper())
        mRunnable = object : Runnable {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun run() {
                RemoteConfigHelper.checkRemoteConfigs(context)
                val myProcess = RunningAppProcessInfo()
                ActivityManager.getMyMemoryState(myProcess)
                if (myProcess.importance != RunningAppProcessInfo.IMPORTANCE_GONE) {
                    mHandler!!.postDelayed(this, 600000) // 10-min
                } else {
                    mHandler!!.removeCallbacks(mRunnable!!)
                }
            }
        }
        mHandler!!.post(mRunnable!!)
    }

    fun isBlocked(context: Context): Boolean {
        val isBlocked: String = SharedPref.readString(context, Constants.REMOTE_CONFIG_BLOCK_PREF_KEY,
                "f")
        return isBlocked == "t"
    }
}