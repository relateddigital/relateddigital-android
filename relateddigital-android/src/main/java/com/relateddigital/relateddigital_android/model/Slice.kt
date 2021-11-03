package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Slice : Serializable {
    @SerializedName("displayName")
    var displayName: String? = null

    @SerializedName("color")
    var color: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("is_available")
    var isAvailable: Boolean? = null
}