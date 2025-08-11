package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName

class NotificationBellTexts {
    @SerializedName("text")
    var text: String? = null

    @SerializedName("ios_lnk")
    var ios_lnk: String? = null

    @SerializedName("android_lnk")
    var android_lnk: String? = null
}