package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Actions : Serializable {

    @SerializedName("Title"        ) var Title        : String? = null
    @SerializedName("Action"       ) var Action       : String? = null
    @SerializedName("Icon"         ) var Icon         : String? = null
    @SerializedName("Url"          ) var Url          : String? = null
    @SerializedName("AlternateUrl" ) var AlternateUrl : String? = null
}