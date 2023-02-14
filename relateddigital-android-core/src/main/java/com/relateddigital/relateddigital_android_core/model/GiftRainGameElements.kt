package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GiftRainGameElements: Serializable {
    @SerializedName("gift_images")
    var giftImages: List<String>? = null

    @SerializedName("gift_catcher_image")
    var giftCatcherImage: String? = null

    @SerializedName("number_of_products")
    var numberOfProducts: Int? = null

    @SerializedName("downward_speed")
    var downwardSpeed: String? = null

    @SerializedName("sound_url")
    var soundUrl: String? = null
}