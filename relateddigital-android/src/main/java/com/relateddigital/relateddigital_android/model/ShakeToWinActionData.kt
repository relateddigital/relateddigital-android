package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ShakeToWinActionData : Serializable {
    @SerializedName("mail_subscription"      ) var mailSubscription     : Boolean?              = null
    @SerializedName("mail_subscription_form" ) var mailSubscriptionForm : ShakeToWinMailSubscriptionForm? = ShakeToWinMailSubscriptionForm()
    @SerializedName("gamification_rules"     ) var gamificationRules    : ShakeToWinGamificationRules?    = ShakeToWinGamificationRules()
    @SerializedName("game_elements"          ) var gameElements         : ShakeToWinGameElements?         = ShakeToWinGameElements()
    @SerializedName("game_result_elements"   ) var gameResultElements   : ShakeToWinGameResultElements?   = ShakeToWinGameResultElements()
    @SerializedName("copybutton_label"       ) var copybuttonLabel      : String?               = null
    @SerializedName("copybutton_function"    ) var copybuttonFunction   : String?               = null
    @SerializedName("ios_lnk"                ) var iosLnk               : String?               = null
    @SerializedName("android_lnk"            ) var androidLnk           : String?               = null
    @SerializedName("ExtendedProps"          ) var ExtendedProps        : String?               = null
    @SerializedName("cid"                    ) var cid                  : String?               = null
    @SerializedName("staticcode"             ) var staticcode           : String?               = null
    @SerializedName("courseofaction"         ) var courseofaction       : String?               = null

}