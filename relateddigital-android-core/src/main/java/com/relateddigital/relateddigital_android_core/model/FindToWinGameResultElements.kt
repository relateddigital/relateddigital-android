package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class FindToWinGameResultElements: Serializable {
    @SerializedName("title")
    var title: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("lose_image")
    var loseImage: String? = null

    @SerializedName("lose_button_label")
    var loseButtonLabel: String? = null

    @SerializedName("lose_ios_lnk")
    var loseIosLink: String? = null

    @SerializedName("lose_android_lnk")
    var loseAndroidLink: String? = null
}