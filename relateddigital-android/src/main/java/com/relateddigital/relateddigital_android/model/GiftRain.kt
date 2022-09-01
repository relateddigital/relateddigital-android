package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GiftRain : Serializable {
    @SerializedName("actid")
    var actid: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("actiontype")
    var actiontype: String? = null

    @SerializedName("actiondata")
    var actiondata: GiftRainActionData? = null
}