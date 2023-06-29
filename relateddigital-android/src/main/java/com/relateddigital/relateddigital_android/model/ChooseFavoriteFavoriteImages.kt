package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ChooseFavoriteFavoriteImages : Serializable {
    @SerializedName("image"              ) var image             : String? = null
    @SerializedName("staticcode"         ) var staticcode        : String? = null
    @SerializedName("ios_lnk"            ) var iosLnk            : String? = null
    @SerializedName("android_lnk"        ) var androidLnk        : String? = null
    @SerializedName("result_displaytext" ) var resultDisplaytext : String? = null

}