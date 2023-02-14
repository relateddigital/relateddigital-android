package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ShakeToWinReport : Serializable {
    @SerializedName("impression" ) var impression : String? = null
    @SerializedName("click"      ) var click      : String? = null
}