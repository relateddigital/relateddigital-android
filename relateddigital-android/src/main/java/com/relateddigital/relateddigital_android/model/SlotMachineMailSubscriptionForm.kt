package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SlotMachineMailSubscriptionForm  : Serializable {
    @SerializedName("title"                 ) var title               : String? = null
    @SerializedName("message"               ) var message             : String? = null
    @SerializedName("placeholder"           ) var placeholder         : String? = null
    @SerializedName("button_label"          ) var buttonLabel         : String? = null
    @SerializedName("emailpermit_text"      ) var emailpermitText     : String? = null
    @SerializedName("consent_text"          ) var consentText         : String? = null
    @SerializedName("check_consent_message" ) var checkConsentMessage : String? = null
    @SerializedName("invalid_email_message" ) var invalidEmailMessage : String? = null
    @SerializedName("success_message"       ) var successMessage      : String? = null

}