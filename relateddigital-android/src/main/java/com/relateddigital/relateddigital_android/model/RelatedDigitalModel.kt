package com.relateddigital.relateddigital_android.model

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.GoogleUtils
import com.relateddigital.relateddigital_android.util.PersistentTargetManager
import com.relateddigital.relateddigital_android.util.SharedPref
import java.io.Serializable
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class RelatedDigitalModel(
        private var isPushNotificationEnabled: Boolean = false,
        private var isInAppNotificationEnabled: Boolean = false,
        private var isGeofenceEnabled: Boolean = false,
        private var googleAppAlias: String = "",
        private var huaweiAppAlias: String = "",
        private var organizationId: String,
        private var profileId: String,
        private var dataSource: String,
        private var requestTimeoutInSecond: Int = 30,
        private var maxGeofenceCount: Int = 100,
        private var appVersion: String,
        private var pushPermissionStatus: String,
        private var apiVersion: String = "Android",
        private var osType: String = "ANDROID",
        private var osVersion: String,
        private var sdkVersion: String,
        private var deviceType: String,
        private var deviceName: String,
        private var carrier: String,
        private var identifierForVendor: String,
        private var advertisingIdentifier: String = "",
        private var local: String,
        private var exVisitorId: String = "",
        private var token: String = "",
        private var cookieId: String?,
        private var userAgent: String,
        private var visitorData: String,
        private var visitData: String,
        private var cookie: LoadBalanceCookie? = null,
        private var extra: HashMap<String, Any> = HashMap()
) : Serializable {

    companion object {
        private const val LOG_TAG = "RelatedDigitalModel"
    }

    fun setIsPushNotificationEnabled(context: Context, isPushNotificationEnabled: Boolean) {
        this.isPushNotificationEnabled = isPushNotificationEnabled
        saveToSharedPrefs(context)
    }

    fun setIsInAppNotificationEnabled(context: Context, isInAppNotificationEnabled: Boolean) {
        this.isInAppNotificationEnabled = isInAppNotificationEnabled
        saveToSharedPrefs(context)
    }

    fun setIsGeofenceEnabled(context: Context, isGeofenceEnabled: Boolean) {
        this.isGeofenceEnabled = isGeofenceEnabled
        saveToSharedPrefs(context)
    }

    fun setGoogleAppAlias(context: Context, googleAppAlias: String) {
        this.googleAppAlias = googleAppAlias
        saveToSharedPrefs(context)
    }

    fun setHuaweiAppAlias(context: Context, huaweiAppAlias: String) {
        this.huaweiAppAlias = huaweiAppAlias
        saveToSharedPrefs(context)
    }

    fun setOrganizationId(context: Context, organizationId: String) {
        this.organizationId = organizationId
        saveToSharedPrefs(context)
    }

    fun setProfileId(context: Context, profileId: String) {
        this.profileId = profileId
        saveToSharedPrefs(context)
    }

    fun setDataSource(context: Context, dataSource: String) {
        this.dataSource = dataSource
        saveToSharedPrefs(context)
    }

    fun setRequestTimeoutInSecond(context: Context, requestTimeoutInSecond: Int) {
        this.requestTimeoutInSecond = requestTimeoutInSecond
        saveToSharedPrefs(context)
    }

    fun setMaxGeofenceCount(context: Context, maxGeofenceCount: Int) {
        this.maxGeofenceCount = maxGeofenceCount
        saveToSharedPrefs(context)
    }

    fun setAppVersion(context: Context, appVersion: String) {
        this.appVersion = appVersion
        saveToSharedPrefs(context)
    }

    fun setPushPermissionStatus(context: Context, pushPermissionStatus: String) {
        this.pushPermissionStatus = pushPermissionStatus
        saveToSharedPrefs(context)
    }

    fun setOsType(context: Context, osType: String) {
        this.osType = osType
        saveToSharedPrefs(context)
    }

    fun setOsVersion(context: Context, osVersion: String) {
        this.osVersion = osVersion
        saveToSharedPrefs(context)
    }

    fun setSdkVersion(context: Context, sdkVersion: String) {
        this.sdkVersion = sdkVersion
        saveToSharedPrefs(context)
    }

    fun setDeviceType(context: Context, deviceType: String) {
        this.deviceType = deviceType
        saveToSharedPrefs(context)
    }

    fun setDeviceName(context: Context, deviceName: String) {
        this.deviceName = deviceName
        saveToSharedPrefs(context)
    }

    fun setCarrier(context: Context, carrier: String) {
        this.carrier = carrier
        saveToSharedPrefs(context)
    }

    fun setIdentifierForVendor(context: Context, identifierForVendor: String) {
        this.identifierForVendor = identifierForVendor
        saveToSharedPrefs(context)
    }

    fun setAdvertisingIdentifier(context: Context, advertisingIdentifier: String) {
        this.advertisingIdentifier = advertisingIdentifier
        saveToSharedPrefs(context)
    }

    fun setLocal(context: Context, local: String) {
        this.local = local
        saveToSharedPrefs(context)
    }

    fun setExVisitorId(context: Context, exVisitorId: String, isLogout: Boolean) {
        if(exVisitorId.isEmpty() && !isLogout) {
            Log.w(LOG_TAG, "exVisitorId cannot be empty!!")
            return
        }

        val previousExVisitorId = this.exVisitorId

        if(exVisitorId.isNotEmpty() && exVisitorId != previousExVisitorId &&
                previousExVisitorId.isNotEmpty() ) {
            setCookieId(context, null)
        }

        this.exVisitorId = exVisitorId

        if (previousExVisitorId.isNotEmpty() && previousExVisitorId != this.exVisitorId) {
            PersistentTargetManager.clearParameters(context)
        }
        saveToSharedPrefs(context)
    }

    fun setToken(context: Context, token: String) {
        this.token = token
        saveToSharedPrefs(context)
    }

    fun setCookieId(context: Context, cookieId: String?) {
        val previousCookieID = this.cookieId
        if (cookieId.isNullOrEmpty()) {
            this.cookieId = UUID.randomUUID().toString()
        } else {
            this.cookieId = cookieId
        }

        if (previousCookieID != null && previousCookieID != this.cookieId) {
            PersistentTargetManager.clearParameters(context)
        }

        saveToSharedPrefs(context)
    }

    fun setUserAgent(context: Context, userAgent: String) {
        this.userAgent = userAgent
        saveToSharedPrefs(context)
    }

    fun setVisitorData(context: Context, visitorData: String) {
        this.visitorData = visitorData
        saveToSharedPrefs(context)
    }

    fun setVisitData(context: Context, visitData: String) {
        this.visitData = visitData
    }

    fun setCookie(context: Context, cookie: LoadBalanceCookie) {
        this.cookie = cookie
    }

    fun add(context: Context, key: String, value: Any) {
        extra[key] = value
        saveToSharedPrefs(context)
    }

    fun addAll(context: Context, extras: Map<String, Any>) {
        extra.putAll(extras)
        saveToSharedPrefs(context)
    }

    fun removeAll(context: Context) {
        extra.clear()
        saveToSharedPrefs(context)
    }

    fun getIsPushNotificationEnabled(): Boolean {
        return isPushNotificationEnabled
    }

    fun getIsInAppNotificationEnabled(): Boolean {
        return isInAppNotificationEnabled
    }

    fun getIsGeofenceEnabled(): Boolean {
        return isGeofenceEnabled
    }

    fun getGoogleAppAlias(): String {
        return googleAppAlias
    }

    fun getHuaweiAppAlias(): String {
        return huaweiAppAlias
    }

    fun getOrganizationId(): String {
        return organizationId
    }

    fun getProfileId(): String {
        return profileId
    }

    fun getDataSource(): String {
        return dataSource
    }

    fun getRequestTimeoutInSecond(): Int {
        return requestTimeoutInSecond
    }

    fun getMaxGeofenceCount(): Int {
        return maxGeofenceCount
    }

    fun getAppVersion(): String {
        return appVersion
    }

    fun getPushPermissionStatus(): String {
        return pushPermissionStatus
    }

    fun getApiVersion(): String {
        return apiVersion
    }

    fun getOsType(): String {
        return osType
    }

    fun getOsVersion(): String {
        return osVersion
    }

    fun getSdkVersion(): String {
        return sdkVersion
    }

    fun getDeviceType(): String {
        return deviceType
    }

    fun getDeviceName(): String {
        return deviceName
    }

    fun getCarrier(): String {
        return carrier
    }

    fun getIdentifierForVendor(): String {
        return identifierForVendor
    }

    fun getAdvertisingIdentifier(): String {
        return advertisingIdentifier
    }

    fun getLocal(): String {
        return local
    }

    fun getExVisitorId(): String {
        return exVisitorId
    }

    fun getToken(): String {
        return token
    }

    fun getCookieId(): String? {
        return cookieId
    }

    fun getUserAgent(): String {
        return userAgent
    }

    fun getVisitorData(): String {
        return visitorData
    }

    fun getVisitData(): String {
        return visitData
    }

    fun getCookie(): LoadBalanceCookie? {
        return cookie
    }

    fun getExtra(): HashMap<String, Any> {
        return extra
    }

    private fun saveToSharedPrefs(context: Context) {
        SharedPref.writeString(context,
                Constants.RELATED_DIGITAL_MODEL_KEY,
                Gson().toJson(this))
    }

    fun getFromSharedPref(context: Context): RelatedDigitalModel {
        return Gson().fromJson(SharedPref.readString(context,
                Constants.RELATED_DIGITAL_MODEL_KEY), this::class.java)
    }

    fun fill(model: RelatedDigitalModel) {
        if(model.getIsPushNotificationEnabled() != null) {
            this.isPushNotificationEnabled = model.getIsPushNotificationEnabled()
        }
        if(model.getIsInAppNotificationEnabled() != null) {
            this.isInAppNotificationEnabled = model.getIsInAppNotificationEnabled()
        }
        if(model.getIsGeofenceEnabled() != null) {
            this.isGeofenceEnabled = model.getIsGeofenceEnabled()
        }
        if(model.getGoogleAppAlias() != null) {
            this.googleAppAlias = model.getGoogleAppAlias()
        }
        if(model.getHuaweiAppAlias() != null) {
            this.huaweiAppAlias = model.getHuaweiAppAlias()
        }
        if(model.getOrganizationId() != null) {
            this.organizationId = model.getOrganizationId()
        }
        if(model.getProfileId() != null) {
            this.profileId = model.getProfileId()
        }
        if(model.getDataSource() != null) {
            this.dataSource = model.getDataSource()
        }
        if(model.getRequestTimeoutInSecond() != null) {
            this.requestTimeoutInSecond = model.getRequestTimeoutInSecond()
        }
        if(model.getMaxGeofenceCount() != null) {
            this.maxGeofenceCount = model.getMaxGeofenceCount()
        }
        if(model.getAdvertisingIdentifier() != null) {
            this.advertisingIdentifier = model.getAdvertisingIdentifier()
        }
        if(model.getExVisitorId() != null) {
            this.exVisitorId = model.getExVisitorId()
        }
        if(model.getToken() != null) {
            this.token = model.getToken()
        }
        if(model.getVisitorData() != null) {
            this.visitorData = model.getVisitorData()
        }
        if(!model.getExtra().isNullOrEmpty()) {
            this.extra.clear()
            this.extra.putAll(model.getExtra())
        }
    }

    fun isValid(context: Context): Boolean {
        var appAlias = ""
        appAlias = if(GoogleUtils.checkPlayService(context)) {
            googleAppAlias
        } else {
            huaweiAppAlias
        }
        val res1 = !(TextUtils.isEmpty(getToken()) && TextUtils.isEmpty(appAlias))
        var res2 = true
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val dateNow = dateFormat.format(Calendar.getInstance().time)
        val lastSubsTime: String =
            SharedPref.readString(context, Constants.LAST_SUBS_DATE_KEY)
        if (lastSubsTime.isNotEmpty()) {
            if (!AppUtils.isDateDifferenceGreaterThan(dateNow, lastSubsTime, 3)) {
                val lastSubStr: String =
                    SharedPref.readString(context, Constants.LAST_SUBS_KEY)
                if (lastSubStr.isNotEmpty()) {
                    try {
                        val lastSubscription: RelatedDigitalModel =
                            Gson().fromJson(
                                lastSubStr,
                                RelatedDigitalModel::class.java
                            )
                        if (isEqual(lastSubscription)) {
                            res2 = false
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        SharedPref.writeString(context, Constants.LAST_SUBS_KEY, "")
                    }
                }
            }
        }
        return res1 and res2
    }

    fun isEqual(previousModel: RelatedDigitalModel?): Boolean {
        val result: Boolean = if (previousModel == null) {
            false
        } else {
            isStringEqual(googleAppAlias, previousModel.getGoogleAppAlias()) &&
                    isStringEqual(huaweiAppAlias, previousModel.getHuaweiAppAlias()) &&
                    isStringEqual(appVersion, previousModel.getAppVersion()) &&
                    isStringEqual(pushPermissionStatus, previousModel.getPushPermissionStatus()) &&
                    isStringEqual(apiVersion, previousModel.getApiVersion()) &&
                    isStringEqual(osType, previousModel.getOsType()) &&
                    isStringEqual(osVersion, previousModel.getOsVersion()) &&
                    isStringEqual(sdkVersion, previousModel.getSdkVersion()) &&
                    isStringEqual(deviceType, previousModel.getDeviceType()) &&
                    isStringEqual(deviceName, previousModel.getDeviceName()) &&
                    isStringEqual(carrier, previousModel.getCarrier()) &&
                    isStringEqual(local, previousModel.getLocal()) &&
                    isStringEqual(
                        identifierForVendor,
                        previousModel.getIdentifierForVendor()
                    ) &&
                    isStringEqual(
                        advertisingIdentifier,
                        previousModel.getAdvertisingIdentifier()
                    ) &&
                    isStringEqual(
                        getToken(),
                        previousModel.getToken()
                    ) &&
                    isMapEqual(extra, previousModel.getExtra())
        }
        return result
    }

    private fun isStringEqual(first: String?, second: String?): Boolean {
        val result: Boolean = if (first == null || second == null) {
            first == null && second == null
        } else {
            first == second
        }
        return result
    }

    private fun isMapEqual(first: Map<String, Any>, second: Map<String, Any>): Boolean {
        var result = true
        if (first.size != second.size) {
            result = false
        } else {
            for (i in first.keys.toTypedArray().indices) {
                val key = first.keys.toTypedArray()[i]
                if (key == Constants.EURO_CONSENT_TIME_KEY) {
                    continue
                }
                if (!second.containsKey(key)) {
                    result = false
                    break
                } else {
                    val value1 = first[key] as String?
                    val value2 = second[key] as String?
                    if (value1 == null || value2 == null) {
                        if (!(value1 == null && value2 == null)) {
                            result = false
                            break
                        }
                    } else {
                        if (value1 != value2) {
                            result = false
                            break
                        }
                    }
                }
            }
        }
        return result
    }

    fun copyFrom(context: Context, fromModel: RelatedDigitalModel) {
        isPushNotificationEnabled = fromModel.getIsPushNotificationEnabled()
        isInAppNotificationEnabled = fromModel.getIsInAppNotificationEnabled()
        isGeofenceEnabled = fromModel.getIsGeofenceEnabled()
        googleAppAlias = fromModel.getGoogleAppAlias()
        huaweiAppAlias = fromModel.getHuaweiAppAlias()
        organizationId = fromModel.getOrganizationId()
        profileId = fromModel.getProfileId()
        dataSource = fromModel.getDataSource()
        requestTimeoutInSecond = fromModel.getRequestTimeoutInSecond()
        maxGeofenceCount = fromModel.getMaxGeofenceCount()
        appVersion = if(fromModel.getAppVersion().isNullOrEmpty()) {
            AppUtils.getAppVersion(context)
        } else {
            fromModel.getAppVersion()
        }
        pushPermissionStatus = if(fromModel.getPushPermissionStatus().isNullOrEmpty()) {
            AppUtils.getNotificationPermissionStatus(context)
        } else {
            fromModel.getPushPermissionStatus()
        }
        apiVersion = fromModel.getApiVersion()
        osType = fromModel.getOsType()
        osVersion = if(fromModel.getOsVersion().isNullOrEmpty()) {
            AppUtils.getOsVersion()
        } else {
            fromModel.getOsVersion()
        }
        sdkVersion = if(fromModel.getSdkVersion().isNullOrEmpty()) {
            AppUtils.getSdkVersion()
        } else {
            fromModel.getSdkVersion()
        }
        deviceType = if(fromModel.getDeviceType().isNullOrEmpty()) {
            AppUtils.getDeviceType()
        } else {
            fromModel.getDeviceType()
        }
        deviceName = if(fromModel.getDeviceName().isNullOrEmpty()) {
            AppUtils.getDeviceName()
        } else {
            fromModel.getDeviceName()
        }
        carrier = if(fromModel.getCarrier().isNullOrEmpty()) {
            AppUtils.getCarrier(context)
        } else {
            fromModel.getCarrier()
        }
        identifierForVendor = if(fromModel.getIdentifierForVendor().isNullOrEmpty()) {
            AppUtils.getIdentifierForVendor(context)
        } else {
            fromModel.getIdentifierForVendor()
        }
        advertisingIdentifier = fromModel.getAdvertisingIdentifier()
        local = if(fromModel.getLocal().isNullOrEmpty()) {
            AppUtils.getLocal(context)
        } else {
            fromModel.getLocal()
        }
        exVisitorId = fromModel.getExVisitorId()
        token = fromModel.getToken()
        cookieId = if(fromModel.getCookieId().isNullOrEmpty()) {
            AppUtils.getCookieId(context)
        } else {
            fromModel.getCookieId()
        }
        userAgent = if(fromModel.getUserAgent().isNullOrEmpty()) {
            AppUtils.getUserAgent()
        } else {
            fromModel.getUserAgent()
        }
        visitorData = if(fromModel.getVisitorData().isNullOrEmpty()) {
            ""
        } else {
            fromModel.getVisitorData()
        }
        visitData = if(fromModel.getVisitData().isNullOrEmpty()) {
            ""
        } else {
            fromModel.getVisitData()
        }
        cookie = if(fromModel.getCookie() == null) {
            null
        } else {
            fromModel.getCookie()
        }
        extra = HashMap<String, Any>()
        for (i in fromModel.getExtra().keys.toTypedArray().indices) {
            val key = fromModel.getExtra().keys.toTypedArray()[i] as String
            extra[key] = fromModel.getExtra()[key]!!
        }
    }
}