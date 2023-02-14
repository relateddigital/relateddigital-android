package com.relateddigital.relateddigital_android_core.model

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.relateddigital.relateddigital_android_core.util.GoogleUtils
import java.io.Serializable
import java.util.*

class Subscription(context: Context, model: RelatedDigitalModel) : Serializable {
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

    init {
        token = model.getToken()
        appVersion = model.getAppVersion()
        appAlias = if(GoogleUtils.checkPlayService(context)) {
            model.getGoogleAppAlias()
        } else {
            model.getHuaweiAppAlias()
        }
        os = model.getOsType()
        osVersion = model.getOsVersion()
        deviceType = model.getDeviceType()
        deviceName = model.getDeviceName()
        carrier = model.getCarrier()
        local = model.getLocal()
        identifierForVendor = model.getIdentifierForVendor()
        advertisingIdentifier = model.getAdvertisingIdentifier()
        sdkVersion = model.getSdkVersion()
        extra.clear()
        extra.putAll(model.getExtra())
    }
}