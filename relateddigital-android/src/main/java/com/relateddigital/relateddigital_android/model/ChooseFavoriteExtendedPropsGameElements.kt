package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ChooseFavoriteExtendedPropsGameElements  : Serializable {
    @SerializedName("likebutton_color"              ) var likebuttonColor             : String? = null
    @SerializedName("likebutton_background_color"   ) var likebuttonBackgroundColor   : String? = null
    @SerializedName("ignorebutton_color"            ) var ignorebuttonColor           : String? = null
    @SerializedName("ignorebutton_background_color" ) var ignorebuttonBackgroundColor : String? = null
}