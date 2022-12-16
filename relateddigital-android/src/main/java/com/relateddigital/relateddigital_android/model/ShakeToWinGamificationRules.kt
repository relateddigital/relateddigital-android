package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ShakeToWinGamificationRules : Serializable {
    @SerializedName("background_image" ) var backgroundImage : String? = null
    @SerializedName("button_label"     ) var buttonLabel     : String? = null
}