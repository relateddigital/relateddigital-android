package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GiftRainGameResultElements: Serializable {
    @SerializedName("title")
    var title: String? = null

    @SerializedName("message")
    var message: String? = null
}