package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class FindToWinExtendedPropsGameElements: Serializable {
    @SerializedName("scoreboard_shape")
    var scoreboardShape: String? = null

    @SerializedName("scoreboard_background_color")
    var scoreboardBackgroundColor: String? = null

    @SerializedName("scoreboard_pageposition")
    var scoreboardPagePosition: String? = null

    @SerializedName("backofcards_image")
    var backOfCardsImage: String? = null

    @SerializedName("backofcards_color")
    var backOfCardsColor: String? = null

    @SerializedName("blankcard_image")
    var blankCardImage: String? = null
}