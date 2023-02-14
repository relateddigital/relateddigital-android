package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.HashMap

class GraylogModel : Serializable {
    @SerializedName("logLevel")
    var logLevel = ""

    @SerializedName("logMessage")
    var logMessage = ""

    @SerializedName("logPlace")
    var logPlace = ""

    @SerializedName("googleAppAlias")
    var googleAppAlias = ""

    @SerializedName("huaweiAppAlias")
    var huaweiAppAlias = ""

    @SerializedName("iosAppAlias")
    var iosAppAlias = ""

    @SerializedName("token")
    var token = ""

    @SerializedName("appVersion")
    var appVersion = ""

    @SerializedName("sdkVersion")
    var sdkVersion = ""

    @SerializedName("osType")
    var osType = ""

    @SerializedName("osVersion")
    var osVersion = ""

    @SerializedName("deviceName")
    var deviceName = ""

    @SerializedName("userAgent")
    var userAgent = ""

    @SerializedName("identifierForVendor")
    var identifierForVendor = ""

    @SerializedName("extra")
    var extra: MutableMap<String, Any> = HashMap()
        set(extra) {
            this.extra.clear()
            this.extra.putAll(extra)
        }
}