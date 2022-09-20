package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class FindToWinActionData: Serializable {

    @SerializedName("mail_subscription")
    var mailSubscription: Boolean? = null

    @SerializedName("mail_subscription_form")
    var mailSubscriptionForm: FindToWinMailSubscriptionForm? = null

    @SerializedName("gamification_rules")
    var gamificationRules: FindToWinGamificationRules? = null

    @SerializedName("game_elements")
    var gameElements: FindToWinGameElements? = null

    @SerializedName("game_result_elements")
    var gameResultElements: FindToWinGameResultElements? = null

    @SerializedName("copybutton_label")
    var copyButtonLabel: String? = null

    @SerializedName("copybutton_function")
    var copyButtonFunction: String? = null

    @SerializedName("ios_lnk")
    var iosLink: String? = null

    @SerializedName("android_lnk")
    var androidLink: String? = null

    @SerializedName("promo_codes")
    var promoCodes: List<FindToWinPromoCode?>? = null

    @SerializedName("ExtendedProps")
    var extendedProps: String? = null

    @SerializedName("report")
    var report: FindToWinReport? = null

    @SerializedName("auth")
    var auth: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("waiting_time")
    var waitingTime: Int? = null
}