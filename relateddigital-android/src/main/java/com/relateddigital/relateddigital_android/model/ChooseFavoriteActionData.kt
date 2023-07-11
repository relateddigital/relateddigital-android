package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ChooseFavoriteActionData : Serializable {
    @SerializedName("mail_subscription"      ) var mailSubscription     : Boolean?              = null
    @SerializedName("mail_subscription_form" ) var mailSubscriptionForm : ChooseFavoriteMailSubscriptionForm? = null
    @SerializedName("gamification_rules"     ) var gamificationRules    : ChooseFavoriteGamificationRules?    = null
    @SerializedName("game_elements"          ) var gameElements         : ChooseFavoriteGameElements?         = null
    @SerializedName("game_result_elements"   ) var gameResultElements   : ChooseFavoriteGameResultElements?   = null
    @SerializedName("copybutton_label"       ) var copybuttonLabel      : String?               = null
    @SerializedName("copybutton_function"    ) var copybuttonFunction   : String?               = null
    @SerializedName("ExtendedProps"          ) var ExtendedProps        : String?               = null
    @SerializedName("auth"                   ) var auth                 : String?               = null
    @SerializedName("type"                   ) var type                 : String?               = null
    @SerializedName("report"                 ) var report               : ChooseFavoriteReport? = null
}