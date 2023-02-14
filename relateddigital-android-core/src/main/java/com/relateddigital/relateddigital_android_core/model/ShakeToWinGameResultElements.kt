package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ShakeToWinGameResultElements : Serializable {
    @SerializedName("title"   ) var title   : String? = null
    @SerializedName("message" ) var message : String? = null

}