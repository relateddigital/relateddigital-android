package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ChooseFavoritedActionData : Serializable {

    @SerializedName("mail_subscription")
    var mailSubscription: Boolean? = null

    @SerializedName("mail_subscription_form")
    var mailSubscriptionForm: ChooseFavoritedMailSubscriptionForm? = null

    @SerializedName("gamification_rules")
    var gamificationRules: ChooseFavoritedGamificationRules? = null

    @SerializedName("game_elements")
    var gameElements: ChooseFavoritedGameElements? = null

    @SerializedName("game_result_elements")
    var gameResultElements: ChooseFavoritedGameResultElements? = null

    @SerializedName("copybutton_label")
    var copyButtonLabel: String? = null

    @SerializedName("copybutton_function")
    var copyButtonFunction: String? = null

    @SerializedName("ios_lnk")
    var iosLink: String? = null

    @SerializedName("android_lnk")
    var androidLink: String? = null

    @SerializedName("promo_codes")
    var promoCodes: List<ChooseFavoritedPromoCode?>? = null

    @SerializedName("ExtendedProps")
    var extendedProps: String? = null

    @SerializedName("report")
    var report: ChooseFavoritedReport? = null

    @SerializedName("auth")
    var auth: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("waiting_time")
    var waitingTime: Int? = null
}