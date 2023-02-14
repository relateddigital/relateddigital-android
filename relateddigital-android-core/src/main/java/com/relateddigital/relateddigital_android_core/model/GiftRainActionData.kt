package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GiftRainActionData : Serializable {
    @SerializedName("close_event_trigger")
    var closeEventTrigger: String? = null

    @SerializedName("mail_subscription")
    var isMailSubscription: Boolean? = null

    @SerializedName("mail_subscription_form")
    var mailSubscriptionForm: GiftRainMailSubscriptionForm? = null

    @SerializedName("gamification_rules")
    var gamificationRules: GiftRainGamificationRules? = null

    @SerializedName("game_elements")
    var gameElements: GiftRainGameElements? = null

    @SerializedName("game_result_elements")
    var gameResultElements: GiftRainGameResultElements? = null

    @SerializedName("copybutton_label")
    var copyButtonLabel: String? = null

    @SerializedName("copybutton_function")
    var copyButtonFunction: String? = null

    @SerializedName("ios_lnk")
    var iosLink: String? = null

    @SerializedName("android_lnk")
    var androidLink: String? = null

    @SerializedName("promo_codes")
    var promoCodes: List<GiftRainPromoCode>? = null

    @SerializedName("ExtendedProps")
    var extendedProps: String? = null

    @SerializedName("report")
    var report: GiftRainReport? = null

    @SerializedName("auth")
    var auth: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("waiting_time")
    var waitingTime: Int? = null

    @SerializedName("after")
    var after: Boolean? = null
}