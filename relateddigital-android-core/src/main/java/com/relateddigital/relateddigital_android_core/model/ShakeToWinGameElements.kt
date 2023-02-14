package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ShakeToWinGameElements : Serializable {
    @SerializedName("shaking_time" ) var shakingTime : Int?    = null
    @SerializedName("sound_url"    ) var soundUrl    : String? = null
    @SerializedName("video_url"    ) var videoUrl    : String? = null

}