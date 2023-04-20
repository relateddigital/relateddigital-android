package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GiftBoxExtendedPropsMailSubscriptionForm : Serializable {
    @SerializedName("title_text_color"      ) var titleTextColor      : String? = null
    @SerializedName("title_text_size"       ) var titleTextSize       : String? = null
    @SerializedName("text_color"            ) var textColor           : String? = null
    @SerializedName("text_size"             ) var textSize            : String? = null
    @SerializedName("button_color"          ) var buttonColor         : String? = null
    @SerializedName("button_text_color"     ) var buttonTextColor     : String? = null
    @SerializedName("button_text_size"      ) var buttonTextSize      : String? = null
    @SerializedName("emailpermit_text_size" ) var emailpermitTextSize : String? = null
    @SerializedName("emailpermit_text_url"  ) var emailpermitTextUrl  : String? = null
    @SerializedName("consent_text_size"     ) var consentTextSize     : String? = null
    @SerializedName("consent_text_url"      ) var consentTextUrl      : String? = null
}