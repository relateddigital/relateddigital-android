package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CustomActionsExtendedProps  : Serializable {

    @SerializedName("position"           ) var position         : String? = null
    @SerializedName("width"              ) var width            : Int?    = null
    @SerializedName("height"             ) var height           : Int?    = null
    @SerializedName("close_button_color" ) var closeButtonColor : String? = null
    @SerializedName("border_radius"      ) var borderRadius     : Int?    = null
}