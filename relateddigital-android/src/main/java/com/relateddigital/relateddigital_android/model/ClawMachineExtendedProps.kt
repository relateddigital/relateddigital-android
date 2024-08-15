package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName

class ClawMachineExtendedProps {

    @SerializedName("background_image"                  ) var backgroundImage                : String?               = null
    @SerializedName("background_color"                  ) var backgroundColor                : String?               = null
    @SerializedName("font_family"                       ) var fontFamily                     : String?               = null
    @SerializedName("custom_font_family_ios"            ) var customFontFamilyIos            : String?               = null
    @SerializedName("custom_font_family_android"        ) var customFontFamilyAndroid        : String?               = null
    @SerializedName("close_button_color"                ) var closeButtonColor               : String?               = null
    @SerializedName("mail_subscription_form"            ) var mailSubscriptionForm           : ClawMachineExtendedPropsMailSubscriptionForm? = ClawMachineExtendedPropsMailSubscriptionForm()
    @SerializedName("gamification_rules"                ) var gamificationRules              : ClawMachineExtendedPropsGamificationRules?    = ClawMachineExtendedPropsGamificationRules()
    @SerializedName("game_elements"                     ) var gameElements                   : ClawMachineExtendedPropsGameElements?         = ClawMachineExtendedPropsGameElements()
    @SerializedName("game_result_elements"              ) var gameResultElements             : ClawMachineExtendedPropsGameResultElements?   = ClawMachineExtendedPropsGameResultElements()
    @SerializedName("promocode_background_color"        ) var promocodeBackgroundColor       : String?               = null
    @SerializedName("promocode_text_color"              ) var promocodeTextColor             : String?               = null
    @SerializedName("copybutton_color"                  ) var copybuttonColor                : String?               = null
    @SerializedName("copybutton_text_color"             ) var copybuttonTextColor            : String?               = null
    @SerializedName("copybutton_text_size"              ) var copybuttonTextSize             : String?               = null
    @SerializedName("promocode_banner_text"             ) var promocodeBannerText            : String?               = null
    @SerializedName("promocode_banner_text_color"       ) var promocodeBannerTextColor       : String?               = null
    @SerializedName("promocode_banner_background_color" ) var promocodeBannerBackgroundColor : String?               = null
    @SerializedName("promocode_banner_button_label"     ) var promocodeBannerButtonLabel     : String?               = null
}