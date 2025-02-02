package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SlotMachineActionData : Serializable {

    @SerializedName("mail_subscription"      ) var mailSubscription     : Boolean?              = null
    @SerializedName("mail_subscription_form" ) var mailSubscriptionForm : SlotMachineMailSubscriptionForm? = SlotMachineMailSubscriptionForm()
    @SerializedName("gamification_rules"     ) var gamificationRules    : SlotMachineGamificationRules?    = SlotMachineGamificationRules()
    @SerializedName("game_elements"          ) var gameElements         : SlotMachineGameElements?         = SlotMachineGameElements()
    @SerializedName("game_result_elements"   ) var gameResultElements   : SlotMachineGameResultElements?   = SlotMachineGameResultElements()
    @SerializedName("copybutton_label"       ) var copybuttonLabel      : String?               = null
    @SerializedName("copybutton_function"    ) var copybuttonFunction   : String?               = null
    @SerializedName("ios_lnk"                ) var iosLink               : String?               = null
    @SerializedName("android_lnk"            ) var androidLink           : String?               = null
    @SerializedName("ExtendedProps"          ) var extendedProps        : String?               = null
    @SerializedName("report"                 ) var report               : SlotMachineReport?                = SlotMachineReport()
    @SerializedName("auth"                   ) var auth                 : String?               = null
    @SerializedName("type"                   ) var type                 : String?               = null
    @SerializedName("waiting_time")
    var waitingTime: Int? = null

}