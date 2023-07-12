package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class JackpotActionData : Serializable {

    @SerializedName("mail_subscription"      ) var mailSubscription     : Boolean?              = null
    @SerializedName("mail_subscription_form" ) var mailSubscriptionForm : JackpotMailSubscriptionForm? = JackpotMailSubscriptionForm()
    @SerializedName("gamification_rules"     ) var gamificationRules    : JackpotGamificationRules?    = JackpotGamificationRules()
    @SerializedName("game_elements"          ) var gameElements         : JackpotGameElements?         = JackpotGameElements()
    @SerializedName("game_result_elements"   ) var gameResultElements   : JackpotGameResultElements?   = JackpotGameResultElements()
    @SerializedName("copybutton_label"       ) var copybuttonLabel      : String?               = null
    @SerializedName("copybutton_function"    ) var copybuttonFunction   : String?               = null
    @SerializedName("ios_lnk"                ) var iosLink               : String?               = null
    @SerializedName("android_lnk"            ) var androidLink           : String?               = null
    @SerializedName("ExtendedProps"          ) var extendedProps        : String?               = null
    @SerializedName("report"                 ) var report               : JackpotReport?                = JackpotReport()
    @SerializedName("auth"                   ) var auth                 : String?               = null
    @SerializedName("type"                   ) var type                 : String?               = null


}