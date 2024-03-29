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
        private var geofencingIntervalInMinute: Int = 15,
        private var appVersion: String,
        private var pushPermissionStatus: String,
        private var apiVersion: String = "Android",
        private var osType: String = "ANDROID",
        private var osVersion: String,
        private var sdkVersion: String,
        private var sdkType: String,
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
        private var extra: HashMap<String, Any> = HashMap(),
        private var utmCampaign: String?,
        private var utmContent: String?,
        private var utmMedium: String?,
        private var utmSource: String?,
        private var utmTerm: String?
) : Serializable {

    companion object {
        private const val LOG_TAG = "RelatedDigitalModel"
        private val googleAppAliasLock = Any()
        private val huaweiAppAliasLock = Any()
    }

    fun setIsPushNotificationEnabled(context: Context, isPushNotificationEnabled: Boolean) {
        synchronized(this) {
            this.isPushNotificationEnabled = isPushNotificationEnabled
            saveToSharedPrefs(context)
        }
    }

    fun setIsInAppNotificationEnabled(context: Context, isInAppNotificationEnabled: Boolean) {
        synchronized(this) {
            this.isInAppNotificationEnabled = isInAppNotificationEnabled
            saveToSharedPrefs(context)
        }
    }

    fun setIsGeofenceEnabled(context: Context, isGeofenceEnabled: Boolean) {
        synchronized(this) {
            this.isGeofenceEnabled = isGeofenceEnabled
            saveToSharedPrefs(context)
        }
    }

    fun setGoogleAppAlias(context: Context, googleAppAlias: String) {
        synchronized(this) {
            this.googleAppAlias = googleAppAlias
            saveToSharedPrefs(context)
        }
    }

    fun setHuaweiAppAlias(context: Context, huaweiAppAlias: String) {
        synchronized(this) {
            this.huaweiAppAlias = huaweiAppAlias
            saveToSharedPrefs(context)
        }
    }

    fun setOrganizationId(context: Context, organizationId: String) {
        synchronized(this) {
            this.organizationId = organizationId
            saveToSharedPrefs(context)
        }
    }

    fun setProfileId(context: Context, profileId: String) {
        synchronized(this) {
            this.profileId = profileId
            saveToSharedPrefs(context)
        }
    }

    fun setDataSource(context: Context, dataSource: String) {
        synchronized(this) {
            this.dataSource = dataSource
            saveToSharedPrefs(context)
        }
    }

    fun setRequestTimeoutInSecond(context: Context, requestTimeoutInSecond: Int) {
        synchronized(this) {
            this.requestTimeoutInSecond = requestTimeoutInSecond
            saveToSharedPrefs(context)
        }
    }

    fun setMaxGeofenceCount(context: Context, maxGeofenceCount: Int) {
        synchronized(this) {
            this.maxGeofenceCount = maxGeofenceCount
            saveToSharedPrefs(context)
        }
    }

    fun setGeofencingIntervalInMinute(context: Context, geofencingIntervalInMinute: Int) {
        synchronized(this) {
            this.geofencingIntervalInMinute = geofencingIntervalInMinute
            saveToSharedPrefs(context)
        }
    }

    fun setAppVersion(context: Context, appVersion: String) {
        synchronized(this) {
            this.appVersion = appVersion
            saveToSharedPrefs(context)
        }
    }

    fun setPushPermissionStatus(context: Context, pushPermissionStatus: String) {
        synchronized(this) {
            this.pushPermissionStatus = pushPermissionStatus
            saveToSharedPrefs(context)
        }
    }

    fun setOsType(context: Context, osType: String) {
        synchronized(this) {
            this.osType = osType
            saveToSharedPrefs(context)
        }
    }

    fun setOsVersion(context: Context, osVersion: String) {
        synchronized(this) {
            this.osVersion = osVersion
            saveToSharedPrefs(context)
        }
    }

    fun setSdkVersion(context: Context, sdkVersion: String) {
        synchronized(this) {
            this.sdkVersion = sdkVersion
            saveToSharedPrefs(context)
        }
    }

    fun setSdkType(context: Context, sdkType: String = "native") {
        synchronized(this) {
            this.sdkType = sdkType
            saveToSharedPrefs(context)
        }
    }

    fun setDeviceType(context: Context, deviceType: String) {
        synchronized(this) {
            this.deviceType = deviceType
            saveToSharedPrefs(context)
        }
    }

    fun setDeviceName(context: Context, deviceName: String) {
        synchronized(this) {
            this.deviceName = deviceName
            saveToSharedPrefs(context)
        }
    }

    fun setCarrier(context: Context, carrier: String) {
        synchronized(this) {
            this.carrier = carrier
            saveToSharedPrefs(context)
        }
    }

    fun setIdentifierForVendor(context: Context, identifierForVendor: String) {
        synchronized(this) {
            this.identifierForVendor = identifierForVendor
            saveToSharedPrefs(context)
        }
    }

    fun setAdvertisingIdentifier(context: Context, advertisingIdentifier: String) {
        synchronized(this) {
            this.advertisingIdentifier = advertisingIdentifier
            saveToSharedPrefs(context)
        }
    }

    fun setLocal(context: Context, local: String) {
        synchronized(this) {
            this.local = local
            saveToSharedPrefs(context)
        }
    }

    fun setExVisitorId(context: Context, exVisitorId: String, isLogout: Boolean) {
        synchronized(this) {
            if (exVisitorId.isEmpty() && !isLogout) {
                Log.w(LOG_TAG, "exVisitorId cannot be empty!!")
                return
            }

            val previousExVisitorId = this.exVisitorId

            if (exVisitorId.isNotEmpty() && exVisitorId != previousExVisitorId &&
                previousExVisitorId.isNotEmpty()
            ) {
                setCookieId(context, null)
            }

            this.exVisitorId = exVisitorId

            if (previousExVisitorId.isNotEmpty() && previousExVisitorId != this.exVisitorId) {
                PersistentTargetManager.clearParameters(context)
            }
            saveToSharedPrefs(context)
        }
    }

    fun setUtmCampaign(context: Context, utmCampaign: String) {
        synchronized(this) {
            this.utmCampaign = utmCampaign
            saveToSharedPrefs(context)
        }
    }

fun setUtmContent(context: Context, utmContent: String) {
        synchronized(this) {
            this.utmContent = utmContent
            saveToSharedPrefs(context)
        }
    }

fun setUtmMedium(context: Context, utmMedium: String) {
        synchronized(this) {
            this.utmMedium = utmMedium
            saveToSharedPrefs(context)
        }
    }

fun setUtmSource(context: Context, utmSource: String) {
        synchronized(this) {
            this.utmSource = utmSource
            saveToSharedPrefs(context)
        }
    }

fun setUtmTerm(context: Context, utmTerm: String) {
        synchronized(this) {
            this.utmTerm = utmTerm
            saveToSharedPrefs(context)
        }
    }

    fun setToken(context: Context, token: String) {
        synchronized(this) {
            this.token = token
            saveToSharedPrefs(context)
        }
    }

    fun setCookieId(context: Context, cookieId: String?) {
        synchronized(this) {
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
    }

    fun setUserAgent(context: Context, userAgent: String) {
        synchronized(this) {
            this.userAgent = userAgent
            saveToSharedPrefs(context)
        }
    }

    fun setVisitorData(context: Context, visitorData: String) {
        synchronized(this) {
            this.visitorData = visitorData
            saveToSharedPrefs(context)
        }
    }

    fun setVisitData(context: Context, visitData: String) {
        synchronized(this) {
            this.visitData = visitData
        }
    }

    fun setCookie(context: Context, cookie: LoadBalanceCookie) {
        synchronized(this) {
            this.cookie = cookie
        }
    }

    fun add(context: Context, key: String, value: Any) {
        synchronized(this) {
            extra[key] = value
            saveToSharedPrefs(context)
        }
    }

    fun addWithoutSaving(key: String, value: Any) {
        synchronized(this) {
            extra[key] = value
        }
    }

    fun addAll(context: Context, extras: Map<String, Any>) {
        synchronized(this) {
            extra.putAll(extras)
            saveToSharedPrefs(context)
        }
    }

    fun removeAll(context: Context) {
        synchronized(this) {
            extra.clear()
            saveToSharedPrefs(context)
        }
    }

    fun remove(context: Context, key: String) {
        synchronized(this) {
            try {
                if (key.isNotEmpty()) {
                    extra.remove(key)
                    saveToSharedPrefs(context)
                } else {
                    Log.w("remove user property", "The key could not been found!")
                }
            } catch (e: Exception) {
                Log.w("remove user property", "something went wrong!")
            }
        }
    }

    fun getIsPushNotificationEnabled(): Boolean {
        synchronized(this) {
            return isPushNotificationEnabled
        }
    }

    fun getIsInAppNotificationEnabled(): Boolean {
        synchronized(this) {
            return isInAppNotificationEnabled
        }
    }

    fun getIsGeofenceEnabled(): Boolean {
        synchronized(this) {
            return isGeofenceEnabled
        }
    }

    fun getGoogleAppAlias(): String {
        synchronized(googleAppAliasLock) {
            return googleAppAlias
        }
    }

    fun getHuaweiAppAlias(): String {
        synchronized(huaweiAppAliasLock) {
            return huaweiAppAlias
        }
    }

    fun getOrganizationId(): String {
        synchronized(this) {
            return organizationId
        }
    }

    fun getProfileId(): String {
        synchronized(this) {
            return profileId
        }
    }

    fun getDataSource(): String {
        synchronized(this) {
            return dataSource
        }
    }

    fun getRequestTimeoutInSecond(): Int {
        synchronized(this) {
            return requestTimeoutInSecond
        }
    }

    fun getMaxGeofenceCount(): Int {
        synchronized(this) {
            return maxGeofenceCount
        }
    }

    fun getGeofencingIntervalInMinute(): Int {
        synchronized(this) {
            return geofencingIntervalInMinute
        }
    }

    fun getAppVersion(): String {
        synchronized(this) {
            return appVersion
        }
    }

    fun getPushPermissionStatus(): String {
        synchronized(this) {
            return pushPermissionStatus
        }
    }

    fun getApiVersion(): String {
        synchronized(this) {
            return apiVersion
        }
    }

    fun getOsType(): String {
        synchronized(this) {
            return osType
        }
    }

    fun getOsVersion(): String {
        synchronized(this) {
            return osVersion
        }
    }

    fun getSdkVersion(): String {
        synchronized(this) {
            return sdkVersion
        }
    }

    fun getSdkType(): String {
        synchronized(this) {
            return sdkType
        }
    }

    fun getDeviceType(): String {
        synchronized(this) {
            return deviceType
        }
    }

    fun getDeviceName(): String {
        synchronized(this) {
            return deviceName
        }
    }

    fun getCarrier(): String {
        synchronized(this) {
            return carrier
        }
    }

    fun getIdentifierForVendor(): String {
        synchronized(this) {
            return identifierForVendor
        }
    }

    fun getAdvertisingIdentifier(): String {
        synchronized(this) {
            return advertisingIdentifier
        }
    }

    fun getLocal(): String {
        synchronized(this) {
            return local
        }
    }

    fun getExVisitorId(): String {
        synchronized(this) {
            return exVisitorId
        }
    }

    fun getUtmCampaign(): String? {
        synchronized(this) {
            return utmCampaign
        }
    }

    fun getUtmContent(): String? {
        synchronized(this) {
            return utmContent
        }
    }

    fun getUtmMedium(): String? {
        synchronized(this) {
            return utmMedium
        }
    }

    fun getUtmSource(): String? {
        synchronized(this) {
            return utmSource
        }
    }

    fun getUtmTerm(): String? {
        synchronized(this) {
            return utmTerm
        }
    }

    fun getToken(): String {
        synchronized(this) {
            return token
        }
    }

    fun getCookieId(): String? {
        synchronized(this) {
            return cookieId
        }
    }

    fun getUserAgent(): String {
        synchronized(this) {
            return userAgent
        }
    }

    fun getVisitorData(): String {
        synchronized(this) {
            return visitorData
        }
    }

    fun getVisitData(): String {
        synchronized(this) {
            return visitData
        }
    }

    fun getCookie(): LoadBalanceCookie? {
        synchronized(this) {
            return cookie
        }
    }

    fun getExtra(): HashMap<String, Any> {
        synchronized(this) {
            return extra
        }
    }

    private fun saveToSharedPrefs(context: Context) {
        synchronized(this) {
            SharedPref.writeString(
                context,
                Constants.RELATED_DIGITAL_MODEL_KEY,
                Gson().toJson(this)
            )
        }
    }

    fun getFromSharedPref(context: Context): RelatedDigitalModel {
        synchronized(this) {
            return Gson().fromJson(
                SharedPref.readString(
                    context,
                    Constants.RELATED_DIGITAL_MODEL_KEY
                ), this::class.java
            )
        }
    }

    fun fill(model: RelatedDigitalModel) {
        synchronized(this) {
            if (model.getIsPushNotificationEnabled() != null) {
                this.isPushNotificationEnabled = model.getIsPushNotificationEnabled()
            }
            if (model.getIsInAppNotificationEnabled() != null) {
                this.isInAppNotificationEnabled = model.getIsInAppNotificationEnabled()
            }
            if (model.getIsGeofenceEnabled() != null) {
                this.isGeofenceEnabled = model.getIsGeofenceEnabled()
            }
            if (model.getGoogleAppAlias() != null) {
                this.googleAppAlias = model.getGoogleAppAlias()
            }
            if (model.getHuaweiAppAlias() != null) {
                this.huaweiAppAlias = model.getHuaweiAppAlias()
            }
            if (model.getOrganizationId() != null) {
                this.organizationId = model.getOrganizationId()
            }
            if (model.getProfileId() != null) {
                this.profileId = model.getProfileId()
            }
            if (model.getDataSource() != null) {
                this.dataSource = model.getDataSource()
            }
            if (model.getRequestTimeoutInSecond() != null) {
                this.requestTimeoutInSecond = model.getRequestTimeoutInSecond()
            }
            if (model.getMaxGeofenceCount() != null) {
                this.maxGeofenceCount = model.getMaxGeofenceCount()
            }
            if (model.getGeofencingIntervalInMinute() != null) {
                this.geofencingIntervalInMinute = model.getGeofencingIntervalInMinute()
            }
            if (model.getAdvertisingIdentifier() != null) {
                this.advertisingIdentifier = model.getAdvertisingIdentifier()
            }
            if (model.getExVisitorId() != null) {
                this.exVisitorId = model.getExVisitorId()
            }
            if (model.getUtmCampaign() != null) {
                this.utmCampaign = model.getUtmCampaign()
            }
            if (model.getUtmContent() != null) {
                this.utmContent = model.getUtmContent()
            }
            if (model.getUtmSource() != null) {
                this.utmSource= model.getUtmSource()
            }
            if (model.getUtmMedium() != null) {
                this.utmMedium = model.getUtmMedium()
            }
            if (model.getUtmTerm() != null) {
                this.utmTerm = model.getUtmTerm()
            }
            if (model.getToken() != null) {
                this.token = model.getToken()
            }
            if (model.getVisitorData() != null) {
                this.visitorData = model.getVisitorData()
            }
            if (!model.getExtra().isNullOrEmpty()) {
                this.extra.clear()
                this.extra.putAll(model.getExtra())
            }
        }
    }

    fun isValid(context: Context): Boolean {
        synchronized(this) {
            var appAlias = ""
            appAlias = if (GoogleUtils.checkPlayService(context)) {
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
    }

    fun isEqual(previousModel: RelatedDigitalModel?): Boolean {
        synchronized(this) {
            val result: Boolean = if (previousModel == null) {
                false
            } else {
                isStringEqual(googleAppAlias, previousModel.getGoogleAppAlias()) &&
                        isStringEqual(huaweiAppAlias, previousModel.getHuaweiAppAlias()) &&
                        isStringEqual(appVersion, previousModel.getAppVersion()) &&
                        isStringEqual(
                            pushPermissionStatus,
                            previousModel.getPushPermissionStatus()
                        ) &&
                        isStringEqual(apiVersion, previousModel.getApiVersion()) &&
                        isStringEqual(osType, previousModel.getOsType()) &&
                        isStringEqual(osVersion, previousModel.getOsVersion()) &&
                        isStringEqual(sdkVersion, previousModel.getSdkVersion()) &&
                        isStringEqual(sdkType, previousModel.getSdkType()) &&
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
    }

    private fun isStringEqual(first: String?, second: String?): Boolean {
        synchronized(this) {
            val result: Boolean = if (first == null || second == null) {
                first == null && second == null
            } else {
                first == second
            }
            return result
        }
    }

    private fun isMapEqual(first: Map<String, Any>, second: Map<String, Any>): Boolean {
        synchronized(this) {
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
    }

    fun copyFrom(context: Context, fromModel: RelatedDigitalModel) {
        synchronized(this) {
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
            geofencingIntervalInMinute = fromModel.getGeofencingIntervalInMinute()
            appVersion = if (fromModel.getAppVersion().isNullOrEmpty()) {
                AppUtils.getAppVersion(context)
            } else {
                fromModel.getAppVersion()
            }
            pushPermissionStatus = if (fromModel.getPushPermissionStatus().isNullOrEmpty()) {
                AppUtils.getNotificationPermissionStatus(context)
            } else {
                fromModel.getPushPermissionStatus()
            }
            apiVersion = fromModel.getApiVersion()
            osType = fromModel.getOsType()
            osVersion = if (fromModel.getOsVersion().isNullOrEmpty()) {
                AppUtils.getOsVersion()
            } else {
                fromModel.getOsVersion()
            }
            sdkVersion = if (fromModel.getSdkVersion().isNullOrEmpty()) {
                AppUtils.getSdkVersion()
            } else {
                fromModel.getSdkVersion()
            }

            sdkType = if (fromModel.getSdkType().isNullOrEmpty()) {
                AppUtils.getSdkType()
            } else {
                fromModel.getSdkType()
            }
            deviceType = if (fromModel.getDeviceType().isNullOrEmpty()) {
                AppUtils.getDeviceType()
            } else {
                fromModel.getDeviceType()
            }
            deviceName = if (fromModel.getDeviceName().isNullOrEmpty()) {
                AppUtils.getDeviceName()
            } else {
                fromModel.getDeviceName()
            }
            carrier = if (fromModel.getCarrier().isNullOrEmpty()) {
                AppUtils.getCarrier(context)
            } else {
                fromModel.getCarrier()
            }
            identifierForVendor = if (fromModel.getIdentifierForVendor().isNullOrEmpty()) {
                AppUtils.getIdentifierForVendor(context)
            } else {
                fromModel.getIdentifierForVendor()
            }
            advertisingIdentifier = fromModel.getAdvertisingIdentifier()
            local = if (fromModel.getLocal().isNullOrEmpty()) {
                AppUtils.getLocal(context)
            } else {
                fromModel.getLocal()
            }
            exVisitorId = fromModel.getExVisitorId()
            token = fromModel.getToken()
            cookieId = if (fromModel.getCookieId().isNullOrEmpty()) {
                AppUtils.getCookieId(context)
            } else {
                fromModel.getCookieId()
            }
            userAgent = if (fromModel.getUserAgent().isNullOrEmpty()) {
                AppUtils.getUserAgent()
            } else {
                fromModel.getUserAgent()
            }
            visitorData = if (fromModel.getVisitorData().isNullOrEmpty()) {
                ""
            } else {
                fromModel.getVisitorData()
            }
            visitData = if (fromModel.getVisitData().isNullOrEmpty()) {
                ""
            } else {
                fromModel.getVisitData()
            }
            cookie = if (fromModel.getCookie() == null) {
                null
            } else {
                fromModel.getCookie()
            }
            utmCampaign = if (fromModel.getUtmCampaign() == null) {
                null
            } else {
                fromModel.getUtmCampaign()
            }
            utmContent = if (fromModel.getUtmContent() == null) {
                null
            } else {
                fromModel.getUtmContent()
            }
            utmMedium = if (fromModel.getUtmMedium() == null) {
                null
            } else {
                fromModel.getUtmMedium()
            }
            utmTerm = if (fromModel.getUtmTerm() == null) {
                null
            } else {
                fromModel.getUtmTerm()
            }
            utmSource = if (fromModel.getUtmSource() == null) {
                null
            } else {
                fromModel.getUtmSource()
            }
            extra = HashMap()
            for (i in fromModel.getExtra().keys.toTypedArray().indices) {
                val key = fromModel.getExtra().keys.toTypedArray()[i]
                extra[key] = fromModel.getExtra()[key]!!
            }
        }
    }
}