package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ClawMachineExtendedPropsGameElements : Serializable {


    @SerializedName("catchbutton_color"      ) var catchbuttonColor     : String? = null
    @SerializedName("catchbutton_text_color" ) var catchbuttonTextColor : String? = null
    @SerializedName("catchbutton_text_size"  ) var catchbuttonTextSize  : String? = null
}