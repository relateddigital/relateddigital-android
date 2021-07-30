package com.relateddigital.relateddigital_android.model

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.relateddigital.relateddigital_android.constants.Constants
import java.io.Serializable
import java.util.*

class Subscription : Serializable {
    @SerializedName("token")
    var token: String? = null

    @SerializedName("appVersion")
    var appVersion: String? = null

    @SerializedName("appKey")
    var appAlias: String? = null

    @SerializedName("os")
    var os: String? = null

    @SerializedName("osVersion")
    var osVersion: String? = null

    @SerializedName("deviceType")
    var deviceType: String? = null

    @SerializedName("deviceName")
    var deviceName: String? = null

    @SerializedName("carrier")
    var carrier: String? = null

    @SerializedName("local")
    var local: String? = null

    @SerializedName("identifierForVendor")
    var identifierForVendor: String? = null

    @SerializedName("advertisingIdentifier")
    var advertisingIdentifier: String? = null

    @SerializedName("sdkVersion")
    var sdkVersion: String? = null

    @SerializedName("firstTime")
    var firstTime = 0

    @SerializedName("extra")
    private var extra: MutableMap<String, Any?> = HashMap()

    fun add(key: String, value: Any?) {
        extra[key] = value
    }

    fun addAll(extras: Map<String, Any?>?) {
        extra.putAll(extras!!)
    }

    fun removeAll() {
        extra.clear()
    }

    val isValid: Boolean
        get() = !(TextUtils.isEmpty(token) && TextUtils.isEmpty(appAlias))

    fun isEqual(previousSubscription: Subscription?): Boolean {
        return if (previousSubscription == null) {
            false
        } else {
            isStringEqual(appVersion, previousSubscription.appVersion) &&
                    isStringEqual(appAlias, previousSubscription.appAlias) &&
                    isStringEqual(os, previousSubscription.os) &&
                    isStringEqual(osVersion, previousSubscription.osVersion) &&
                    isStringEqual(deviceType, previousSubscription.deviceType) &&
                    isStringEqual(deviceName, previousSubscription.deviceName) &&
                    isStringEqual(carrier, previousSubscription.carrier) &&
                    isStringEqual(local, previousSubscription.local) &&
                    isStringEqual(identifierForVendor, previousSubscription.identifierForVendor) &&
                    isStringEqual(
                        advertisingIdentifier,
                        previousSubscription.advertisingIdentifier
                    ) &&
                    isStringEqual(sdkVersion, previousSubscription.sdkVersion) &&
                    isStringEqual(
                        this.token,
                        previousSubscription.token
                    ) && firstTime == previousSubscription.firstTime &&
                    isMapEqual(extra, previousSubscription.getExtra())
        }
    }

    fun copyFrom(fromSubscription: Subscription) {
        appVersion = if (fromSubscription.appVersion == null) {
            null
        } else {
            fromSubscription.appVersion
        }
        appAlias = if (fromSubscription.appAlias == null) {
            null
        } else {
            fromSubscription.appAlias
        }
        os = if (fromSubscription.os == null) {
            null
        } else {
            fromSubscription.os
        }
        osVersion = if (fromSubscription.osVersion == null) {
            null
        } else {
            fromSubscription.osVersion
        }
        deviceType = if (fromSubscription.deviceType == null) {
            null
        } else {
            fromSubscription.deviceType
        }
        deviceName = if (fromSubscription.deviceName == null) {
            null
        } else {
            fromSubscription.deviceName
        }
        carrier = if (fromSubscription.carrier == null) {
            null
        } else {
            fromSubscription.carrier
        }
        local = if (fromSubscription.local == null) {
            null
        } else {
            fromSubscription.local
        }
        identifierForVendor = if (fromSubscription.identifierForVendor == null) {
            null
        } else {
            fromSubscription.identifierForVendor
        }
        advertisingIdentifier = if (fromSubscription.advertisingIdentifier == null) {
            null
        } else {
            fromSubscription.advertisingIdentifier
        }
        sdkVersion = if (fromSubscription.sdkVersion == null) {
            null
        } else {
            fromSubscription.sdkVersion
        }
        firstTime = fromSubscription.firstTime
        if (fromSubscription.token == null) {
            this.token = null
        } else {
            this.token = fromSubscription.token
        }
        extra = HashMap()
        for (i in fromSubscription.getExtra().keys.toTypedArray().indices) {
            val key = fromSubscription.getExtra().keys.toTypedArray()[i]
            if (fromSubscription.getExtra()[key] == null) {
                extra[key] = null
            } else {
                extra[key] = fromSubscription.getExtra()[key]
            }
        }
    }

    private fun getExtra(): Map<String, Any?> {
        return extra
    }

    fun setExtra(extra: MutableMap<String, Any?>) {
        this.extra = extra
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    private fun isStringEqual(first: String?, second: String?): Boolean {
        return if (first == null || second == null) {
            first == null && second == null
        } else {
            first == second
        }
    }

    private fun isMapEqual(first: Map<String, Any?>, second: Map<String, Any?>): Boolean {
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