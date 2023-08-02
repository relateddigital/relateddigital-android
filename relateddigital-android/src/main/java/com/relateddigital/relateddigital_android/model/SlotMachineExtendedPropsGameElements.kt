package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SlotMachineExtendedPropsGameElements : Serializable {

    @SerializedName("spinbutton_color"      ) var spinbuttonColor     : String? = null
    @SerializedName("spinbutton_text_color" ) var spinbuttonTextColor : String? = null
    @SerializedName("spinbutton_text_size"  ) var spinbuttonTextSize  : String? = null
}