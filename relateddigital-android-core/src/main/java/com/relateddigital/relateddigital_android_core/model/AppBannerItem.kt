package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AppBannerItem: Serializable {
    @SerializedName("img")
    var image: String? = null

    @SerializedName("ios_lnk")
    var iosLink: String? = null

    @SerializedName("android_lnk")
    var androidLink: String? = null
}