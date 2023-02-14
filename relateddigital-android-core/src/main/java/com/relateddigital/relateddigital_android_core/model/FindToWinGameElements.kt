package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class FindToWinGameElements: Serializable {
    @SerializedName("card_images")
    var cardImages: List<String>? = null

    @SerializedName("playground_rowcount")
    var playgroundRowCount: Int? = null

    @SerializedName("playground_columncount")
    var playgroundColumnCount: Int? = null

    @SerializedName("duration_of_game")
    var durationOfGame: Int? = null

    @SerializedName("sound_url")
    var soundUrl: String? = null
}