package com.relateddigital.relateddigital_android.model

import android.content.Context
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.util.SharedPref
import java.io.Serializable

class RelatedDigitalModel(
        private var isPushNotificationEnabled: Boolean,
        private var isInAppNotificationEnabled: Boolean,
        private var isGeofenceEnabled: Boolean,
        private var googleAppAlias: String,
        private var huaweiAppAlias: String,
        private var organizationId: String,
        private var profileId: String,
        private var dataSource: String,
        private var requestTimeoutInSecond: Int,
        private var maxGeofenceCount: Int) : Serializable {

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