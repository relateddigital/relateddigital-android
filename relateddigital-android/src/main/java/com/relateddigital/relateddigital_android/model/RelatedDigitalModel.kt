package com.relateddigital.relateddigital_android.model

import android.content.Context
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.util.SharedPref
import java.io.Serializable

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
        private var osType: String,
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
        private var cookieId: String,
        private var userAgent: String,
        private var visitorData: String,
) : Serializable {

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

    fun setExVisitorId(context: Context, exVisitorId: String) {
        this.exVisitorId = exVisitorId
        saveToSharedPrefs(context)
    }

    fun setToken(context: Context, token: String) {
        this.token = token
        saveToSharedPrefs(context)
    }

    fun setCookieId(context: Context, cookieId: String) {
        this.cookieId = cookieId
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

    fun getCookieId(): String {
        return cookieId
    }

    fun getUserAgent(): String {
        return userAgent
    }

    fun getVisitorData(): String {
        return visitorData
    }

    fun saveToSharedPrefs(context: Context) {
        SharedPref.writeString(context,
                Constants.RELATED_DIGITAL_MODEL_KEY,
                Gson().toJson(this))
    }

    fun getFromSharedPref(context: Context) : RelatedDigitalModel{
        return Gson().fromJson(SharedPref.readString(context,
                Constants.RELATED_DIGITAL_MODEL_KEY), this::class.java)
    }
}