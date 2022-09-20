package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GiftRainExtendedPropsGameElements: Serializable {
    @SerializedName("scoreboard_shape") var scoreboardShape: String? = null
    @SerializedName("scoreboard_background_color") var scoreboardBackgroundColor: String? = null
}