package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ClawMachineActionData : Serializable {
    @SerializedName("mail_subscription"      ) var mailSubscription     : Boolean?              = null
    @SerializedName("mail_subscription_form" ) var mailSubscriptionForm : ClawMachineMailSubscriptionForm? = null
    @SerializedName("gamification_rules"     ) var gamificationRules    : ClawMachineGamificationRules?    = null
    @SerializedName("game_elements"          ) var gameElements         : ClawMachineGameElements?         = null
    @SerializedName("game_result_elements"   ) var gameResultElements   : ClawMachineGameResultElements?   = null
    @SerializedName("copybutton_label"       ) var copybuttonLabel      : String?               = null
    @SerializedName("copybutton_function"    ) var copybuttonFunction   : String?               = null
    @SerializedName("ios_lnk"                ) var iosLnk               : String?               = null
    @SerializedName("android_lnk"            ) var androidLnk           : String?               = null
    @SerializedName("ExtendedProps"          ) var ExtendedProps        : String?               = null
    @SerializedName("report"                 ) var report               : ClawMachineReport?               =  null
    @SerializedName("auth"                   ) var auth                 : String?               = null
    @SerializedName("type"                   ) var type                 : String?               = null
}