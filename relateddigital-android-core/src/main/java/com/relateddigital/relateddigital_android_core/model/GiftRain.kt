package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GiftRain : Serializable {
    var fontFiles = ArrayList<String>()

    @SerializedName("actid")
    var actid: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("actiontype")
    var actiontype: String? = null

    @SerializedName("actiondata")
    var actiondata: GiftRainActionData? = null
}